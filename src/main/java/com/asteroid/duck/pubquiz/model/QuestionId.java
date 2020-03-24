package com.asteroid.duck.pubquiz.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionId {
    private static final String SEPARATOR = ".";
    private final int round;
    private final int question;

    @Override
    public final String toString() {
        return round + SEPARATOR + question;
    }

    public static QuestionId parse(String s) {
        String[] split = s.split(SEPARATOR);
        return builder().round(Integer.parseInt(split[0]))
                .question(Integer.parseInt(split[1]))
                .build();
    }
}
