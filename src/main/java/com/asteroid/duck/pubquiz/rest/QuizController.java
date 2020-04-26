package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/quiz")
public class QuizController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Creates new quiz in the database
     * @param quiz the quiz object from JSON
     * @return the quiz ID
     */
    @RequestMapping(value = "/new", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String create(@RequestBody Quiz quiz) {
        return quizRepository.save(quiz).getId().toHexString();
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
