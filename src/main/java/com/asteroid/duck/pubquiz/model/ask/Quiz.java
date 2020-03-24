package com.asteroid.duck.pubquiz.model.ask;

import com.asteroid.duck.pubquiz.model.QuestionId;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.NoSuchElementException;

@Data
@Builder
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private ObjectId id;
    private String quizName;
    private List<Round> rounds;

    public Question getById(QuestionId questionId) throws NoSuchElementException {
        Round round = rounds.get(questionId.getRound());
        return round.getQuestions().get(questionId.getQuestion());
    }
}
