package com.thepapiok.multiplecard.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
  private static final String TEST_PHONE = "123123123";
  private static final String LANDING_PAGE_URL = "/";
  private static final String REVIEW_PARAM = "newReview";
  private static final String REVIEWS_SIZE_PARAM = "reviewsSize";
  private static final String LANDING_PAGE_VIEW = "landingPage";
  private static final String ERROR_PARAM = "error";
  private static final String SUCCESS_PARAM = "success";
  private static final String REVIEWS_PARAM = "reviews";
  private static final String PRINCIPAL_PARAM = "principal";
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
  public void shouldReturnLandingPageWhenNoPrincipal() throws Exception {
    returnLandingPage(null, false);
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldReturnLandingPageWithPrincipal() throws Exception {
    returnLandingPage(TEST_PHONE, true);
  }

  private void returnLandingPage(String phone, boolean principal) throws Exception {
    when(reviewService.getReviewsFirst3(phone)).thenReturn(list);

    mockMvc
        .perform(get(LANDING_PAGE_URL))
        .andExpect(model().attribute(REVIEW_PARAM, new ReviewDTO()))
        .andExpect(model().attribute(REVIEWS_SIZE_PARAM, list.size()))
        .andExpect(model().attribute(REVIEWS_PARAM, list))
        .andExpect(model().attribute(PRINCIPAL_PARAM, principal))
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
    when(reviewService.getReviewsFirst3(null)).thenReturn(list);

    mockMvc
        .perform(get(LANDING_PAGE_URL).param(param, ""))
        .andExpect(model().attribute(REVIEWS_SIZE_PARAM, list.size()))
        .andExpect(model().attribute(REVIEWS_PARAM, list))
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
