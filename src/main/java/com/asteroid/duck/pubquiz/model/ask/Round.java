package com.asteroid.duck.pubquiz.model.ask;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * A round in a quiz - a list of questions united under a title
 */
@Data
@Builder
public class Round {
    private String title;
    private List<Question> questions;
}
