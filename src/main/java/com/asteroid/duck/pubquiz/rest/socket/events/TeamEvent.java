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
    public static final String OP_ENTERED = "entered";
    public static final String OP_LEFT = "left";

    private String operation;
    private Team team;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TeamEventBuilder {}
}
