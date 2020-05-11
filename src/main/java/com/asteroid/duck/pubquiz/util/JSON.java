package com.asteroid.duck.pubquiz.util;

import com.asteroid.duck.pubquiz.model.QuestionId;
import com.asteroid.duck.pubquiz.model.QuestionIdSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JSON {
    private static ObjectMapper MAPPER;

    public static ObjectMapper mapper() {
        if (MAPPER == null) {
            MAPPER = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addKeyDeserializer(QuestionId.class, new QuestionIdSerializer());
            MAPPER.registerModule(simpleModule);
        }
        return MAPPER;
    }
}
