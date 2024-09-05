package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.services.ReviewService;
import java.util.List;
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
  private static final String TEST_ID = "dfsa132sd132123fsd";
  private static final String ID_PARAM = "id";
  private static final String TRUE_TEXT = "true";
  private static final String TEST_DESCRIPTION1 = "sfdafsdf test";
  private static final String REVIEWS_URL = "/reviews";
  private static final String DESCRIPTION_PARAM = "description";
  private static final String RATING_PARAM = "rating";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String LANDING_PAGE_ERROR_URL = "/?error";
  private static final String TEST_DESCRIPTION2 = "123sdfasdfasdfsaf1 test";
  private static final String REVIEWS_PAGE = "reviewPage";
  private static final String REVIEWS_PARAM = "reviews";
  private static final String REVIEWS_SIZE_PARAM = "reviewsSize";
  private static final String PRINCIPAL_PARAM = "principal";
  private static final String PAGE_PARAM = "page";
  private static final String FIELD_PARAM = "field";
  private static final String TEXT_PARAM = "text";
  private static final String IS_DESCENDING_PARAM = "isDescending";
  private static final String PAGES_PARAM = "pages";
  private static final String PAGE_SELECTED_PARAM = "pageSelected";
  private static final String TEST_PAGE = "1";
  private static final String TEST_FIELD = "count";
  private static final String TEST_TEXT = "test";

  @Autowired private MockMvc mockMvc;
  @MockBean private ReviewService reviewService;

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldRedirectToLandingPageWithParamSuccess() throws Exception {
    final int rating = 3;
    ReviewDTO reviewDTO = new ReviewDTO();
    reviewDTO.setDescription(TEST_DESCRIPTION1);
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
    reviewDTO.setDescription(TEST_DESCRIPTION1);
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

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldSuccessAddLike() throws Exception {
    when(reviewService.addLike(TEST_ID, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(post("/reviews/addLike").param(ID_PARAM, TEST_ID))
        .andExpect(content().string(TRUE_TEXT));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldSuccessDeleteLike() throws Exception {
    when(reviewService.deleteLike(TEST_ID, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(post("/reviews/deleteLike").param(ID_PARAM, TEST_ID))
        .andExpect(content().string(TRUE_TEXT));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldSuccessRemoveReview() throws Exception {
    when(reviewService.removeReview(TEST_ID, TEST_PHONE)).thenReturn(true);

    mockMvc
        .perform(delete(REVIEWS_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(TRUE_TEXT));
  }

  @Test
  @WithMockUser(username = TEST_PHONE)
  public void shouldSuccessReviewPage() throws Exception {
    final int count1 = 5;
    Review review1 = new Review();
    review1.setDescription(TEST_DESCRIPTION1);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setReview(review1);
    reviewGetDTO1.setCount(count1);

    when(reviewService.getReview(TEST_PHONE)).thenReturn(reviewGetDTO1);

    reviewPage(TEST_PHONE, true);
    mockMvc
        .perform(
            get(REVIEWS_URL)
                .param(PAGE_PARAM, TEST_PAGE)
                .param(FIELD_PARAM, TEST_FIELD)
                .param(IS_DESCENDING_PARAM, TRUE_TEXT)
                .param(TEXT_PARAM, TEST_TEXT))
        .andExpect(model().attribute("yourReview", reviewGetDTO1));
  }

  @Test
  public void shouldSuccessReviewPageWhenUserNoLogin() throws Exception {
    reviewPage(null, false);
  }

  private void reviewPage(String phone, boolean principal) throws Exception {
    final int testPageInt = Integer.parseInt(TEST_PAGE);
    final int count1 = 5;
    final int count2 = 3;
    Review review1 = new Review();
    review1.setDescription(TEST_DESCRIPTION1);
    Review review2 = new Review();
    review2.setDescription(TEST_DESCRIPTION2);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setReview(review1);
    reviewGetDTO1.setCount(count1);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setReview(review2);
    reviewGetDTO2.setCount(count2);
    List<ReviewGetDTO> expectedReviews = List.of(reviewGetDTO1, reviewGetDTO2);
    List<Integer> pages = List.of(1);

    when(reviewService.getPages(testPageInt + 1)).thenReturn(pages);
    when(reviewService.getReviews(phone, testPageInt, TEST_FIELD, true, TEST_TEXT))
        .thenReturn(expectedReviews);

    mockMvc
        .perform(
            get(REVIEWS_URL)
                .param(PAGE_PARAM, TEST_PAGE)
                .param(FIELD_PARAM, TEST_FIELD)
                .param(IS_DESCENDING_PARAM, TRUE_TEXT)
                .param(TEXT_PARAM, TEST_TEXT))
        .andExpect(model().attribute(FIELD_PARAM, TEST_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, pages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, testPageInt + 1))
        .andExpect(model().attribute(REVIEWS_PARAM, expectedReviews))
        .andExpect(model().attribute(REVIEWS_SIZE_PARAM, expectedReviews.size()))
        .andExpect(model().attribute(PRINCIPAL_PARAM, principal))
        .andExpect(view().name(REVIEWS_PAGE));
  }
}
