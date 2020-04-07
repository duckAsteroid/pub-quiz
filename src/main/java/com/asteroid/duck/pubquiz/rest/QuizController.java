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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/quiz")
public class QuizController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Creates new quiz in the database
     * @param formParams the URL form params
     * @return a session (with a short ID) that teams can join
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String create(@RequestBody MultiValueMap<String, String> formParams) {
        System.out.println("form params received " + formParams);
        String file = formParams.getFirst("file");
        Quiz quiz = null;
        try {
            quiz = objectMapper.readValue(file, Quiz.class);
            return quizRepository.save(quiz).getId().toHexString();
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed JSON", e);
        }
    }

    @RequestMapping(value="/list", method = RequestMethod.GET)
    public List<String> list() {
        return quizRepository.findAll().stream()
                .map(Quiz::getId)
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Quiz read(@PathVariable("id") String id) {
        return quizRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable("id") String id, @RequestBody Quiz quiz) {
        ObjectId quizId = new ObjectId(id);
        if (quiz.getId().equals(quizId)) {
            quizRepository.save(quiz);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz ID does not match that of JSON Quiz object");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean delete(@PathVariable("id") String id) {
        Quiz quiz = read(id);
        if (quiz != null) {
            quizRepository.delete(quiz);
            return true;
        }
        return false;
    }

}
