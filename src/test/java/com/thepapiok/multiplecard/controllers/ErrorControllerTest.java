package com.thepapiok.multiplecard.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ErrorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void shouldReturnAccessDeniedPageAtAccessDeniedPageWhenEverythingOk() throws Exception {
    mockMvc.perform(get("/access_denied")).andExpect(view().name("accessDeniedPage"));
  }
}
