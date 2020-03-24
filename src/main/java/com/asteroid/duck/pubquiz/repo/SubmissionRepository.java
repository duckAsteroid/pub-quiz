package com.asteroid.duck.pubquiz.repo;

import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.model.answer.Submission;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubmissionRepository extends MongoRepository<Submission, ObjectId> {
    Optional<Submission> findByQuizSessionAndTeam(String quizSession, Team team);

    void deleteAllByQuizSession(String quizSession);
    void deleteAllByQuizSessionaAndTeam(String quizSession, Team team);
}
