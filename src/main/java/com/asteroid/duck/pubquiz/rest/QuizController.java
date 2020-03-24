package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.QuizName;
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
import com.asteroid.duck.pubquiz.rest.events.SubmissionEvent;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
public class QuizController {

    private Random random = new Random();
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private SimpMessagingTemplate webSocket;

    //region Session actions
    /**
     * Creates and starts a new quiz session
     * @param quiz the quiz to start
     * @param host the name of the host
     * @return a session (with a short ID) that teams can join
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public QuizSession startQuiz( @RequestHeader("host") String host, Quiz quiz) {
        Quiz savedQuiz = quizRepository.save(quiz);
        String shortName;
        do {
            shortName = QuizName.newName();
        } while(sessionRepository.countAllByShortId(shortName) > 0);

        QuizSession session = QuizSession.builder().host(host)
                .shortId(shortName)
                .quizId(savedQuiz.getId().toHexString())
                .hostKey(Long.toHexString(random.nextLong()))
                .build();
        QuizSession savedSession = sessionRepository.save(session);
        webSocket.convertAndSend(Channel.SESSIONS, savedSession.getShortId());
        return savedSession;
    }

    /**
     * Get a currently running session
     * @param sessionId the short ID of the session
     * @return the session (and it's current teams)
     */
    public QuizSession getSession(String sessionId) {
        Optional<QuizSession> session = sessionRepository.findByShortId(sessionId);
        return session.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session for ID "+session));
    }

    /**
     * Delete a session and all associated resources
     * @param sessionId short ID of session
     */
    @RequestMapping(value = "/sessions/{session}", method = RequestMethod.DELETE)
    public void deleteSession(@PathVariable("session") String sessionId, @RequestParam("key") String hostKey) {
        QuizSession session = getSession(sessionId);
        if (session.getHostKey().equals(hostKey)) {
            sessionRepository.deleteAllByShortId(sessionId);
            submissionRepository.deleteAllByQuizSession(sessionId);
        }
        else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid host key");
    }
    //endregion

    //region Team actions
    /**
     * Get this list of teams in this session
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "/sessions/{session}/teams")
    public List<Team> getTeams(@PathVariable("session") String sessionId) {
        QuizSession session = getSession(sessionId);
        return session.getTeams();
    }

    /**
     * Get a specific team
     * @param sessionId
     * @param teamId
     * @return
     */
    @RequestMapping(value = "/sessions/{session}/teams/{teamId}")
    public Team getTeam(@PathVariable("session") String sessionId, @PathVariable("teamId") Integer teamId) {
        QuizSession session = getSession(sessionId);
        return session.getTeams().get(teamId);
    }

    /**
     * Create a new team in a session
     * @param sessionId
     * @param name
     * @return
     */
    @RequestMapping(value = "/sessions/{session}/teams/new", method = RequestMethod.POST)
    public Team newTeam(@PathVariable("session") String sessionId, @RequestParam("name") String name) {
        Team team = Team.builder().name(name).build();
        QuizSession session = getSession(sessionId);
        List<Team> teams = session.getTeams();
        if (teams.contains(team)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name already taken");
        teams.add(team);
        sessionRepository.save(session);
        webSocket.convertAndSend(Channel.TEAM, team);
        return team;
    }
    //endregion

    /**
     * Get a question
     * @param sessionId
     * @param roundId
     * @param questionId
     * @param showAnswer
     * @return
     */
    @RequestMapping("/sessions/{session}/rounds/{roundId}/questions/{questionId}")
    public Question getQuestion(@PathVariable("session") String sessionId, @PathVariable("roundId") Integer roundId, @PathVariable("questionId") Integer questionId, @RequestParam(value = "showAnswer") Optional<Boolean> showAnswer) {
        QuizSession session = getSession(sessionId);
        Optional<Quiz> found = quizRepository.findById(new ObjectId(session.getQuizId()));
        Quiz quiz = found.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz for ID " + session.getQuizId()));
        QuestionId id = QuestionId.builder().round(roundId).question(questionId).build();
        Question question = quiz.getById(id);
        // suppress answer
        if (!showAnswer.orElse(false)) {
            question.setCorrectAnswer("CHEAT!");
        }
        return question;
    }

    /**
     * Get the current question in the session
     * @param sessionId
     * @param showAnswer
     * @return
     */
    @RequestMapping("/sessions/{session}/questions/current")
    public Question currentQuestion(@PathVariable("session") String sessionId, @RequestParam(value = "showAnswer") Optional<Boolean> showAnswer) {
        QuizSession session = getSession(sessionId);
        Optional<Quiz> found = quizRepository.findById(new ObjectId(session.getQuizId()));
        Quiz quiz = found.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz for ID " + session.getQuizId()));
        Question question = quiz.getById(session.getCurrentQuestion());
        // suppress answer
        if (!showAnswer.orElse(false)) {
            question.setCorrectAnswer("CHEAT!");
        }
        return question;
    }

    /**
     * Advance to the next question
     * @param sessionId the session short ID
     * @param showAnswer show the answer in the response or mask it
     * @param key the host secure key
     * @return
     */
    @RequestMapping("/sessions/{session}/questions/next")
    public Question nextQuestion(@PathVariable("session") String sessionId, @RequestParam(value = "showAnswer") Optional<Boolean> showAnswer, @RequestParam("key") String key) {
        QuizSession session = getSession(sessionId);
        if (!session.getHostKey().equals(key)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid host key");
        Optional<Quiz> found = quizRepository.findById(new ObjectId(session.getQuizId()));
        Quiz quiz = found.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz for ID " + session.getQuizId()));
        QuestionId.QuizIterator iterator = new QuestionId.QuizIterator(quiz, session.getCurrentQuestion());
        if (iterator.hasNext()) {
            QuestionId nextQuestion = iterator.next();
            session.setCurrentQuestion(nextQuestion);
            sessionRepository.save(session);
            webSocket.convertAndSend(Channel.SESSIONS + "/" + sessionId, nextQuestion.toString());
            return quiz.getById(nextQuestion);
        } else {
            session.setCurrentQuestion(null);
            sessionRepository.save(session);
            webSocket.convertAndSend(Channel.SESSIONS + "/" + sessionId, "GAME OVER");
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
    @RequestMapping(value = "/sessions/{session}/rounds/{roundId}/questions/{questionId}", method = RequestMethod.POST)
    public void submitAnswer(@PathVariable("session") String sessionId, @PathVariable("roundId") Integer roundId, @PathVariable("questionId") Integer questionId,
                             @RequestParam("teamId") Integer teamId, @RequestBody String answerContent) {
        Team team = getTeam(sessionId, teamId);
        Optional<Submission> repoSub = submissionRepository.findByQuizSessionAndTeam(sessionId, team);
        Submission submission = repoSub.orElseGet(() -> Submission.builder().quizSession(sessionId).team(team).build());
        QuestionId id = QuestionId.builder().round(roundId).question(questionId).build();
        SubmittedAnswer answer = SubmittedAnswer.builder().answer(answerContent).build();
        submission.getAnswers().put(id, answer);
        submissionRepository.save(submission);
        webSocket.convertAndSend(Channel.SUBMISSION, SubmissionEvent.builder().questionId(id).team(team).build());
    }

}
