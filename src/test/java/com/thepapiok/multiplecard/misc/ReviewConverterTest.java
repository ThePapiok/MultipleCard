package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import org.junit.jupiter.api.Test;

public class ReviewConverterTest {
  private final ReviewConverter reviewConverter = new ReviewConverter();

  @Test
  public void shouldReturnReviewEntityAtGetEntityWhenEverythingOk() {
    final int rating = 3;
    final String description = "tests123123123sdfwerw";
    ReviewDTO reviewDTO = new ReviewDTO();
    reviewDTO.setDescription(description);
    reviewDTO.setRating(rating);
    Review expectedReview = new Review();
    expectedReview.setRating(rating);
    expectedReview.setDescription(description);

    assertEquals(expectedReview, reviewConverter.getEntity(reviewDTO));
  }
}
