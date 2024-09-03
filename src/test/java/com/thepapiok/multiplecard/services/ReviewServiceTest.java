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
  private static final String TEST_ID1 = "66cefb4a78e8125cc175dde6";
  private static final String TEST_ID2 = "66cefb4a78e8125cc175dde2";

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
    account.setId(TEST_ID1);
  }

  @Test
  public void shouldSuccessAddReviewWhenNoLikes() {
    Review expectedReview = new Review();
    expectedReview.setRating(TEST_RATING);
    expectedReview.setDescription(TEST_DESCRIPTION);
    expectedReview.setCreatedAt(TEST_DATE);
    User user = new User();
    user.setId(TEST_ID1);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);
    expectedUser.setReview(expectedReview);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(new ObjectId(TEST_ID1))).thenReturn(Optional.empty());

    assertTrue(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldSuccessAddReviewWhenAreLikes() {
    final ObjectId objectId = new ObjectId(TEST_ID1);
    Review expectedReview = new Review();
    expectedReview.setRating(TEST_RATING);
    expectedReview.setDescription(TEST_DESCRIPTION);
    expectedReview.setCreatedAt(TEST_DATE);
    User user = new User();
    user.setId(TEST_ID1);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);
    expectedUser.setReview(expectedReview);
    Like like = new Like();
    like.setReviewUserId(objectId);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(objectId)).thenReturn(Optional.of(like));

    assertTrue(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
    verify(likeRepository).deleteAllByReviewUserId(objectId);
  }

  @Test
  public void shouldFailAddReviewWhenUserIsEmpty() {
    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.empty());

    assertFalse(reviewService.addReview(reviewDTO, TEST_PHONE));
  }

  @Test
  public void shouldFailAddReviewGetException() {
    Review expectedReview = new Review();
    expectedReview.setRating(TEST_RATING);
    expectedReview.setDescription(TEST_DESCRIPTION);
    expectedReview.setCreatedAt(TEST_DATE);
    User user = new User();
    user.setId(TEST_ID1);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);
    expectedUser.setReview(expectedReview);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(new ObjectId(TEST_ID1))).thenReturn(Optional.empty());
    doThrow(MongoWriteException.class).when(userRepository).save(expectedUser);

    assertFalse(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldSuccessAddLike() {
    ObjectId reviewUserId = new ObjectId(TEST_ID2);
    ObjectId userId = new ObjectId(TEST_ID1);
    Like expectedLike = new Like();
    expectedLike.setUserId(userId);
    expectedLike.setReviewUserId(reviewUserId);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(reviewUserId, userId))
        .thenReturn(Optional.empty());

    assertTrue(reviewService.addLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).save(expectedLike);
  }

  @Test
  public void shouldFailAddLikeAndDeleteLikeWhenNoUserId() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(null);

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeAndDeleteLikeWhenNoReviewUserId() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    assertFalse(reviewService.addLike("", TEST_PHONE));
    assertFalse(reviewService.deleteLike("", TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeAndDeleteLikeWhenNoAccount() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(accountRepository.findById(TEST_ID2)).thenReturn(Optional.empty());

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeAndDeleteLikeWhenUserNotFound() {
    Account account1 = new Account();
    account1.setId(TEST_ID2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(accountRepository.findById(TEST_ID2)).thenReturn(Optional.of(account1));
    when(userRepository.findById(TEST_ID2)).thenReturn(Optional.empty());

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeAndDeleteLikeWhenUserFoundWithoutReview() {
    Account account1 = new Account();
    account1.setId(TEST_ID2);
    User user = new User();
    user.setId(TEST_ID2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(accountRepository.findById(TEST_ID2)).thenReturn(Optional.of(account1));
    when(userRepository.findById(TEST_ID2)).thenReturn(Optional.of(user));

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeWhenFoundLike() {
    ObjectId reviewUserId = new ObjectId(TEST_ID2);
    ObjectId userId = new ObjectId(TEST_ID1);
    Like like = new Like();

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(reviewUserId, userId))
        .thenReturn(Optional.of(like));

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeWhenGetException() {
    ObjectId reviewUserId = new ObjectId(TEST_ID2);
    ObjectId userId = new ObjectId(TEST_ID1);
    Like expectedLike = new Like();
    expectedLike.setUserId(userId);
    expectedLike.setReviewUserId(reviewUserId);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(reviewUserId, userId))
        .thenReturn(Optional.empty());
    doThrow(MongoWriteException.class).when(likeRepository).save(expectedLike);

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).save(expectedLike);
  }

  @Test
  public void shouldFailDeleteLikeWhenNotFoundLike() {
    ObjectId reviewUserId = new ObjectId(TEST_ID2);
    ObjectId userId = new ObjectId(TEST_ID1);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(reviewUserId, userId))
        .thenReturn(Optional.empty());

    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailDeleteLikeWhenGetException() {
    ObjectId reviewUserId = new ObjectId(TEST_ID2);
    ObjectId userId = new ObjectId(TEST_ID1);
    Like expectedLike = new Like();
    expectedLike.setUserId(userId);
    expectedLike.setReviewUserId(reviewUserId);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(reviewUserId, userId))
        .thenReturn(Optional.of(expectedLike));
    doThrow(MongoWriteException.class).when(likeRepository).delete(expectedLike);

    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).delete(expectedLike);
  }

  @Test
  public void shouldSuccessDeleteLike() {
    ObjectId reviewUserId = new ObjectId(TEST_ID2);
    ObjectId userId = new ObjectId(TEST_ID1);
    Like expectedLike = new Like();
    expectedLike.setUserId(userId);
    expectedLike.setReviewUserId(reviewUserId);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(reviewUserId, userId))
        .thenReturn(Optional.of(expectedLike));

    assertTrue(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).delete(expectedLike);
  }

  private void atLike() {
    Account account1 = new Account();
    account1.setId(TEST_ID2);
    User user = new User();
    user.setId(TEST_ID2);
    user.setReview(review);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(accountRepository.findById(TEST_ID2)).thenReturn(Optional.of(account1));
    when(userRepository.findById(TEST_ID2)).thenReturn(Optional.of(user));
  }

  @Test
  public void shouldSuccessRemoveReview() {
    User user = new User();
    user.setReview(new Review());
    user.setId(TEST_ID1);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));

    assertTrue(reviewService.removeReview(TEST_ID1, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldFailRemoveReviewWhenUserNotFound() {
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.empty());

    assertFalse(reviewService.removeReview(TEST_ID1, TEST_PHONE));
  }

  @Test
  public void shouldFailRemoveReviewWhenNotTheSameId() {
    User user = new User();
    user.setReview(new Review());
    user.setId(TEST_ID1);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));

    assertFalse(reviewService.removeReview(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailRemoveReviewWhenGetException() {
    User user = new User();
    user.setReview(new Review());
    user.setId(TEST_ID1);
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));
    doThrow(MongoWriteException.class).when(userRepository).save(expectedUser);

    assertFalse(reviewService.removeReview(TEST_ID1, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }
}
