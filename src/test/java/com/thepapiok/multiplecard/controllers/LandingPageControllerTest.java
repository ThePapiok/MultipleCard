package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.services.ReviewService;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
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
public class LandingPageControllerTest {
  private static final String LANDING_PAGE_URL = "/";
  private static final String REVIEW_PARAM = "newReview";
  private static final String REVIEWS_SIZE_PARAM = "reviewsSize";
  private static final String LANDING_PAGE_VIEW = "landingPage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String REVIEWS_PARAM = "reviews";
  private static final String ERROR_PARAM = "error";
  private static List<ReviewGetDTO> list;
  @Autowired private MockMvc mockMvc;
  @MockBean private ReviewService reviewService;

  @BeforeAll
  public static void setUp() {
    final int rating = 5;
    Review review = new Review();
    review.setRating(rating);
    review.setDescription("dasdasqw123sdfsdf");
    ReviewGetDTO reviewGetDTO = new ReviewGetDTO();
    reviewGetDTO.setCount(0);
    reviewGetDTO.setIsAdded(0);
    reviewGetDTO.setId(new ObjectId("123456789012345678901244"));
    reviewGetDTO.setFirstName("Test");
    reviewGetDTO.setReview(review);
    list = List.of(reviewGetDTO);
  }

  @Test
  public void shouldReturnLandingPageAtGetLandingPageWhenNoPrincipal() throws Exception {
    returnLandingPage(null, false);
  }

  @Test
  @WithMockUser(username = "123123123")
  public void shouldReturnLandingPageAtGetLandingPageWithPrincipal() throws Exception {
    returnLandingPage("123123123", true);
  }

  private void returnLandingPage(String phone, boolean principal) throws Exception {
    when(reviewService.getReviewsFirst3(phone)).thenReturn(list);

    mockMvc
        .perform(get(LANDING_PAGE_URL))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(model().attribute(REVIEWS_SIZE_PARAM, list.size()))
        .andExpect(model().attribute(REVIEWS_PARAM, list))
        .andExpect(model().attribute("principal", principal))
        .andExpect(view().name(LANDING_PAGE_VIEW));
  }

  @Test
  public void shouldReturnLandingPageAtGetLandingPageWithParamErrorButNoMessage() throws Exception {
    paramWithoutMessage(ERROR_PARAM);
  }

  @Test
  public void shouldReturnLandingPageAtGetLandingPageWithParamSuccessButNoMessage()
      throws Exception {
    paramWithoutMessage("success");
  }

  private void paramWithoutMessage(String param) throws Exception {
    when(reviewService.getReviewsFirst3(null)).thenReturn(list);

    mockMvc
        .perform(get(LANDING_PAGE_URL).param(param, ""))
        .andExpect(model().attribute(REVIEWS_SIZE_PARAM, list.size()))
        .andExpect(model().attribute(REVIEWS_PARAM, list))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(view().name(LANDING_PAGE_VIEW));
  }

  @Test
  public void shouldReturnLandingPageAtGetLandingPageWithParamError() throws Exception {
    final String message = "Error";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);

    paramWithMessage(ERROR_PARAM, httpSession, message, ERROR_MESSAGE_PARAM);
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  public void shouldReturnLandingPageAtGetLandingPageWithParamError501() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, "success!");

    mockMvc
        .perform(get(LANDING_PAGE_URL).param(ERROR_PARAM, "501").session(httpSession))
        .andExpect(redirectedUrl("/?error"));
    assertEquals("Anulowano zakup produktów", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
    assertNull(httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  @Test
  public void shouldReturnLandingPageAtGetLandingPageWithParamSuccess() throws Exception {
    final String message = "Opinia została pomyślnie dodana !";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, message);

    paramWithMessage("success", httpSession, message, SUCCESS_MESSAGE_PARAM);
    assertNull(httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  private void paramWithMessage(
      String param, MockHttpSession httpSession, String message, String type) throws Exception {
    when(reviewService.getReviewsFirst3(null)).thenReturn(list);

    mockMvc
        .perform(get(LANDING_PAGE_URL).param(param, "").session(httpSession))
        .andExpect(model().attribute(REVIEWS_SIZE_PARAM, list.size()))
        .andExpect(model().attribute(REVIEWS_PARAM, list))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(model().attribute(type, message))
        .andExpect(view().name(LANDING_PAGE_VIEW));
  }
}
