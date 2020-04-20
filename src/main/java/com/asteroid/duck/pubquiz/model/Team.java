package com.asteroid.duck.pubquiz.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * A team in a quiz session
 */
@Data
@JsonDeserialize(builder = Team.TeamBuilder.class)
@Builder
public class Team {
    private String name;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TeamBuilder {}
}
