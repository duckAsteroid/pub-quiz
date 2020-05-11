package com.asteroid.duck.pubquiz.rest.socket.events;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.asteroid.duck.pubquiz.model.Team;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = SubmissionEvent.SubmissionEventBuilder.class)
public class SubmissionEvent {
    private Team team;
    @JsonSerialize(as = String.class)
    private QuestionId questionId;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SubmissionEventBuilder {}
}
