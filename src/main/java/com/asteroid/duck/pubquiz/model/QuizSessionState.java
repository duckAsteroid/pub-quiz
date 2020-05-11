package com.asteroid.duck.pubquiz.model;

public enum QuizSessionState {
    /** Waiting for teams to join the session */
    WAITING_FOR_TEAMS,
    /** Actively playing the quiz */
    PLAYING,
    /** Teams are marking each other's answers */
    SCORING;
}
