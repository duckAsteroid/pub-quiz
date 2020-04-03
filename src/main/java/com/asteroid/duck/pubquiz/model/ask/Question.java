package com.asteroid.duck.pubquiz.model.ask;

import com.asteroid.duck.pubquiz.model.QuizSession;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.util.List;

@Data
@Builder
@JsonDeserialize(builder = Question.QuestionBuilder.class)
public class Question {
    /** The question as put/read to the audience */
    private String question;
    /** A URL to a picture relevant to the question */
    private URL imageReference;
    /** The kind of question */
    private QuestionType type;
    /** Candidate answers (if multiple choice) */
    private List<CandidateAnswer> candidateAnswers;
    /** The correct answer (descriptive - or by reference to the correct candidate code) */
    private String correctAnswer;
    /** What is the question worth (maximum) */
    private int maxPoints;

    @JsonPOJOBuilder(withPrefix = "")
    public static class QuestionBuilder {}
}
