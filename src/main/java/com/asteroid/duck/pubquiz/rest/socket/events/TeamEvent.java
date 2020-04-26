package com.asteroid.duck.pubquiz.rest.socket.events;

import com.asteroid.duck.pubquiz.model.Team;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = TeamEvent.TeamEventBuilder.class)
public class TeamEvent {
    private String operation;
    private Team team;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TeamEventBuilder {}
}
