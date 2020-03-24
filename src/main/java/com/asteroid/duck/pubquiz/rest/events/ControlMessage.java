package com.asteroid.duck.pubquiz.rest.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ControlMessage.ControlMessageBuilder.class)
public class ControlMessage {
    public enum Action {
        REVEAL_NEXT_QUESTION
    }
    private String sessionId;
    private Action action;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ControlMessageBuilder {}
}
