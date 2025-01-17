package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ReviewAtReportDTO;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class UserRepositoryTest {

  private static final ObjectId TEST_ID1 = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_ID2 = new ObjectId("123456789012345678901235");
  private static final ObjectId TEST_ID3 = new ObjectId("123456789012345678901236");
  private static final ObjectId TEST_ID5 = new ObjectId("123456789012115678901231");
  private static final String TEST1_TEXT = "Test1";
  private static final String FIELD_COUNT = "count";
  private static final String FIELD_CREATED_ADT = "review.createdAt";
  private static final String FIELD_RATING = "review.rating";
  private static ReviewGetDTO reviewGetDTO1;
  private static ReviewGetDTO reviewGetDTO2;
  private static ReviewGetDTO reviewGetDTO3;
  @Autowired private UserRepository userRepository;
  @Autowired private LikeRepository likeRepository;
  @Autowired private CardRepository cardRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private AccountRepository accountRepository;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  @Transactional
  public void setUp() {
    final ObjectId cardId = new ObjectId("123123123123123123123123");
    final int count1 = 2;
    final int count2 = 1;
    final int year1 = 2012;
    final int year2 = 2013;
    final int year3 = 2015;
    final int year5 = 2018;
    final int month1 = 5;
    final int month2 = 1;
    final int month3 = 2;
    final int month5 = 6;
    final int dayOfMonth1 = 3;
    final int dayOfMonth2 = 5;
    final int dayOfMonth3 = 1;
    final int dayOfMonth5 = 1;
    final int hour1 = 6;
    final int hour2 = 12;
    final int hour3 = 22;
    final int hour5 = 6;
    final int minute1 = 1;
    final int minute2 = 6;
    final int minute3 = 6;
    final int minute5 = 34;
    final int rating1 = 3;
    final int rating2 = 5;
    final int rating3 = 1;
    final int rating5 = 3;
    final ObjectId testUserId4 = new ObjectId("123456789012345678901237");
    Review review1 = new Review();
    review1.setDescription(TEST1_TEXT);
    review1.setCreatedAt(LocalDateTime.of(year1, month1, dayOfMonth1, hour1, minute1));
    review1.setRating(rating1);
    Address address1 = new Address();
    address1.setCity("city1");
    address1.setCountry("country1");
    address1.setHouseNumber("h1");
    address1.setPostalCode("p1");
    address1.setProvince("province1");
    address1.setStreet("street1");
    Card card = new Card();
    card.setId(cardId);
    card.setName("name");
    card.setAttempts(0);
    card.setPin("123sasdfaf");
    card.setImageUrl("123asdasdasdasd");
    card.setUserId(TEST_ID1);
    mongoTemplate.save(card);
    User user1 = new User();
    user1.setFirstName(TEST1_TEXT);
    user1.setId(TEST_ID1);
    user1.setLastName("last1");
    user1.setAddress(address1);
    user1.setReview(review1);
    user1.setCardId(cardId);
    mongoTemplate.save(user1);
    Review review2 = new Review();
    review2.setDescription(TEST1_TEXT);
    review2.setCreatedAt(LocalDateTime.of(year2, month2, dayOfMonth2, hour2, minute2));
    review2.setRating(rating2);
    Address address2 = new Address();
    address2.setCity("city2");
    address2.setCountry("country2");
    address2.setHouseNumber("h2");
    address2.setPostalCode("p2");
    address2.setProvince("province2");
    address2.setStreet("street2");
    User user2 = new User();
    user2.setFirstName("Test2");
    user2.setId(TEST_ID2);
    user2.setReview(review2);
    user2.setLastName("last2");
    user2.setAddress(address2);
    user2.setCardId(null);
    mongoTemplate.save(user2);
    Review review3 = new Review();
    review3.setDescription("b");
    review3.setCreatedAt(LocalDateTime.of(year3, month3, dayOfMonth3, hour3, minute3));
    review3.setRating(rating3);
    Address address3 = new Address();
    address3.setCity("city3");
    address3.setCountry("country3");
    address3.setHouseNumber("h3");
    address3.setPostalCode("p3");
    address3.setProvince("province3");
    address3.setStreet("street3");
    User user3 = new User();
    user3.setFirstName("Test3");
    user3.setId(TEST_ID3);
    user3.setReview(review3);
    user3.setLastName("last3");
    user3.setAddress(address3);
    user3.setCardId(null);
    mongoTemplate.save(user3);
    Address address4 = new Address();
    address4.setCity("city4");
    address4.setCountry("country4");
    address4.setHouseNumber("h4");
    address4.setPostalCode("p4");
    address4.setProvince("province4");
    address4.setStreet("street4");
    User user4 = new User();
    user4.setFirstName("Test4");
    user4.setId(testUserId4);
    user4.setLastName("last4");
    user4.setAddress(address4);
    user4.setCardId(null);
    mongoTemplate.save(user4);
    Address address5 = new Address();
    address5.setCity("city5");
    address5.setCountry("country5");
    address5.setHouseNumber("h5");
    address5.setPostalCode("p5");
    address5.setProvince("province5");
    address5.setStreet("street5");
    Review review5 = new Review();
    review5.setDescription("sfdaa");
    review5.setCreatedAt(LocalDateTime.of(year5, month5, dayOfMonth5, hour5, minute5));
    review5.setRating(rating5);
    User user5 = new User();
    user5.setFirstName("Test5");
    user5.setId(TEST_ID5);
    user5.setReview(review5);
    user5.setLastName("last5");
    user5.setAddress(address5);
    user5.setCardId(null);
    mongoTemplate.save(user5);
    Like like1 = new Like();
    like1.setReviewUserId(TEST_ID1);
    like1.setUserId(TEST_ID1);
    mongoTemplate.save(like1);
    Like like2 = new Like();
    like2.setReviewUserId(TEST_ID1);
    like2.setUserId(TEST_ID2);
    mongoTemplate.save(like2);
    Like like3 = new Like();
    like3.setReviewUserId(TEST_ID2);
    like3.setUserId(TEST_ID3);
    mongoTemplate.save(like3);
    reviewGetDTO1 = new ReviewGetDTO();
    reviewGetDTO1.setFirstName(TEST1_TEXT);
    reviewGetDTO1.setCount(count1);
    reviewGetDTO1.setId(TEST_ID1);
    reviewGetDTO1.setIsAdded(1);
    reviewGetDTO1.setOwner(true);
    reviewGetDTO1.setReview(review1);
    reviewGetDTO2 = new ReviewGetDTO();
    reviewGetDTO2.setFirstName("Test2");
    reviewGetDTO2.setCount(count2);
    reviewGetDTO2.setId(TEST_ID2);
    reviewGetDTO2.setIsAdded(0);
    reviewGetDTO2.setReview(review2);
    reviewGetDTO2.setOwner(false);
    reviewGetDTO3 = new ReviewGetDTO();
    reviewGetDTO3.setFirstName("Test3");
    reviewGetDTO3.setCount(0);
    reviewGetDTO3.setId(TEST_ID3);
    reviewGetDTO3.setIsAdded(0);
    reviewGetDTO3.setReview(review3);
    reviewGetDTO3.setOwner(false);
    Account account1 = new Account();
    account1.setId(TEST_ID1);
    account1.setPhone("+23412312343421");
    account1.setRole(Role.ROLE_USER);
    account1.setActive(true);
    account1.setBanned(false);
    account1.setEmail("emailasfdfdasafds");
    account1.setPassword("sdfasdffasdfdsa");
    accountRepository.save(account1);
    Account account2 = new Account();
    account2.setId(TEST_ID2);
    account2.setPhone("+345235443");
    account2.setRole(Role.ROLE_USER);
    account2.setActive(true);
    account2.setBanned(false);
    account2.setEmail("emailsadffsaddsaf");
    account2.setPassword("sfdaasfddasewrqwer");
    accountRepository.save(account2);
    Account account3 = new Account();
    account3.setId(TEST_ID3);
    account3.setPhone("+13413223141324");
    account3.setRole(Role.ROLE_USER);
    account3.setActive(true);
    account3.setBanned(false);
    account3.setEmail("emailhffdhdfgds");
    account3.setPassword("dsggfsgsdsgdsddgs");
    accountRepository.save(account3);
    Account account4 = new Account();
    account4.setId(testUserId4);
    account4.setPhone("+34524352345");
    account4.setRole(Role.ROLE_USER);
    account4.setActive(true);
    account4.setBanned(false);
    account4.setEmail("emaildfsgsdfgsdfg");
    account4.setPassword("hfghdggdfgdfs");
    accountRepository.save(account4);
    Account account5 = new Account();
    account5.setId(testUserId4);
    account5.setPhone("+213423153245324");
    account5.setRole(Role.ROLE_USER);
    account5.setActive(true);
    account5.setBanned(true);
    account5.setEmail("emaildertwertwertwe");
    account5.setPassword("gdsgdgsdgertw");
    accountRepository.save(account5);
    TextIndexDefinition textIndex =
        new TextIndexDefinition.TextIndexDefinitionBuilder()
            .onField("review.description")
            .withDefaultLanguage("none")
            .build();
    mongoTemplate.indexOps(User.class).ensureIndex(textIndex);
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
    likeRepository.deleteAll();
    cardRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void
      shouldReturnListOfReviewGetDTOAtFindPageOfReviewWithCountAndIsAddedCheckWithTextWhenIsDescendingAndFieldCount() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, FIELD_COUNT, -1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, FIELD_COUNT, -1, 0, TEST1_TEXT));
  }

  @Test
  public void
      shouldReturnListOfReviewGetDTOAtFindPageOfReviewWithCountAndIsAddedCheckWithTextWhenWithFieldCountAndAscending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO3, reviewGetDTO2, reviewGetDTO1);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO2, reviewGetDTO1);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, FIELD_COUNT, 1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, FIELD_COUNT, 1, 0, TEST1_TEXT));
  }

  @Test
  public void
      shouldReturnListOfReviewGetDTOAtFindPageOfRevieWithCountAndIsAddedCheckWithTextWhenFieldCreatedAtAndDescending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO3, reviewGetDTO2, reviewGetDTO1);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO2, reviewGetDTO1);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_ID1, FIELD_CREATED_ADT, -1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, FIELD_CREATED_ADT, -1, 0, TEST1_TEXT));
  }

  @Test
  public void
      shouldReturnListOfReviewGetDTOAtFindPageOfReviewWithCountAndIsAddedCheckWithTextWhenFieldCreatedAtAndAscending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, FIELD_CREATED_ADT, 1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, FIELD_CREATED_ADT, 1, 0, TEST1_TEXT));
  }

  @Test
  public void
      shouldReturnListOfReviewGetDTOAtFindPageOfReviewWithCountAndIsAddedCheckWithTextWhenFieldRatingAndDescending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO2, reviewGetDTO1, reviewGetDTO3);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO2, reviewGetDTO1);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, FIELD_RATING, -1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, FIELD_RATING, -1, 0, TEST1_TEXT));
  }

  @Test
  public void
      shouldReturnListOfReviewGetDTOAtFindPageOfReviewWithCountAndIsAddedCheckWithTextWhenFieldRatingAndAscending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO3, reviewGetDTO1, reviewGetDTO2);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(TEST_ID1, FIELD_RATING, 1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_ID1, FIELD_RATING, 1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldReturnReviewGetDTOAtFindReviewWhenEverythingOk() {
    assertEquals(reviewGetDTO2, userRepository.findReview(TEST_ID2));
  }

  @Test
  public void shouldReturnReviewAtReportDTOAtGetReviewAtReportDTOByIdWhenEverythingOk() {
    final int testCount = 2;
    final int testRating = 3;
    ReviewAtReportDTO reviewAtReportDTO = new ReviewAtReportDTO();
    reviewAtReportDTO.setFirstName(TEST1_TEXT);
    reviewAtReportDTO.setDescription(TEST1_TEXT);
    reviewAtReportDTO.setRating(testRating);
    reviewAtReportDTO.setId(TEST_ID1.toString());
    reviewAtReportDTO.setCount(testCount);

    assertEquals(reviewAtReportDTO, userRepository.getReviewAtReportDTOById(TEST_ID1));
  }
}
