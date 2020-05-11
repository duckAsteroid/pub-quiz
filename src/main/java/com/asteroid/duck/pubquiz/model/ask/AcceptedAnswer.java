package com.asteroid.duck.pubquiz.model.ask;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * The accepted answer to a question as determined by the quiz setter
 */
@Data
@Builder
@JsonDeserialize(builder = AcceptedAnswer.AcceptedAnswerBuilder.class)
public class AcceptedAnswer {
    /** The correct answer (descriptive - or by reference to the correct candidate code) */
    private String answer;
    /** A correct answer is worth this many points */
    private double points;
    @JsonPOJOBuilder(withPrefix = "")
    public static class AcceptedAnswerBuilder {}
}
