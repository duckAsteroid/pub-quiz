package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.model.ask.Round;
import com.asteroid.duck.pubquiz.rest.socket.Channel;
import com.asteroid.duck.pubquiz.rest.socket.events.SessionEvent;
import com.asteroid.duck.pubquiz.util.QuizName;
import com.asteroid.duck.pubquiz.model.QuestionId;
import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.model.answer.Submission;
import com.asteroid.duck.pubquiz.model.answer.SubmittedAnswer;
import com.asteroid.duck.pubquiz.model.ask.Question;
import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import com.asteroid.duck.pubquiz.repo.SubmissionRepository;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static com.asteroid.duck.pubquiz.util.KeyVerifier.verify;

@RestController
@RequestMapping("/rest/sessions")
public class SessionController {

    private Random random = new Random();
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private SimpMessagingTemplate webSocket;

    /**
     * Creates and starts a new quiz session
     * @param host the name of the host
     * @param quizId the quiz to start
     * @return a session (with a short ID) that teams can join
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public QuizSession startQuiz( @RequestHeader("host") String host, @RequestHeader("quizId") String quizId) {
        // check the quiz ID exists
        Quiz quiz = quizRepository.findById(new ObjectId(quizId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String shortName;
        do {
            shortName = QuizName.newName();
        } while(sessionRepository.countAllByShortId(shortName) > 0);
        // build a session
        QuizSession session = QuizSession.builder().host(host)
                .shortId(shortName)
                .quizId(quiz.getId().toHexString())
                // this random number is required as a key for the host
                .hostKey(Long.toHexString(random.nextLong()))
                .build();
        // save the session
        QuizSession savedSession = sessionRepository.save(session);
        // notify listeners a new session started
        //webSocket.convertAndSend(Channel.SESSIONS, savedSession.getShortId());
        return savedSession;
    }

    /**
     * Get a currently running session
     * @param sessionId the short ID of the session
     * @return the session (and it's current teams)
     */
    private QuizSession getSession(String sessionId) {
        Optional<QuizSession> session = sessionRepository.findByShortId(sessionId);
        return session.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session for ID "+session));
    }

    /**
     * Delete a session and all associated resources
     * @param sessionId short ID of session
     */
    @RequestMapping(value = "/{session}", method = RequestMethod.DELETE)
    public void deleteSession(@PathVariable("session") String sessionId, @RequestParam("key") String hostKey) {
        QuizSession session = getSession(sessionId);
        if (session.getHostKey().equals(hostKey)) {
            List<Team> teams = session.getTeams();
            sessionRepository.deleteAllByShortId(sessionId);
            submissionRepository.deleteAllByQuizSession(sessionId);
            // FIXME notify teams via socket
            webSocket.convertAndSend(Channel.session(sessionId), SessionEvent.builder().action(SessionEvent.Action.SESSION_ENDED).build());
        }
        else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid host key");
    }

    @Data
    public static class RoundCarrier {
        private final String title;
        private final int questionCount;

        public RoundCarrier(Round r) {
            this.title = r.getTitle();
            this.questionCount = Optional.ofNullable(r.getQuestions()).orElse(Collections.emptyList()).size();
        }

        public static RoundCarrier from(Round r) {
            return new RoundCarrier(r);
        }
    }

    @RequestMapping("/{session}/rounds")
    public List<RoundCarrier> getRounds(@PathVariable("session") String sessionId) {
        QuizSession session = getSession(sessionId);
        Optional<Quiz> found = quizRepository.findById(new ObjectId(session.getQuizId()));
        Quiz quiz = found.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz for ID " + session.getQuizId()));
        return quiz.getRounds().stream()
                .map(RoundCarrier::from)
                .collect(Collectors.toList());
    }

    /**
     * Get the current question in the session
     * @param sessionId
     * @return
     */
    @RequestMapping("/{session}/questions/current")
    public QuestionId currentQuestion(@PathVariable("session") String sessionId) {
        QuizSession session = getSession(sessionId);
        return session.getCurrentQuestion();
    }

    /**
     * Advance to the next question
     * @param sessionId the session short ID
     * @param key the host secure key
     * @return
     */
    @RequestMapping(value = "/{session}/questions/next", method = RequestMethod.PUT)
    public Question nextQuestion(@PathVariable("session") String sessionId, @RequestParam("key") Optional<String> key) {
        String actualKey = verify(key);
        QuizSession session = getSession(sessionId);
        verify(actualKey, session);
        Optional<Quiz> found = quizRepository.findById(new ObjectId(session.getQuizId()));
        Quiz quiz = found.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz for ID " + session.getQuizId()));
        QuestionId.QuizIterator iterator = new QuestionId.QuizIterator(quiz, session.getCurrentQuestion());
        if (iterator.hasNext()) {
            QuestionId nextQuestion = iterator.next();
            session.setCurrentQuestion(nextQuestion);
            sessionRepository.save(session);
            webSocket.convertAndSend(Channel.session( sessionId), nextQuestion);
            return quiz.getById(nextQuestion);
        } else {
            session.setCurrentQuestion(null);
            sessionRepository.save(session);
            webSocket.convertAndSend(Channel.session( sessionId), "GAME OVER");
            // FIXME Log error - no next question
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
        }
    }

    /**
     * Submit an answer
     * @param sessionId
     * @param roundId
     * @param questionId
     * @param teamId
     * @param answerContent
     */
    @RequestMapping(value = "/{session}/rounds/{roundId}/questions/{questionId}", method = RequestMethod.POST)
    public void submitAnswer(@PathVariable("session") String sessionId, @PathVariable("roundId") Integer roundId, @PathVariable("questionId") Integer questionId,
                             @RequestParam("teamId") Integer teamId, @RequestBody String answerContent) {
        QuizSession session = getSession(sessionId);
        if (teamId < 0 || teamId > session.getTeams().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such team id="+teamId);
        }
        Team team = session.getTeams().get(teamId);
        Optional<Submission> repoSub = submissionRepository.findByQuizSessionAndTeam(sessionId, team);
        Submission submission = repoSub.orElseGet(() -> Submission.builder().quizSession(sessionId).team(team).build());
        QuestionId id = QuestionId.builder().round(roundId).question(questionId).build();
        SubmittedAnswer answer = SubmittedAnswer.builder().answer(answerContent).build();
        submission.getAnswers().put(id, answer);
        submissionRepository.save(submission);
        //webSocket.convertAndSend(Channel.SUBMISSION, SubmissionEvent.builder().questionId(id).team(team).build());
    }

}
