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
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
public class QuizController {
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public QuizSession startQuiz(@RequestBody Quiz quiz, @RequestParam("host") String host) {
        Quiz savedQuiz = quizRepository.save(quiz);
        QuizSession session = QuizSession.builder().host(host)
                .shortId(QuizName.newName())
                .quizId(savedQuiz.getId().toHexString())
                .build();
        QuizSession savedSession = sessionRepository.save(session);
        return savedSession;
    }

    @RequestMapping("/sessions/{session}")
    public QuizSession getSession(@PathVariable("session") String sessionId) {
        Optional<QuizSession> session = sessionRepository.findByShortId(sessionId);
        return session.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session for ID "+session));
    }

    @RequestMapping(value = "/sessions/{session}/teams")
    public List<Team> getTeams(@PathVariable("session") String sessionId) {
        QuizSession session = getSession(sessionId);
        return session.getTeams();
    }

    @RequestMapping(value = "/sessions/{session}/teams/{teamId}")
    public Team getTeam(@PathVariable("session") String sessionId, @PathVariable("teamId") Integer teamId) {
        QuizSession session = getSession(sessionId);
        return session.getTeams().get(teamId);
    }

    @RequestMapping(value = "/sessions/{session}/teams/new", method = RequestMethod.POST)
    public Team newTeam(@PathVariable("session") String sessionId, @RequestParam("name") String name) {
        Team team = Team.builder().name(name).build();
        QuizSession session = getSession(sessionId);
        List<Team> teams = session.getTeams();
        if (teams.contains(team)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name already taken");
        teams.add(team);
        sessionRepository.save(session);
        return team;
    }

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
    }
}
