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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/quiz")
public class QuizController {
    @Autowired
    private QuizRepository quizRepository;

    /**
     * Creates new quiz in the database
     * @param quiz the quiz to start
     * @return a session (with a short ID) that teams can join
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create( Quiz quiz) {
        return quizRepository.save(quiz).getId().toHexString();
    }

    @RequestMapping(value="/list")
    public List<String> list() {
        return quizRepository.findAll().stream()
                .map(Quiz::getId)
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}")
    public Quiz get(@PathVariable("id") String id) {
        return quizRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
