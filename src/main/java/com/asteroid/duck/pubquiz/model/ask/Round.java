package com.asteroid.duck.pubquiz.model.ask;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A round in a quiz - a list of questions united under a title
 */
@Data
@Builder
@JsonDeserialize(builder = Round.RoundBuilder.class)
public class Round {
    private String title;
    private List<Question> questions;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RoundBuilder {}
}
