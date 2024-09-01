package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.services.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReviewControllerTest {
  private static final String TEST_PHONE = "12312312312";
  private static final String TEST_DESCRIPTION = "sfdafsdf";
  private static final String REVIEWS_URL = "/reviews";
  private static final String DESCRIPTION_PARAM = "description";
  private static final String RATING_PARAM = "rating";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String LANDING_PAGE_ERROR_URL = "/?error";
  @Autowired private MockMvc mockMvc;
  @MockBean private ReviewService reviewService;

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLandingPageWithParamSuccess() throws Exception {
    final int rating = 3;
    ReviewDTO reviewDTO = new ReviewDTO();
    reviewDTO.setDescription(TEST_DESCRIPTION);
    reviewDTO.setRating(rating);
    MockHttpSession httpSession = new MockHttpSession();

    when(reviewService.addReview(reviewDTO, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(
            post(REVIEWS_URL)
                .param(DESCRIPTION_PARAM, reviewDTO.getDescription())
                .param(RATING_PARAM, String.valueOf(rating))
                .session(httpSession))
        .andExpect(redirectedUrl("/?success"));
    assertTrue((Boolean) httpSession.getAttribute("success"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLandingPageWithParamErrorWhenValidationError() throws Exception {
    final int rating = 6;
    final String message = "Podane dane są niepoprawne";
    ReviewDTO reviewDTO = new ReviewDTO();
    reviewDTO.setRating(rating);
    MockHttpSession httpSession = new MockHttpSession();

    mockMvc
        .perform(
            post(REVIEWS_URL)
                .param(DESCRIPTION_PARAM, reviewDTO.getDescription())
                .param(RATING_PARAM, String.valueOf(rating))
                .session(httpSession))
        .andExpect(redirectedUrl(LANDING_PAGE_ERROR_URL));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLandingPageWithParamErrorWhenErrorAtAddReview() throws Exception {
    final int rating = 3;
    final String message = "Nieoczekiwany błąd";
    ReviewDTO reviewDTO = new ReviewDTO();
    reviewDTO.setDescription(TEST_DESCRIPTION);
    reviewDTO.setRating(rating);
    MockHttpSession httpSession = new MockHttpSession();

    when(reviewService.addReview(reviewDTO, TEST_PHONE)).thenReturn(false);

    mockMvc
        .perform(
            post(REVIEWS_URL)
                .param(DESCRIPTION_PARAM, reviewDTO.getDescription())
                .param(RATING_PARAM, String.valueOf(rating))
                .session(httpSession))
        .andExpect(redirectedUrl(LANDING_PAGE_ERROR_URL));
    assertEquals(message, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }
}
