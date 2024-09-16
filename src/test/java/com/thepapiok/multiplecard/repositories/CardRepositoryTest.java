package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
public class CardRepositoryTest {

  @Autowired private CardRepository cardRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @Test
  @Rollback
  public void shouldSuccessFindCarByUserId() throws JsonProcessingException {
    final ObjectId id = new ObjectId("123456789012345678901234");
    Card card = new Card();
    card.setName("Test");
    card.setPin("123sdv");
    card.setAttempts(0);
    card.setImageUrl("123fdsfasdf");
    card.setUserId(id);
    card = cardRepository.save(card);
    Address address = new Address();
    address.setCity("City");
    address.setStreet("Street");
    address.setCountry("Country");
    address.setProvince("Province");
    address.setPostalCode("111-11");
    address.setHouseNumber("1");
    User user = new User();
    user.setId(id);
    user.setCardId(card.getId());
    user.setPoints(0);
    user.setFirstName("First");
    user.setLastName("Last");
    user.setAddress(address);
    userRepository.save(user);

    assertEquals(card, cardRepository.findCardByUserId(id));
  }
}
