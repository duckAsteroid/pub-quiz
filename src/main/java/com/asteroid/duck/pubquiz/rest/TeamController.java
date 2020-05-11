package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import com.asteroid.duck.pubquiz.rest.socket.Channel;
import com.asteroid.duck.pubquiz.rest.socket.events.TeamEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.asteroid.duck.pubquiz.util.KeyVerifier.verify;

@RestController
@RequestMapping("/rest")
public class TeamController {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SimpMessagingTemplate webSocket;

    private QuizSession findSession(String sessionId) {
        return sessionRepository.findByShortId(sessionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session ID="+sessionId));
    }

    private Team findTeam(String sessionId, String teamId) {
        QuizSession session = findSession(sessionId);
        return session.getTeams().stream()
                .filter(team -> team.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team with ID=" + teamId));

    }

    /**
     * Get this list of teams in this session
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "/sessions/{session}/teams")
    public List<Team> getTeams(@PathVariable("session") String sessionId) {
        QuizSession session = findSession(sessionId);
        return session.getTeams();
    }


    /**
     * Get a specific team
     * @param sessionId
     * @param teamId
     * @return
     */
    @GetMapping(value = "/sessions/{session}/teams/{teamId}")
    public Team read(@PathVariable("session") String sessionId, @PathVariable("teamId") String teamId) {
        return findTeam(sessionId, teamId);
    }

    @RequestMapping(value = "/sessions/{session}/teams/{teamId}", method = RequestMethod.DELETE)
    public boolean removeTeam(@PathVariable("session") String sessionId, @PathVariable("teamId") String teamId, @RequestParam("key") Optional<String> key) {
        String actualKey = verify(key);
        QuizSession session = findSession(sessionId);
        verify(actualKey, session);
        Team team = findTeam(sessionId, teamId);
        boolean success = session.getTeams().remove(team);
        sessionRepository.save(session);
        webSocket.convertAndSend(Channel.teams(sessionId), TeamEvent.builder().team(team).operation(TeamEvent.OP_LEFT).build());
        return success;
    }


}
