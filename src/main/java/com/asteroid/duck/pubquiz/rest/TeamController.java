package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class TeamController {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SimpMessagingTemplate webSocket;

    private QuizSession getSession(String sessionId) {
        return sessionRepository.findByShortId(sessionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No session ID="+sessionId));
    }

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
}
