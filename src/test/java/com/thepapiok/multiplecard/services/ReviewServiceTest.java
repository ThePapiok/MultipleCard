package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.misc.ReviewConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.LikeRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {
  private static final ObjectId TEST_ID1 = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_ID2 = new ObjectId("123456789012345678901235");
  private static final int TEST_RATING = 3;
  private static final String TEST_DESCRIPTION = "adsdasdasd123123";
  private static final String TEST_PHONE = "1231231231";
  private static final String TEST1_TEXT = "test1";
  private static final String TEST2_TEXT = "test2";
  private static final String TEST3_TEXT = "test3";
  private static final String TEST4_TEXT = "test4";
  private static final String TEST_FIELD = "count";
  private static final int TEST_PAGE = 1;
  private static final int COUNT_REVIEWS_AT_PAGE = 12;
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
  public void shouldSuccessAtAddReviewWhenNoLikes() {
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
    when(likeRepository.findByReviewUserId(TEST_ID1)).thenReturn(Optional.empty());

    assertTrue(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldSuccessAtAddReviewWhenAreLikes() {
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
    like.setReviewUserId(TEST_ID1);

    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.of(user));
    when(likeRepository.findByReviewUserId(TEST_ID1)).thenReturn(Optional.of(like));

    assertTrue(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
    verify(likeRepository).deleteAllByReviewUserId(TEST_ID1);
  }

  @Test
  public void shouldFailAtAddReviewWhenUserIsEmpty() {
    when(reviewConverter.getEntity(reviewDTO)).thenReturn(review);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.empty());

    assertFalse(reviewService.addReview(reviewDTO, TEST_PHONE));
  }

  @Test
  public void shouldFailAtAddReviewGetException() {
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
    when(likeRepository.findByReviewUserId(TEST_ID1)).thenReturn(Optional.empty());
    doThrow(MongoWriteException.class).when(userRepository).save(expectedUser);

    assertFalse(reviewService.addReview(reviewDTO, TEST_PHONE));
    verify(userRepository).save(expectedUser);
  }

  @Test
  public void shouldSuccessAtAddLike() {
    Like expectedLike = new Like();
    expectedLike.setUserId(TEST_ID1);
    expectedLike.setReviewUserId(TEST_ID2);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(TEST_ID2, TEST_ID1))
        .thenReturn(Optional.empty());

    assertTrue(reviewService.addLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).save(expectedLike);
  }

  @Test
  public void shouldFailAtAddLikeAndAtDeleteLikeWhenNoUserId() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(null);

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAtAddLikeAndAtDeleteLikeWhenNoReviewUserId() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    assertFalse(reviewService.addLike(new ObjectId(), TEST_PHONE));
    assertFalse(reviewService.deleteLike(new ObjectId(), TEST_PHONE));
  }

  @Test
  public void shouldFailAtAddLikeAndAtDeleteLikeWhenNoAccount() {
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(accountRepository.findById(TEST_ID2)).thenReturn(Optional.empty());

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAtAddLikeAndAtDeleteLikeWhenUserNotFound() {
    Account account1 = new Account();
    account1.setId(TEST_ID2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(accountRepository.findById(TEST_ID2)).thenReturn(Optional.of(account1));
    when(userRepository.findById(TEST_ID2)).thenReturn(Optional.empty());

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAtAddLikeAndAtDeleteLikeWhenUserFoundWithoutReview() {
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
    Like like = new Like();

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(TEST_ID2, TEST_ID1))
        .thenReturn(Optional.of(like));

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailAddLikeWhenGetException() {
    Like expectedLike = new Like();
    expectedLike.setUserId(TEST_ID1);
    expectedLike.setReviewUserId(TEST_ID2);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(TEST_ID2, TEST_ID1))
        .thenReturn(Optional.empty());
    doThrow(MongoWriteException.class).when(likeRepository).save(expectedLike);

    assertFalse(reviewService.addLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).save(expectedLike);
  }

  @Test
  public void shouldFailDeleteLikeWhenNotFoundLike() {
    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(TEST_ID2, TEST_ID1))
        .thenReturn(Optional.empty());

    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
  }

  @Test
  public void shouldFailDeleteLikeWhenGetException() {
    Like expectedLike = new Like();
    expectedLike.setUserId(TEST_ID1);
    expectedLike.setReviewUserId(TEST_ID2);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(TEST_ID2, TEST_ID1))
        .thenReturn(Optional.of(expectedLike));
    doThrow(MongoWriteException.class).when(likeRepository).delete(expectedLike);

    assertFalse(reviewService.deleteLike(TEST_ID2, TEST_PHONE));
    verify(likeRepository).delete(expectedLike);
  }

  @Test
  public void shouldSuccessAtDeleteLike() {
    Like expectedLike = new Like();
    expectedLike.setUserId(TEST_ID1);
    expectedLike.setReviewUserId(TEST_ID2);

    atLike();
    when(likeRepository.findByReviewUserIdAndUserId(TEST_ID2, TEST_ID1))
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
  public void shouldSuccessAtRemoveReview() {
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
  public void shouldFailAtRemoveReviewWhenUserNotFound() {
    User expectedUser = new User();
    expectedUser.setId(TEST_ID1);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID1)).thenReturn(Optional.empty());

    assertFalse(reviewService.removeReview(TEST_ID1, TEST_PHONE));
  }

  @Test
  public void shouldFailAtRemoveReviewWhenNotTheSameId() {
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
  public void shouldFailAtRemoveReviewWhenGetException() {
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

  @Test
  public void shouldSuccessAtGetReviewsWithoutText() {
    getReviews(TEST_PHONE, true, "");
  }

  @Test
  public void shouldSuccessAtGetReviewsWithoutTextWhenNotLoginUser() {
    getReviews(null, true, "");
  }

  @Test
  public void shouldSuccessAtGetReviews() {
    getReviews(TEST_PHONE, false, TEST1_TEXT);
  }

  @Test
  public void shouldSuccessAtGetReviewsWhenNotLoginUser() {
    getReviews(null, false, TEST1_TEXT);
  }

  @Test
  public void shouldFailAtGetReviewsWhenGetNull() {
    Account account = new Account();
    account.setId(TEST_ID1);
    List<ReviewGetDTO> expectedList = List.of();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_ID1, TEST_FIELD, -1, TEST_PAGE * COUNT_REVIEWS_AT_PAGE))
        .thenReturn(expectedList);
    when(userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, TEST_FIELD, -1, TEST_PAGE * COUNT_REVIEWS_AT_PAGE, TEST1_TEXT))
        .thenReturn(expectedList);

    assertEquals(
        expectedList, reviewService.getReviews(TEST_PHONE, TEST_PAGE, TEST_FIELD, true, ""));
    assertEquals(
        expectedList,
        reviewService.getReviews(TEST_PHONE, TEST_PAGE, TEST_FIELD, true, TEST1_TEXT));
  }

  @Test
  public void shouldFailAtGetReviewsWhenGetException() {
    Account account = new Account();
    account.setId(TEST_ID1);
    List<ReviewGetDTO> expectedList = List.of();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_ID1, TEST_FIELD, -1, TEST_PAGE * COUNT_REVIEWS_AT_PAGE))
        .thenThrow(MongoWriteException.class);
    when(userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, TEST_FIELD, -1, TEST_PAGE * COUNT_REVIEWS_AT_PAGE, TEST1_TEXT))
        .thenThrow(MongoWriteException.class);

    assertEquals(
        expectedList, reviewService.getReviews(TEST_PHONE, TEST_PAGE, TEST_FIELD, true, ""));
    assertEquals(
        expectedList,
        reviewService.getReviews(TEST_PHONE, TEST_PAGE, TEST_FIELD, true, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessAtGetReviewsFirst3WhenFindMoreThan3Results() {
    final int count1 = 5;
    final int count2 = 4;
    final int count3 = 3;
    final int count4 = 3;
    Account account = new Account();
    account.setId(TEST_ID1);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setOwner(true);
    reviewGetDTO1.setCount(count1);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setFirstName(TEST2_TEXT);
    reviewGetDTO2.setCount(count2);
    ReviewGetDTO reviewGetDTO3 = new ReviewGetDTO();
    reviewGetDTO3.setFirstName(TEST3_TEXT);
    reviewGetDTO3.setCount(count3);
    ReviewGetDTO reviewGetDTO4 = new ReviewGetDTO();
    reviewGetDTO4.setFirstName(TEST4_TEXT);
    reviewGetDTO4.setCount(count4);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, TEST_FIELD, -1, 0))
        .thenReturn(list);

    assertEquals(expected, reviewService.getReviewsFirst3(TEST_PHONE));
  }

  @Test
  public void shouldSuccessAtGetReviewsFirst3WhenFindLessThan3Results() {
    final int count1 = 5;
    final int count2 = 3;
    Account account = new Account();
    account.setId(TEST_ID1);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setCount(count1);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setFirstName(TEST2_TEXT);
    reviewGetDTO2.setCount(count2);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, TEST_FIELD, -1, 0))
        .thenReturn(list);

    assertEquals(expected, reviewService.getReviewsFirst3(TEST_PHONE));
  }

  private void getReviews(String phone, boolean type, String text) {
    final int count1 = 5;
    final int count2 = 4;
    final int count3 = 3;
    final int count4 = 3;
    ObjectId objectId = null;
    if (phone != null) {
      objectId = TEST_ID1;
    }
    Account account = new Account();
    account.setId(TEST_ID1);
    Review review = new Review();
    review.setDescription(text);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setOwner(true);
    reviewGetDTO1.setCount(count1);
    reviewGetDTO1.setReview(review);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setFirstName(TEST2_TEXT);
    reviewGetDTO2.setCount(count2);
    reviewGetDTO2.setReview(review);
    ReviewGetDTO reviewGetDTO3 = new ReviewGetDTO();
    reviewGetDTO3.setFirstName(TEST3_TEXT);
    reviewGetDTO3.setCount(count3);
    reviewGetDTO3.setReview(review);
    ReviewGetDTO reviewGetDTO4 = new ReviewGetDTO();
    reviewGetDTO4.setFirstName(TEST4_TEXT);
    reviewGetDTO4.setCount(count4);
    reviewGetDTO4.setReview(review);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);
    List<ReviewGetDTO> expected =
        List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    if (type) {
      when(userRepository.findPageOfReviewWithCountAndIsAddedCheck(
              objectId, TEST_FIELD, -1, TEST_PAGE * COUNT_REVIEWS_AT_PAGE))
          .thenReturn(list);
    } else {
      when(userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
              objectId, TEST_FIELD, -1, TEST_PAGE * COUNT_REVIEWS_AT_PAGE, text))
          .thenReturn(list);
    }

    assertEquals(expected, reviewService.getReviews(phone, TEST_PAGE, TEST_FIELD, true, text));
  }

  @Test
  public void shouldSuccessAtGetPagesWhenToLess() {
    final int page = 3;
    final int page1 = 1;
    final int page2 = 2;
    final int page3 = 3;
    final int page4 = 4;
    final int page5 = 5;
    final int page6 = 6;
    final int pages = 5;
    final int someOtherReviews = 6;
    getPages(
        pages * COUNT_REVIEWS_AT_PAGE + someOtherReviews,
        page,
        List.of(page1, page2, page3, page4, page5, page6));
  }

  @Test
  public void shouldSuccessAtGetPagesWhenToMany() {
    final int page = 7;
    final int page4 = 4;
    final int page5 = 5;
    final int page6 = 6;
    final int page7 = 7;
    final int page8 = 8;
    final int page9 = 9;
    final int page10 = 10;
    final int pages = 13;
    getPages(
        pages * COUNT_REVIEWS_AT_PAGE,
        page,
        List.of(page4, page5, page6, page7, page8, page9, page10));
  }

  @Test
  public void shouldSuccessAtGetPagesWhenNoReviews() {
    getPages(0, 0, List.of());
  }

  private void getPages(int count, int page, List<Integer> expectedList) {
    when(userRepository.countAllByReviewIsNotNull()).thenReturn(count);

    assertEquals(expectedList, reviewService.getPages(page));
  }

  @Test
  public void shouldSuccessAtGetPagesWhenGetException() {
    List<Integer> expectedList = List.of();

    when(userRepository.countAllByReviewIsNotNull()).thenThrow(MongoWriteException.class);

    assertEquals(expectedList, reviewService.getPages(0));
  }

  @Test
  public void shouldSuccessAtGetReview() {
    Account account = new Account();
    account.setId(TEST_ID1);
    ReviewGetDTO reviewGetDTO = new ReviewGetDTO();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findReview(TEST_ID1)).thenReturn(reviewGetDTO);

    assertEquals(reviewGetDTO, reviewService.getReview(TEST_PHONE));
  }
}
