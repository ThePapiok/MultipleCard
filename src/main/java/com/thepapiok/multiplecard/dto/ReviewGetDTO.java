package com.thepapiok.multiplecard.dto;

import com.thepapiok.multiplecard.collections.Review;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
public class ReviewGetDTO {
  @Id private ObjectId id;
  private String firstName;
  private Review review;
  private int count;
  private int isAdded;
  private boolean owner;
}
