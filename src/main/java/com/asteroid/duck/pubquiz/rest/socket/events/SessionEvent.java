package com.asteroid.duck.pubquiz.rest.socket.events;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * Quiz host control message to clients
 */
@Data
@Builder
@JsonDeserialize(builder = SessionEvent.SessionEventBuilder.class)
public class SessionEvent {
    public enum Action {
        SESSION_STARTED,
        SESSION_ENDED,
        CHANGE_QUESTION
    }

    private Action action;

    private QuestionId questionId;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SessionEventBuilder {}
}
