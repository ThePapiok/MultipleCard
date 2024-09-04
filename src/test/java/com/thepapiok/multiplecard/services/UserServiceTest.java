package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceTest {
  private static final String TEST_PHONE = "+4823423411423";
  private static final String TEST_EMAIL = "email";
  private static final String TEST_PASSWORD = "123wefasdfasd123bsedf";
  private static final String TEST_ID = "123123dfasdf";
  private static final String TEST_OBJECT_ID = "123456789012345678901234";
  private static final String TEST1_TEXT = "test1";
  private static final String TEST2_TEXT = "test2";
  private static final String TEST3_TEXT = "test3";
  private static final String TEST4_TEXT = "test4";

  private UserService userService;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;

  @BeforeAll
  public static void setAll() {}

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(accountRepository, userRepository);
  }

  @Test
  public void shouldSuccessLoadUserByUsername() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setId(TEST_ID);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setActive(true);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(TEST_PASSWORD);
    User user =
        new User(
            TEST_PHONE,
            TEST_PASSWORD,
            Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(expectedAccount);

    assertEquals(user, userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldFailLoadUserByUsernameWhenUserNotFound() {

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(null);

    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldFailLoadUserByUsernameWhenUserNotActive() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setId(TEST_ID);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setActive(false);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(TEST_PASSWORD);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(expectedAccount);

    assertThrows(NotActiveException.class, () -> userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldSuccessGetReviews() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setOwner(true);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST2_TEXT);
    ReviewGetDTO reviewGetDTO3 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST3_TEXT);
    ReviewGetDTO reviewGetDTO4 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST4_TEXT);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);
    List<ReviewGetDTO> expected =
        List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findAllReviewWithCountAndIsAddedCheck(new ObjectId(TEST_OBJECT_ID)))
        .thenReturn(list);

    assertEquals(expected, userService.getReviews(TEST_PHONE));
  }

  @Test
  public void shouldSuccessGetReviewsFirst3WhenFindMoreThan3Results() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setOwner(true);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST2_TEXT);
    ReviewGetDTO reviewGetDTO3 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST3_TEXT);
    ReviewGetDTO reviewGetDTO4 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST4_TEXT);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findAllReviewWithCountAndIsAddedCheck(new ObjectId(TEST_OBJECT_ID)))
        .thenReturn(list);

    assertEquals(expected, userService.getReviewsFirst3(TEST_PHONE));
  }

  @Test
  public void shouldSuccessGetReviewsFirst3WhenFindLessThan3Results() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST2_TEXT);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findAllReviewWithCountAndIsAddedCheck(new ObjectId(TEST_OBJECT_ID)))
        .thenReturn(list);

    assertEquals(expected, userService.getReviewsFirst3(TEST_PHONE));
  }

  @Test
  public void shouldSuccessGetReviewsWhenNotLoginUser() {
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST2_TEXT);
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2);

    when(userRepository.findAllReviewWithCountAndIsAddedCheck(null)).thenReturn(list);

    assertEquals(expected, userService.getReviews(null));
  }

  @Test
  public void shouldFailGetReviewsWhenGetNull() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findAllReviewWithCountAndIsAddedCheck(new ObjectId(TEST_OBJECT_ID)))
        .thenReturn(List.of());

    assertEquals(List.of(), userService.getReviews(TEST_PHONE));
  }

  @Test
  public void shouldFailGetReviewsWhenGetException() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findAllReviewWithCountAndIsAddedCheck(new ObjectId(TEST_OBJECT_ID)))
        .thenThrow(MongoWriteException.class);

    assertNull(userService.getReviews(TEST_PHONE));
  }

  @Test
  public void shouldSuccessGetReviewFirst3WhenFindMoreThan3sResults() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    ReviewGetDTO reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setOwner(true);
    ReviewGetDTO reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST2_TEXT);
    ReviewGetDTO reviewGetDTO3 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName("Test3");
    ReviewGetDTO reviewGetDTO4 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName("Test4");
    List<ReviewGetDTO> list = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3, reviewGetDTO4);
    List<ReviewGetDTO> expected = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findAllReviewWithCountAndIsAddedCheck(new ObjectId(TEST_OBJECT_ID)))
        .thenReturn(list);

    assertEquals(expected, userService.getReviewsFirst3(TEST_PHONE));
  }
}
