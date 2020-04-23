package com.asteroid.duck.pubquiz.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A team in a quiz session
 */
@Data
@JsonDeserialize(builder = Team.TeamBuilder.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private long id;
    private String name;
    /** decimal for an emoji char in HTML5 */
    private String mascot;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TeamBuilder {}
}
