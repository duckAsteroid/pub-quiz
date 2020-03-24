package com.asteroid.duck.pubquiz.repo;

import com.asteroid.duck.pubquiz.model.ask.Quiz;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, ObjectId> {
}
