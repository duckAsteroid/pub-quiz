package com.asteroid.duck.pubquiz.model;

import com.asteroid.duck.pubquiz.model.ask.Question;
import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.model.ask.Round;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

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

    /**
     * Iterator over a quiz returning pairs of ID and questions
     */
    public static class QuizIterator implements Iterator<QuestionId> {
        private final Quiz quiz;
        private QuestionId current;

        public QuizIterator(final Quiz quiz, QuestionId start)
        {
            this.quiz = quiz;
            this.current = start;
        }

        @Override
        public boolean hasNext() {
            return currentRoundHasNextQuestion() || hasNextRound();
        }

        public boolean hasNextRound() {
            return quiz.getRounds().size() > current.round;
        }

        public Round currentRound() {
            return quiz.getRounds().get(current.round);
        }

        public boolean currentRoundHasNextQuestion() {
            return currentRound().getQuestions().size() > current.question;
        }

        @Override
        public QuestionId next() {
            if (currentRoundHasNextQuestion()) {
                current = new QuestionId(current.round, current.question + 1);
            }
            else if (hasNextRound()){
                current = new QuestionId(current.round + 1, 0);
            }
            else {
                current = null;
                throw new NoSuchElementException();
            }
            return current;
        }
    }

    public static Function<QuestionId, Question> mapper(final Quiz quiz) {
        return new Function<QuestionId, Question>() {
            @Override
            public Question apply(QuestionId questionId) {
                return quiz.getById(questionId);
            }
        };
    }
}
