package com.mgu.reactive.tutorial;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PersonController.class})
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("get list of persons")
    void getListOfPersons() throws Exception {
        mockMvc.perform(get("/persons"))
                        .andDo(print())
                                .andExpect(status().isOk())
                .andExpect(content().json("[1,3,2]"));

        mockMvc.perform(get("/persons/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[1,3,2]"));
    }

    @ParameterizedTest
    @DisplayName("get one person")
    @ValueSource(strings = {"1", "2", "3"})
    void getOnePerson(String userId) throws Exception {
        mockMvc.perform(get("/persons/"+userId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("get one person domain")
    @ValueSource(strings = {"1", "2", "3"})
    void getOnePersonDomain(String userId) throws Exception {
        mockMvc.perform(get("/persons/"+userId+"/domain"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}