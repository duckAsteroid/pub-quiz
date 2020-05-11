package com.asteroid.duck.pubquiz.ui;

import com.asteroid.duck.pubquiz.repo.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
public class WebController {

    private QuizRepository quizRepository;

    @Autowired
    public WebController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @GetMapping("/")
    public String main(Model model) {
        return "index";
    }





}
