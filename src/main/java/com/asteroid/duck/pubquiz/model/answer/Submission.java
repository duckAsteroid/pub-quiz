package com.asteroid.duck.pubquiz.model.answer;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.asteroid.duck.pubquiz.model.Team;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@Document(collection = "submissions")
public class Submission {
    @Id
    private ObjectId id;
    @Indexed
    private String quizSession;
    @Indexed
    private Team team;

    private Map<QuestionId, SubmittedAnswer> answers;
}
