package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import org.springframework.stereotype.Component;

@Component
public class ReviewConverter {

  public Review getEntity(ReviewDTO reviewDTO) {
    Review review = new Review();
    review.setDescription(reviewDTO.getDescription());
    review.setRating(reviewDTO.getRating());
    return review;
  }
}
