package com.thepapiok.multiplecard.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReviewControllerTest {
  @Autowired private MockMvc mockMvc;

  @Test
  public void shouldRedirectToLandingPage() throws Exception {
    mockMvc.perform(post("/review")).andExpect(redirectedUrl("/?added"));
  }
}
