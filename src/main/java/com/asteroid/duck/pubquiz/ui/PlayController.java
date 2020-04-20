package com.asteroid.duck.pubquiz.ui;


import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Controller for quiz responses
 */
@Controller
@RequestMapping("/ui/quiz")
public class PlayController {
    @Autowired
    private SessionRepository sessionRepository;

    private QuizSession getSession(String quizId) {
        return sessionRepository.findByShortId(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such quiz with ID="+quizId));
    }

    @GetMapping("{quizId}/play.html")
    public String getQuiz(@PathVariable("quizId") String quizId, Model model) {
        QuizSession quiz = getSession(quizId);
        model.addAttribute("team", Team.builder().build());
        model.addAttribute("teams", quiz.getTeams());
        return "play/team";

    }

    @PostMapping("{quizId}/enterteam")
    public String enterTeam(@PathVariable("quizId") String quizId, @ModelAttribute Team team) {
        QuizSession quiz = getSession(quizId);

        return "play/answer";
    }
}
