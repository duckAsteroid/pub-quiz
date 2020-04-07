package com.asteroid.duck.pubquiz.ui;

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
    @Autowired
    public WebController(SpringTemplateEngine engine) {
        engine.addDialect(new LayoutDialect());
    }

    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("message", "No message specified");
        return "index"; //view
    }

    // /hello?name=kotlin
    @GetMapping("/hello")
    public String mainWithParam(
            @RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {

        model.addAttribute("message", name);

        return "index"; //view
    }
}
