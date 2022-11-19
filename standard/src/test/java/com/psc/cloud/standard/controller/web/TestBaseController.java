package com.psc.cloud.standard.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles({"dev", "db-h2"})
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBaseController {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    public void getIndex() throws Exception {
        String data = "GET";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("data", data);

        MvcResult mvcResult = mockMvc.perform(get("/").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(content).contains(data);
    }

    @Test
    @Order(2)
    public void postIndex() throws Exception {
        String data = "POST";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("data", data);

        MvcResult mvcResult = mockMvc.perform(post("/").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(content).contains(data);
    }
}
