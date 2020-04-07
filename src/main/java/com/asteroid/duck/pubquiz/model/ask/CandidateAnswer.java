package com.asteroid.duck.pubquiz.model.ask;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A candidate or possible answer in a multiple choice question
 */
@Data
@Builder
@JsonDeserialize(builder = CandidateAnswer.CandidateAnswerBuilder.class)
public class CandidateAnswer {
    private char code;
    private String answer;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CandidateAnswerBuilder {}

    public static final char[] ALPHA = IntStream.rangeClosed('A', 'Z')
            .mapToObj(c -> "" + (char) c)
            .collect(Collectors.joining())
            .toCharArray();

    public static final char[] NUMERIC = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final List<CandidateAnswer> alphaChoices(String ... choices) {
        return choices(ALPHA, choices);
    }

    public static final List<CandidateAnswer> choices(char[] codes, String ... choices) {
        ArrayList<CandidateAnswer> result = new ArrayList<>(choices.length);
        if (choices.length > codes.length) throw new IllegalArgumentException("Not enough codes!");
        for (int i = 0; i < choices.length; i++) {
            CandidateAnswer candidateAnswer = CandidateAnswer.builder().code(codes[i]).answer(choices[i]).build();
            result.add(candidateAnswer);
        }
        return result;
    }

}
