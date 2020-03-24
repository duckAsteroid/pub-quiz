package com.asteroid.duck.pubquiz.repo;

import com.asteroid.duck.pubquiz.model.answer.Submission;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<Submission, ObjectId> {
}
