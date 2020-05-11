package com.asteroid.duck.pubquiz.ui;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.QuizSessionState;
import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import com.asteroid.duck.pubquiz.rest.socket.Channel;
import com.asteroid.duck.pubquiz.rest.socket.events.SessionEvent;
import com.asteroid.duck.pubquiz.util.JSON;
import com.asteroid.duck.pubquiz.util.QuizName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.asteroid.duck.pubquiz.util.KeyVerifier.verify;

/**
 * Controller for host activities
 */
@Controller
@RequestMapping("/ui/host")
public class HostController {
    /** Random number generator - for host keys */
    private Random random = new Random();

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SimpMessagingTemplate webSocket;

    @GetMapping("/quizzes.html")
    public String listQuizzes(Model model) {
        model.addAttribute("quizzes", quizRepository.findAll());
        return "host/quizzes";
    }

    @PostMapping("/upload.html")
    public String upload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        final String lcFileName = file.getResource().getFilename().toLowerCase();
        if(lcFileName.endsWith(".xlsx")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel parsing not supported yet");
        }
        if (lcFileName.endsWith(".json")) {
            ObjectMapper mapper = JSON.mapper();
            Quiz quiz = mapper.readValue(file.getInputStream(), Quiz.class);
            quizRepository.save(quiz);
            model.addAttribute("quiz", quiz);
        }
        return "redirect:quizzes.html";
    }

    @GetMapping("/session.html")
    public String startSession(@RequestParam String quizId, Model model) {
        Quiz quiz = quizRepository.findById(new ObjectId(quizId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("quiz", quiz);
        model.addAttribute("session", QuizSession.builder().host("fred").quizId(quiz.getId().toHexString()).build());
        return "host/start";
    }

    @PostMapping("/session.html")
    public RedirectView startSession(@ModelAttribute QuizSession session, Model model) {
        // create session short name
        session.setShortId(QuizName.newName());
        // create secret
        session.setHostKey(Long.toHexString(random.nextLong()));
        // save quiz session to DB
        sessionRepository.save(session);

        model.addAttribute("sessionId", session.getShortId());
        model.addAttribute("hostKey", session.getHostKey());

        RedirectView rv = new RedirectView();
        rv.setContextRelative(true);
        rv.setUrl(session.getShortId()+"/waitForTeams.html?hostKey="+session.getHostKey());
        return rv;
    }

    private QuizSession loadSessionAndQuiz(@PathVariable("sessionId") String sessionId, @RequestParam(name = "hostKey") Optional<String> hostKey, Map<String, Object> model) {
        final String key = verify(hostKey);
        final QuizSession session = sessionRepository.findByShortId(sessionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verify(key, session);
        Quiz quiz = quizRepository.findById(new ObjectId(session.getQuizId())).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz with ID=" + session.getQuizId()));
        // we have a session, quiz and they have the right key...
        model.put("quizSession", session);
        model.put("quiz", quiz);
        return session;
    }

    /**
     * Used by the host of a quiz (redirect from {@link #startSession(String, Model)})
     * to wait for teams to arrive
     * @param sessionId The session short ID
     * @param hostKey The host key
     * @param model MVC model
     * @return The page view name
     */
    @GetMapping("/{sessionId}/waitForTeams.html")
    public View waitForTeams(@PathVariable("sessionId") String sessionId, @RequestParam(name = "hostKey") Optional<String> hostKey, ModelAndView model) {
        QuizSession session = loadSessionAndQuiz(sessionId, hostKey, model.getModel());
        if (session.getState() == QuizSessionState.WAITING_FOR_TEAMS) {
            model.setViewName("host/waitForTeams");
            return model.getView();
        }
        else if (session.getState() == QuizSessionState.PLAYING){
            RedirectView rv =new RedirectView();
            rv.setContextRelative(true);
            rv.setUrl("questioning.html?hostKey="+session.getHostKey());
            return rv;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{sessionId}/questioning.html")
    public String questioning(@PathVariable("sessionId") String sessionId, @RequestParam(name = "hostKey") Optional<String> hostKey, Model model) {
        QuizSession session = loadSessionAndQuiz(sessionId, hostKey, model.asMap());
        if (session.getState() == QuizSessionState.WAITING_FOR_TEAMS) {
            session.setState(QuizSessionState.PLAYING);
            session.setCurrentQuestion(QuestionId.builder().round(0).question(0).build());
            sessionRepository.save(session);
            // send socket event
            webSocket.convertAndSend(Channel.session(sessionId), SessionEvent.builder().action(SessionEvent.Action.SESSION_STARTED).build());
        }
        return "host/questioning";
    }
}
