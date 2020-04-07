package com.asteroid.duck.pubquiz.ui;

import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.asteroid.duck.pubquiz.rest.QuizController;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Controller
@RequestMapping("/ui")
public class WebController {

    private QuizRepository quizRepository;

    @Autowired
    public WebController(SpringTemplateEngine engine, QuizRepository quizRepository) {
        engine.addDialect(new LayoutDialect());
        this.quizRepository = quizRepository;
    }

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("message", "No message specified");
        return "index"; //view
    }

    @GetMapping("/quizzes.html")
    public String quizList(Model model) {
        model.addAttribute("quizzes", quizRepository.findAll());
        return "quizzes"; //view
    }

    // /hello?name=kotlin
    @GetMapping("/hello")
    public String mainWithParam(
            @RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {

        model.addAttribute("message", name);

        return "index"; //view
    }
}
