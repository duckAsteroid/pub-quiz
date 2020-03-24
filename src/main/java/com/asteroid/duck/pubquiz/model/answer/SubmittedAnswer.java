package com.asteroid.duck.pubquiz.model.answer;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.asteroid.duck.pubquiz.model.Team;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@Builder
public class SubmittedAnswer {
    @Indexed
    private String quizSession;
    @Indexed
    private Team team;
    @Indexed
    @JsonSerialize(as = String.class)
    private QuestionId questionId;
    /** The actual answer */
    private String answer;
    /** A mark for this answer */
    private Mark mark;
}
