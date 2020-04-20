package com.asteroid.duck.pubquiz.model.ask;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a set of questions and answers the actual quiz data if you like
 */
@Data
@Builder
@JsonDeserialize(builder = Quiz.QuizBuilder.class)
@Document(collection = "quizzes")
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    private ObjectId id;
    private String quizName;
    private List<Round> rounds;

    public Question getById(QuestionId questionId) throws NoSuchElementException {
        Round round = rounds.get(questionId.getRound());
        return round.getQuestions().get(questionId.getQuestion());
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class QuizBuilder {}
}
