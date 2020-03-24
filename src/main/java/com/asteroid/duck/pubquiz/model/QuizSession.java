package com.asteroid.duck.pubquiz.model;

import com.asteroid.duck.pubquiz.json.ObjectIdDeserializer;
import com.asteroid.duck.pubquiz.json.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@JsonDeserialize(builder = QuizSession.QuizSessionBuilder.class)
@Document(collection = "sessions")
public class QuizSession {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId id;

    @Indexed(unique = true)
    private String shortId;

    private String host;

    /** The ID of the {@link com.asteroid.duck.pubquiz.model.ask.Quiz} */
    private String quizId;

    @Builder.Default
    private List<Team> teams = new ArrayList<>();

    @JsonPOJOBuilder(withPrefix = "")
    public static class QuizSessionBuilder {}
}
