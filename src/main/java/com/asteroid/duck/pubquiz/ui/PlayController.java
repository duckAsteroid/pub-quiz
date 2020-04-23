package com.asteroid.duck.pubquiz.ui;


import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Controller for quiz responses
 */
@Controller
@RequestMapping("/ui/quiz")
public class PlayController {

    private final Random random = new Random();

    private static final List<Long> EMOJIS = emojiRange();

    @Autowired
    private SessionRepository sessionRepository;

    private QuizSession getSession(String quizId) {
        return sessionRepository.findByShortId(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No such quiz with ID="+quizId));
    }

    @GetMapping("{quizId}/play.html")
    public String getQuiz(@PathVariable("quizId") String quizId, Model model) {
        QuizSession quiz = getSession(quizId);
        model.addAttribute("team", Team.builder().name("").build());
        model.addAttribute("teams", quiz.getTeams());
        model.addAttribute("emojis", EMOJIS);
        return "play/team";

    }

    private static List<Long> emojiRange() {
        try {
            List<Long> emojis = IOUtils.readLines(PlayController.class.getResourceAsStream("/emoji.codes"), StandardCharsets.UTF_8)
                    .stream().map(s -> s.split(","))
                    .map(sarr -> Long.parseLong(sarr[1]))
                    .collect(Collectors.toList());
            return emojis;
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @PostMapping("{quizId}/enterteam")
    public RedirectView enterTeam(@PathVariable("quizId") String quizId, @ModelAttribute("team") Team team) {
        QuizSession session = getSession(quizId);
        Optional<Team> existing = session.getTeamNamed(team.getName());
        if (!existing.isPresent()) {
            team.setId(random.nextLong());
            session.getTeams().add(team);
            sessionRepository.save(session);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name in use");
        }
        RedirectView rv = new RedirectView();
        rv.setContextRelative(true);
        rv.setUrl("teams/"+Long.toHexString(team.getId())+"/answer.html");
        return rv;
    }

    @GetMapping("{quizId}/teams/{teamId}/answer.html")
    public String teamPage(@PathVariable("quizId") String sessionId, @PathVariable("teamId") String teamId, Model model) {
        QuizSession session = getSession(sessionId);
        model.addAttribute("quizSession", session);
        try {
            long teamIdLong = Long.parseUnsignedLong(teamId, 16);
            Team team = session.getTeamById(teamIdLong).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team with ID="+teamId));
            model.addAttribute("team", team);
            return "play/answer";
        }
        catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Illegal team ID", e);
        }
    }
}
