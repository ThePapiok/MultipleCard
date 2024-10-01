package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.configs.DbConfig;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class CardRepositoryTest {
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");

  @Autowired private CardRepository cardRepository;
  @Autowired private UserRepository userRepository;
  @MockBean private RestTemplate restTemplate;
  @Autowired private MongoTemplate mongoTemplate;
  private Card expectedCard;

  @BeforeEach
  public void setUp() {
    expectedCard = new Card();
    expectedCard.setName("Test");
    expectedCard.setPin("123sdv");
    expectedCard.setAttempts(0);
    expectedCard.setImageUrl("123fdsfasdf");
    expectedCard.setUserId(TEST_ID);
    expectedCard = mongoTemplate.save(expectedCard);
    Address address = new Address();
    address.setCity("City");
    address.setStreet("Street");
    address.setCountry("Country");
    address.setProvince("Province");
    address.setPostalCode("111-11");
    address.setHouseNumber("1");
    User user = new User();
    user.setId(TEST_ID);
    user.setCardId(expectedCard.getId());
    user.setPoints(0);
    user.setFirstName("First");
    user.setLastName("Last");
    user.setAddress(address);
    mongoTemplate.save(user);
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
    cardRepository.deleteAll();
  }

  @Test
  public void shouldSuccessFindCarByUserId() {
    assertEquals(expectedCard, cardRepository.findCardByUserId(TEST_ID));
  }
}
