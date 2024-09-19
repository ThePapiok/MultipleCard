package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Review;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.configs.DbConfig;
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
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class UserRepositoryTest {

  private static final ObjectId TEST_ID1 = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_ID2 = new ObjectId("123456789012345678901235");
  private static final ObjectId TEST_ID3 = new ObjectId("123456789012345678901236");
  private static final ObjectId TEST_ID4 = new ObjectId("123456789012345678901237");
  private static final String TEST1_TEXT = "Test1";
  private static final String TEST2_TEXT = "Test2";
  private static final String TEST3_TEXT = "Test3";
  private static final String TEST4_TEXT = "Test4";
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
  @Autowired private MongoTransactionManager mongoTransactionManager;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  @Transactional
  public void setUp() {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    transactionTemplate.execute(
        new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus status) {
            final ObjectId cardId = new ObjectId("123123123123123123123123");
            final int count1 = 2;
            final int count2 = 1;
            final int year1 = 2012;
            final int year2 = 2013;
            final int year3 = 2015;
            final int month1 = 5;
            final int month2 = 1;
            final int monht3 = 2;
            final int dayOfMonth1 = 3;
            final int dayOfMonth2 = 5;
            final int dayOfMonth3 = 1;
            final int hour1 = 6;
            final int hour2 = 12;
            final int hour3 = 22;
            final int minute1 = 1;
            final int minute2 = 6;
            final int minute3 = 6;
            final int rating1 = 3;
            final int rating2 = 5;
            final int rating3 = 1;
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
            user2.setFirstName(TEST2_TEXT);
            user2.setId(TEST_ID2);
            user2.setReview(review2);
            user2.setLastName("last2");
            user2.setAddress(address2);
            user2.setCardId(null);
            mongoTemplate.save(user2);
            Review review3 = new Review();
            review3.setDescription("b");
            review3.setCreatedAt(LocalDateTime.of(year3, monht3, dayOfMonth3, hour3, minute3));
            review3.setRating(rating3);
            Address address3 = new Address();
            address3.setCity("city3");
            address3.setCountry("country3");
            address3.setHouseNumber("h3");
            address3.setPostalCode("p3");
            address3.setProvince("province3");
            address3.setStreet("street3");
            User user3 = new User();
            user3.setFirstName(TEST3_TEXT);
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
            user4.setFirstName(TEST4_TEXT);
            user4.setId(TEST_ID4);
            user4.setLastName("last4");
            user4.setAddress(address4);
            user4.setCardId(null);
            mongoTemplate.save(user4);
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
            reviewGetDTO2.setFirstName(TEST2_TEXT);
            reviewGetDTO2.setCount(count2);
            reviewGetDTO2.setId(TEST_ID2);
            reviewGetDTO2.setIsAdded(0);
            reviewGetDTO2.setReview(review2);
            reviewGetDTO2.setOwner(false);
            reviewGetDTO3 = new ReviewGetDTO();
            reviewGetDTO3.setFirstName(TEST3_TEXT);
            reviewGetDTO3.setCount(0);
            reviewGetDTO3.setId(TEST_ID3);
            reviewGetDTO3.setIsAdded(0);
            reviewGetDTO3.setReview(review3);
            reviewGetDTO3.setOwner(false);
            new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("review.description")
                .withDefaultLanguage("none")
                .build();
          }
        });
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
    likeRepository.deleteAll();
    cardRepository.deleteAll();
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldCountAndDescending() {
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
  public void shouldSuccessFindPagesOfReviewWithFieldCountAndAscending() {
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
  public void shouldSuccessFindPagesOfReviewWithFieldCreatedAtAndDescending() {
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
  public void shouldSuccessFindPagesOfReviewWithFieldCreatedAtAndAscending() {
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
  public void shouldSuccessFindPagesOfReviewWithFieldRatingAndDescending() {
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
  public void shouldSuccessFindPagesOfReviewWithFieldRatingAndAscending() {
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
  public void shouldSuccessFindReview() {
    assertEquals(reviewGetDTO2, userRepository.findReview(TEST_ID2));
  }
}
