package com.thepapiok.multiplecard.repositories;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
public class UserRepositoryTest {
  /*
  private static final String TEST_ID1 = "123456789012345678901234";
  private static final String TEST_ID2 = "123456789012345678901235";
  private static final String TEST_ID3 = "123456789012345678901236";
  private static final String TEST_ID4 = "123456789012345678901237";
  private static final String TEST1_TEXT = "Test1";
  private static final String TEST2_TEXT = "Test2";
  private static final String TEST3_TEXT = "Test3";
  private static final String TEST4_TEXT = "Test4";
  private static final String FIELD_COUNT = "count";
  private static final String FIELD_CREATED_ADT = "review.createdAt";
  private static final String FIELD_RATING = "review.rating";

  private static final ObjectId TEST_OBJECT_ID1 = new ObjectId(TEST_ID1);
  private static final ObjectId TEST_OBJECT_ID2 = new ObjectId(TEST_ID2);
  private static final ObjectId TEST_OBJECT_ID3 = new ObjectId(TEST_ID3);
  private static ReviewGetDTO reviewGetDTO1;
  private static ReviewGetDTO reviewGetDTO2;
  private static ReviewGetDTO reviewGetDTO3;
  private static boolean firstTime;
  @Autowired private UserRepository userRepository;
  @Autowired private LikeRepository likeRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    if (!firstTime) {
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
      firstTime = true;
      userRepository.deleteAll();
      likeRepository.deleteAll();
      Review review1 = new Review();
      review1.setDescription(TEST1_TEXT);
      review1.setCreatedAt(LocalDateTime.of(year1, month1, dayOfMonth1, hour1, minute1));
      review1.setRating(rating1);
      User user1 = new User();
      user1.setFirstName(TEST1_TEXT);
      user1.setId(TEST_ID1);
      user1.setReview(review1);
      userRepository.save(user1);
      Review review2 = new Review();
      review2.setDescription(TEST1_TEXT);
      review2.setCreatedAt(LocalDateTime.of(year2, month2, dayOfMonth2, hour2, minute2));
      review2.setRating(rating2);
      User user2 = new User();
      user2.setFirstName(TEST2_TEXT);
      user2.setId(TEST_ID2);
      user2.setReview(review2);
      userRepository.save(user2);
      Review review3 = new Review();
      review3.setDescription("b");
      review3.setCreatedAt(LocalDateTime.of(year3, monht3, dayOfMonth3, hour3, minute3));
      review3.setRating(rating3);
      User user3 = new User();
      user3.setFirstName(TEST3_TEXT);
      user3.setId(TEST_ID3);
      user3.setReview(review3);
      userRepository.save(user3);
      User user4 = new User();
      user4.setFirstName(TEST4_TEXT);
      user4.setId(TEST_ID4);
      userRepository.save(user4);
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
      TextIndexDefinition textIndex =
          new TextIndexDefinition.TextIndexDefinitionBuilder()
              .onField("review.description")
              .withDefaultLanguage("none")
              .build();
      mongoTemplate.indexOps(User.class).ensureIndex(textIndex);
    }
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldCountAndDescending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_OBJECT_ID1, FIELD_COUNT, -1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_OBJECT_ID1, FIELD_COUNT, -1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldCountAndAscending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO3, reviewGetDTO2, reviewGetDTO1);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO2, reviewGetDTO1);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_OBJECT_ID1, FIELD_COUNT, 1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_OBJECT_ID1, FIELD_COUNT, 1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldCreatedAtAndDescending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO3, reviewGetDTO2, reviewGetDTO1);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO2, reviewGetDTO1);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_OBJECT_ID1, FIELD_CREATED_ADT, -1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_OBJECT_ID1, FIELD_CREATED_ADT, -1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldCreatedAtAndAscending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO1, reviewGetDTO2, reviewGetDTO3);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_OBJECT_ID1, FIELD_CREATED_ADT, 1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_OBJECT_ID1, FIELD_CREATED_ADT, 1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldRatingAndDescending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO2, reviewGetDTO1, reviewGetDTO3);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO2, reviewGetDTO1);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_OBJECT_ID1, FIELD_RATING, -1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_OBJECT_ID1, FIELD_RATING, -1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessFindPagesOfReviewWithFieldRatingAndAscending() {
    List<ReviewGetDTO> expectedWithoutText = List.of(reviewGetDTO3, reviewGetDTO1, reviewGetDTO2);
    List<ReviewGetDTO> expectedWithText = List.of(reviewGetDTO1, reviewGetDTO2);

    assertEquals(
        expectedWithoutText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheck(
            TEST_OBJECT_ID1, FIELD_RATING, 1, 0));
    assertEquals(
        expectedWithText,
        userRepository.findPageOfReviewWithCountAndIsAddedCheckWithText(
            TEST_OBJECT_ID1, FIELD_RATING, 1, 0, TEST1_TEXT));
  }

  @Test
  public void shouldSuccessFindReview() {
    assertEquals(reviewGetDTO2, userRepository.findReview(TEST_OBJECT_ID2));
  }

   */
}
