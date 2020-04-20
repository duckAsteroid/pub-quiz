package com.asteroid.duck.pubquiz.model.answer;

import lombok.Builder;
import lombok.Data;

/**
 * A single answer to a single question by a single team
 */
@Data
@Builder
public class SubmittedAnswer {
    /** The actual answer */
    private String answer;
    /** A mark for this answer */
    private Mark mark;
}
