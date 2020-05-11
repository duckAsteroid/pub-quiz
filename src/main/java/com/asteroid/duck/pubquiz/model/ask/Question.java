package com.asteroid.duck.pubquiz.model.ask;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A single question in the quiz, and the quiz setters answer.
 */
@Data
@Builder
@JsonDeserialize(builder = Question.QuestionBuilder.class)
public class Question {
    /** The question as put/read to the audience */
    private String question;
    /** A URL to a picture relevant to the question */
    private URL imageReference;
    /** An attribution for the picture (if any) */
    private String imageAttribution;
    /** The kind of question */
    private QuestionType type;
    /** Candidate answers (if multiple choice) */
    private List<CandidateAnswer> candidateAnswers;
    /** What is the question worth (maximum) */
    private int maxPoints;

    @JsonPOJOBuilder(withPrefix = "")
    public static class QuestionBuilder {
    }
}
