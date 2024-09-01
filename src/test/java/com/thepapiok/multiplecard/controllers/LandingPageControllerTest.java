package com.thepapiok.multiplecard.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LandingPageControllerTest {

  private static final String LANDING_PAGE_URL = "/";
  private static final String REVIEW_PARAM = "review";
  private static final String LANDING_PAGE_VIEW = "landingPage";
  private static final String ERROR_PARAM = "error";
  private static final String SUCCESS_PARAM = "success";
  @Autowired private MockMvc mockMvc;

  @Test
  public void shouldReturnLandingPage() throws Exception {
    mockMvc
        .perform(get(LANDING_PAGE_URL))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(view().name(LANDING_PAGE_VIEW));
  }

  @Test
  public void shouldReturnLandingPageWithParamErrorButNoMessage() throws Exception {
    paramWithoutMessage(ERROR_PARAM);
  }

  @Test
  public void shouldReturnLandingPageWithParamSuccessButNoMessage() throws Exception {
    paramWithoutMessage(SUCCESS_PARAM);
  }

  private void paramWithoutMessage(String param) throws Exception {
    mockMvc
        .perform(get(LANDING_PAGE_URL).param(param, ""))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(view().name(LANDING_PAGE_VIEW));
  }

  @Test
  public void shouldReturnLandingPageWithParamError() throws Exception {
    final String message = "Error";
    final String errorMessageParam = "errorMessage";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(errorMessageParam, message);
    paramWithMessage(ERROR_PARAM, httpSession, message, errorMessageParam);
  }

  @Test
  public void shouldReturnLandingPageWithParamSuccess() throws Exception {
    final String message = "Opinia została pomyślnie dodana !";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_PARAM, true);
    paramWithMessage(SUCCESS_PARAM, httpSession, message, "successMessage");
  }

  private void paramWithMessage(
      String param, MockHttpSession httpSession, String message, String type) throws Exception {
    mockMvc
        .perform(get(LANDING_PAGE_URL).param(param, "").session(httpSession))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(model().attribute(type, message))
        .andExpect(view().name(LANDING_PAGE_VIEW));
  }
}
