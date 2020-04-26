package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import com.asteroid.duck.pubquiz.rest.socket.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
        String keyValue = key.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        QuizSession session = findSession(sessionId);
        if (!session.getHostKey().equals(keyValue)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid host key");
        }
        Team team = findTeam(sessionId, teamId);
        boolean success = session.getTeams().remove(team);
        sessionRepository.save(session);
        return success;
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
        QuizSession session = findSession(sessionId);
        List<Team> teams = session.getTeams();
        if (teams.contains(team)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Team name already taken");
        teams.add(team);
        sessionRepository.save(session);
        webSocket.convertAndSend(Channel.session(sessionId), team);
        return team;
    }
}
