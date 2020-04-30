package com.asteroid.duck.pubquiz.model;

import com.asteroid.duck.pubquiz.json.ObjectIdDeserializer;
import com.asteroid.duck.pubquiz.json.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * An active quiz session, led by a host asking questions to a set of teams
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonDeserialize(builder = QuizSession.QuizSessionBuilder.class)
@Document(collection = "sessions")
public class QuizSession {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId id;

    @Indexed(unique = true)
    private String shortId;

    /** The state of the quiz */
    @Builder.Default
    private QuizSessionState state = QuizSessionState.WAITING_FOR_TEAMS;

    private String host;

    private String hostKey;

    /** The ID of the {@link com.asteroid.duck.pubquiz.model.ask.Quiz} being played */
    private String quizId;

    /** A pointer to the current question */
    private QuestionId currentQuestion;

    /** The teams taking part */
    @Builder.Default
    private List<Team> teams = new ArrayList<>();

    public Optional<Team> getTeamNamed(String name) {
        return teams.stream().filter(team -> team.getName().equals(name)).findFirst();
    }

    public Optional<Team> getTeamById(String teamId) {
        return teams.stream().filter(team -> team.getId().equals(teamId)).findFirst();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class QuizSessionBuilder {}
}
