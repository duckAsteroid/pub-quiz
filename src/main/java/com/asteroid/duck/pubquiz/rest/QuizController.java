package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.repo.QuizRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/rest/quizzes")
public class QuizController {
    @Autowired
    public QuizRepository quizRepository;

    private Quiz getQuiz(String id) {
        Optional<Quiz> quiz = quizRepository.findById(new ObjectId(id));
        return quiz.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No quiz with ID="+id));
    }

    @RequestMapping(value = "{quizId}", method = RequestMethod.DELETE)
    public void deleteQuiz(@PathVariable("quizId") String quizId) {
        quizRepository.deleteById(new ObjectId(quizId));
    }
}
