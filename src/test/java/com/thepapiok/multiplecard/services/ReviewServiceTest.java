package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.misc.ReviewConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.LikeRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {
  private static final String TEST_ID = "66cefb4a78e8125cc175dde6";
  private static final int TEST_RATING = 3;
  private static final String TEST_DESCRIPTION = "adsdasdasd123123";
  private static final String TEST_PHONE = "1231231231";
  private static final LocalDateTime TEST_DATE = LocalDateTime.of(2024, 8, 30, 20, 14);
  private ReviewDTO reviewDTO;
  private Review review;
  private Account account;

  @Mock private ReviewConverter reviewConverter;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private LikeRepository likeRepository;
  private ReviewService reviewService;

  @BeforeAll
  public static void setStaticMethods() {
    MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_DATE);
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    reviewService =
        new ReviewService(reviewConverter, accountRepository, userRepository, likeRepository);
    reviewDTO = new ReviewDTO();
    reviewDTO.setRating(TEST_RATING);
    reviewDTO.setDescription(TEST_DESCRIPTION);
    review = new Review();
    review.setRating(TEST_RATING);
    review.setDescription(TEST_DESCRIPTION);
    review.setCreatedAt(TEST_DATE);
    account = new Account();
    account.setId(TEST_ID);
  }

  @Test
  public void shouldSuccessAddReviewWhenNoLikes() {
    Review expectedReview = new Review();
    expectedReview.setRating(TEST_RATING);
    expectedReview.setDescription(TEST_DESCRIPTION);
    expectedReview.setCreatedAt(TEST_DATE);
    User user = new User();
    user.setId(TEST_ID);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID);
    expectedUser.setReview(expectedReview);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(new ObjectId(TEST_ID))).thenReturn(Optional.empty());

    assertTrue(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldSuccessAddReviewWhenAreLikes() {
    final ObjectId objectId = new ObjectId(TEST_ID);
    Review expectedReview = new Review();
    expectedReview.setRating(TEST_RATING);
    expectedReview.setDescription(TEST_DESCRIPTION);
    expectedReview.setCreatedAt(TEST_DATE);
    User user = new User();
    user.setId(TEST_ID);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID);
    expectedUser.setReview(expectedReview);
    Like like = new Like();
    like.setReviewUserId(objectId);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(objectId)).thenReturn(Optional.of(like));

    assertTrue(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
    verify(likeRepository).deleteAllByReviewUserId(objectId);
  }

  @Test
  public void shouldFailAddReviewWhenUserIsEmpty() {
    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertFalse(reviewService.addReview(reviewDTO, TEST_PHONE));
  }

  @Test
  public void shouldFailAddReviewGetException() {
    Review expectedReview = new Review();
    expectedReview.setRating(TEST_RATING);
    expectedReview.setDescription(TEST_DESCRIPTION);
    expectedReview.setCreatedAt(TEST_DATE);
    User user = new User();
    user.setId(TEST_ID);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID);
    expectedUser.setReview(expectedReview);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(new ObjectId(TEST_ID))).thenReturn(Optional.empty());
    doThrow(MongoWriteException.class).when(userRepository).save(expectedUser);

    assertFalse(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }
}
