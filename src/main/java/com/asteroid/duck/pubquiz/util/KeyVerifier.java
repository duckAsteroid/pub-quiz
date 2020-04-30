package com.asteroid.duck.pubquiz.util;

import com.asteroid.duck.pubquiz.model.QuizSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class KeyVerifier {
    public static String verify(Optional<String> key) {
        return key.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    public static void verify(String key, QuizSession session) {
        if (session == null || !session.getHostKey().equals(key)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid key");
        }
    }
}
