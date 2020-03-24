package com.asteroid.duck.pubquiz.repo;

import com.asteroid.duck.pubquiz.model.QuizSession;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessionRepository extends MongoRepository<QuizSession, ObjectId> {
    Optional<QuizSession> findByShortId(String shortId);
    int countAllByShortId(String shortId);
    void deleteAllByShortId(String shortId);
}
