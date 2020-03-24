package com.asteroid.duck.pubquiz.rest;

import com.asteroid.duck.pubquiz.QuizName;
import com.asteroid.duck.pubquiz.model.QuizJsonTest;
import com.asteroid.duck.pubquiz.model.QuizSession;
import com.asteroid.duck.pubquiz.model.Team;
import com.asteroid.duck.pubquiz.model.ask.Question;
import com.asteroid.duck.pubquiz.model.ask.Quiz;
import com.asteroid.duck.pubquiz.repo.QuizRepository;
import com.asteroid.duck.pubquiz.repo.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static java.util.GregorianCalendar.getInstance;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataMongo
@AutoConfigureMockMvc()
@WebMvcTest
public class QuizControllerTest {
    private static final String HOST = "Bruce Forsyth";
    private static final String TEAM = "Quizzy Rascals";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private QuizRepository quizRepository;

    private Quiz example;

    @BeforeEach
    public void setup() {
        quizRepository.deleteAll();
        example = quizRepository.save(QuizJsonTest.example());

        sessionRepository.deleteAll();
    }

    @Test
    void startQuiz() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(
                    post("/start")
                            .param("host", HOST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsBytes(example)))
                .andExpect(status().isOk())
                //.andExpect(content().json())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        System.out.println(json);
        QuizSession session = mapper.readValue(json, QuizSession.class);
        assertNotNull(session);
    }

    private QuizSession createSession() {
        QuizSession tmp = QuizSession.builder().host(HOST).shortId(QuizName.newName()).quizId(example.getId().toHexString()).build();
        return sessionRepository.save(tmp);
    }

    @Test
    void newTeam() throws Exception {
        QuizSession session = createSession();
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(
                post("/sessions/"+session.getShortId()+"/teams/new")
                        .param("name", TEAM))
                .andExpect(status().isOk())
                //.andExpect(content().json())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        System.out.println(json);
        Team team = mapper.readValue(json, Team.class);
        assertNotNull(team);
        assertEquals(TEAM, team.getName());

        // can't do it twice
        mockMvc.perform(
                post("/sessions/"+session.getShortId()+"/teams/new")
                        .param("name", TEAM))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getQuestion() throws Exception {
        QuizSession session = createSession();
        ObjectMapper mapper = new ObjectMapper();
        MvcResult mvcResult = mockMvc.perform(
                get("/sessions/"+session.getShortId()+"/rounds/0/questions/0"))
                .andExpect(status().isOk())
                //.andExpect(content().json())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        System.out.println(json);
        Question q = mapper.readValue(json, Question.class);
        assertNotNull(q);

        mvcResult = mockMvc.perform(
                get("/sessions/"+session.getShortId()+"/rounds/0/questions/0")
                    .param("showAnswer", "true"))
                .andExpect(status().isOk())
                //.andExpect(content().json())
                .andReturn();
        json = mvcResult.getResponse().getContentAsString();
        System.out.println(json);
        q = mapper.readValue(json, Question.class);
        assertNotNull(q);
    }
}