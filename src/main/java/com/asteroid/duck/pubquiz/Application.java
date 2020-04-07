package com.asteroid.duck.pubquiz;

import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.asteroid.duck.pubquiz.repo.SubmissionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@EnableMongoRepositories(basePackageClasses = {QuizRepository.class, SubmissionRepository.class})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
