package com.asteroid.duck.pubquiz.model.answer;

import com.asteroid.duck.pubquiz.model.Team;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * The mark (or score) given to a single submitted answer
 */
@Data
@Builder
public class Mark {
    private boolean correct;
    private int score;
    private String notes;
    private Team markedBy;
    private Date time;
}
