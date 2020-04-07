package com.asteroid.duck.pubquiz.model;

import com.asteroid.duck.pubquiz.model.answer.Submission;
import com.asteroid.duck.pubquiz.model.answer.SubmittedAnswer;
import com.asteroid.duck.pubquiz.model.ask.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuizJsonTest {

    public static final Quiz example() {
        Question r1q1 = Question.builder()
                .type(QuestionType.TEXT)
                .question("How many Ps in Mississippi?")
                .correctAnswer("2")
                .build();
        Question r1q2 = Question.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .candidateAnswers(CandidateAnswer.alphaChoices("One", "Two", "Three", "Four"))
                .correctAnswer("D")
                .question("How many Ss in Mississippi?")
                .build();;
        Round r1 = Round.builder().title("General Knowledge").questions(Arrays.asList(r1q1, r1q2)).build();

        Question r2q1 = Question.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .candidateAnswers(CandidateAnswer.alphaChoices("Lancashire", "Cheshire", "Yorkshire", "Derbyshire"))
                .question("In which county is Altrincham?")
                .correctAnswer("Cheshire")
                .build();
        Round r2 = Round.builder().title("Geography").questions(Collections.singletonList(r2q1)).build();

        Quiz example = Quiz.builder()
                .quizName("Test Quiz")
                .rounds(Arrays.asList(r1, r2))
                .build();

        return example;
    }

    public  static final Submission exampleSubmission() {
        Team team = Team.builder().name("Jolly Rogers").build();
        HashMap<QuestionId, SubmittedAnswer> answers = new HashMap<>();
        answers.put(QuestionId.builder().round(1).question(1).build(), SubmittedAnswer.builder().answer("3").build());
        answers.put(QuestionId.builder().round(1).question(2).build(), SubmittedAnswer.builder().answer("Two").build());
        answers.put(QuestionId.builder().round(2).question(1).build(), SubmittedAnswer.builder().answer("CHESHIRE").build());
        Submission submission = Submission.builder().quizSession(ObjectId.get().toHexString()).team(team).answers(answers).build();
        return submission;
    }

    @Test
    public void testSerialize() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        Quiz example = example();
        String jsonout = objectWriter.writeValueAsString(example);

        Quiz readBack = objectMapper.readValue(jsonout, Quiz.class);
        assertEquals(example, readBack);

    }
}