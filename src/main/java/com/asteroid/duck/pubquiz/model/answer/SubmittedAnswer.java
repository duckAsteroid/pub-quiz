package com.asteroid.duck.pubquiz.model.answer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmittedAnswer {
    /** The actual answer */
    private String answer;
    /** A mark for this answer */
    private Mark mark;
}
