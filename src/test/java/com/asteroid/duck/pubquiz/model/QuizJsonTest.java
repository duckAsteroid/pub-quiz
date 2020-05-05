package com.asteroid.duck.pubquiz.model;

import com.asteroid.duck.pubquiz.model.answer.Submission;
import com.asteroid.duck.pubquiz.model.answer.SubmittedAnswer;
import com.asteroid.duck.pubquiz.model.ask.*;
import com.asteroid.duck.pubquiz.util.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuizJsonTest {

    public static final Quiz example() {
        Question r1q1 = Question.builder()
                .type(QuestionType.TEXT)
                .question("How many Ps in Mississippi?")
                .build();
        Question r1q2 = Question.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .candidateAnswers(CandidateAnswer.alphaChoices("One", "Two", "Three", "Four"))
                .question("How many Ss in Mississippi?")
                .build();;
        Round r1 = Round.builder().title("General Knowledge").questions(Arrays.asList(r1q1, r1q2)).build();

        Question r2q1 = Question.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .candidateAnswers(CandidateAnswer.alphaChoices("Lancashire", "Cheshire", "Yorkshire", "Derbyshire"))
                .question("In which county is Altrincham?")
                .build();

        Question r2q2 = Question.builder()
                .type(QuestionType.MULTIPLE_CHOICE)
                .candidateAnswers(CandidateAnswer.alphaChoices("Russia", "Ukraine", "Turkey", "Georgia"))
                .question("In which country is the Chrimean Penninsula?")
                .build();
        Round r2 = Round.builder().title("Geography").questions(Arrays.asList(r2q1, r2q2)).build();

        Map<Integer, Map<Integer, List<AcceptedAnswer>>> answers = new HashMap<>();
        Map<Integer, List<AcceptedAnswer>> r1answers = new HashMap<>();
        r1answers.put( 1, Collections.singletonList(AcceptedAnswer.builder().answer("2").build()));
        r1answers.put( 2, Collections.singletonList(AcceptedAnswer.builder().answer("D").build()));
        answers.put(1, r1answers);
        Map<Integer, List<AcceptedAnswer>> r2answers = new HashMap<>();
        r2answers.put(1, Collections.singletonList(AcceptedAnswer.builder().answer("Cheshire").build()));
        r2answers.put(2, Arrays.asList(AcceptedAnswer.builder().answer("Russia").build(), AcceptedAnswer.builder().answer("Ukraine").build()));
        answers.put(2, r2answers);

        Quiz example = Quiz.builder()
                .quizName("Test Quiz")
                .rounds(Arrays.asList(r1, r2))
                .answers(answers)
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
        ObjectMapper objectMapper = JSON.mapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        Quiz example = example();
        String jsonout = objectWriter.writeValueAsString(example);
        System.out.println(jsonout);

        Quiz readBack = objectMapper.readValue(jsonout, Quiz.class);
        assertEquals(example, readBack);

    }
}