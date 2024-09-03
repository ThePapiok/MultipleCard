package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
public class UserRepositoryTest {
  private static final String TEST_ID1 = "123456789012345678901234";
  private static final String TEST_ID2 = "123456789012345678901235";
  private static final String TEST_ID3 = "123456789012345678901236";
  private static final String TEST1_TEXT = "Test1";
  private static final String TEST2_TEXT = "Test2";
  private static final String TEST3_TEXT = "Test3";

  private static final ObjectId TEST_OBJECT_ID1 = new ObjectId(TEST_ID1);
  private static final ObjectId TEST_OBJECT_ID2 = new ObjectId(TEST_ID2);
  private static final ObjectId TEST_OBJECT_ID3 = new ObjectId(TEST_ID3);

  @Autowired private UserRepository userRepository;
  @Autowired private LikeRepository likeRepository;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
    likeRepository.deleteAll();
  }

  @Test
  public void shouldSuccessFindAllReviewWithCountAndIsAddedCheck() {
    Review review1 = new Review();
    User user1 = new User();
    user1.setFirstName(TEST1_TEXT);
    user1.setId(TEST_ID1);
    user1.setReview(review1);
    userRepository.save(user1);
    Review review2 = new Review();
    User user2 = new User();
    user2.setFirstName(TEST2_TEXT);
    user2.setId(TEST_ID2);
    user2.setReview(review2);
    userRepository.save(user2);
    User user3 = new User();
    user3.setFirstName(TEST3_TEXT);
    user3.setId(TEST_ID3);
    userRepository.save(user3);
    Like like1 = new Like();
    like1.setReviewUserId(TEST_OBJECT_ID1);
    like1.setUserId(TEST_OBJECT_ID1);
    likeRepository.save(like1);
    Like like2 = new Like();
    like2.setReviewUserId(TEST_OBJECT_ID1);
    like2.setUserId(TEST_OBJECT_ID2);
    likeRepository.save(like2);
    Like like3 = new Like();
    like3.setReviewUserId(TEST_OBJECT_ID2);
    like3.setUserId(TEST_OBJECT_ID3);
    likeRepository.save(like3);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setCount(2);
    reviewGetDTO1.setId(TEST_ID1);
    reviewGetDTO1.setIsAdded(1);
    reviewGetDTO1.setReview(review1);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setFirstName(TEST2_TEXT);
    reviewGetDTO2.setCount(1);
    reviewGetDTO2.setId(TEST_ID2);
    reviewGetDTO2.setIsAdded(0);
    reviewGetDTO2.setReview(review2);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(expected, userRepository.findAllReviewWithCountAndIsAddedCheck(TEST_OBJECT_ID1));
  }

  @Test
  public void shouldSuccessFindAllReviewWithCountAndIsAddedCheckWhenNoLikes() {
    Review review1 = new Review();
    User user1 = new User();
    user1.setFirstName(TEST1_TEXT);
    user1.setId(TEST_ID1);
    user1.setReview(review1);
    userRepository.save(user1);
    Review review2 = new Review();
    User user2 = new User();
    user2.setFirstName(TEST2_TEXT);
    user2.setId(TEST_ID2);
    user2.setReview(review2);
    userRepository.save(user2);
    User user3 = new User();
    user3.setFirstName(TEST3_TEXT);
    user3.setId(TEST_ID3);
    userRepository.save(user3);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setCount(0);
    reviewGetDTO1.setId(TEST_ID1);
    reviewGetDTO1.setIsAdded(0);
    reviewGetDTO1.setReview(review1);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setFirstName(TEST2_TEXT);
    reviewGetDTO2.setCount(0);
    reviewGetDTO2.setId(TEST_ID2);
    reviewGetDTO2.setIsAdded(0);
    reviewGetDTO2.setReview(review2);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(expected, userRepository.findAllReviewWithCountAndIsAddedCheck(TEST_OBJECT_ID1));
  }

  @Test
  public void shouldFailFindAllReviewWithCountAndIsAddedCheckWhenNoReviews() {
    User user1 = new User();
    user1.setFirstName(TEST1_TEXT);
    user1.setId(TEST_ID1);
    userRepository.save(user1);
    User user2 = new User();
    user2.setFirstName(TEST2_TEXT);
    user2.setId(TEST_ID2);
    userRepository.save(user2);
    User user3 = new User();
    user3.setFirstName(TEST3_TEXT);
    user3.setId(TEST_ID3);
    userRepository.save(user3);

    assertEquals(List.of(), userRepository.findAllReviewWithCountAndIsAddedCheck(TEST_OBJECT_ID1));
  }
}
