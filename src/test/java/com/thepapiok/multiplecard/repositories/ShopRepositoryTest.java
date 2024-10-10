package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.configs.DbConfig;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class ShopRepositoryTest {
  private static final String TEST_CITY = "city1";
  private static final String TEST_STREET = "street1";
  private static final String TEST_COUNTRY = "country1";
  private static final String TEST_PROVINCE = "province1";
  private static final String TEST_POSTAL_CODE = "postalCode1";
  private static final String TEST_HOUSE_NUMBER = "houseNumber1";
  @Autowired private ShopRepository shopRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    Address address1 = new Address();
    address1.setCity(TEST_CITY);
    address1.setStreet(TEST_STREET);
    address1.setCountry(TEST_COUNTRY);
    address1.setProvince(TEST_PROVINCE);
    address1.setPostalCode(TEST_POSTAL_CODE);
    address1.setHouseNumber(TEST_HOUSE_NUMBER);
    address1.setApartmentNumber(1);
    Shop shop1 = new Shop();
    shop1.setFirstName("firstName1");
    shop1.setLastName("lastName1");
    shop1.setId(new ObjectId("123456789012345678901234"));
    shop1.setName("shop1");
    shop1.setTotalAmount(0L);
    shop1.setImageUrl("sadf12312fsddfbwerewr");
    shop1.setPoints(List.of(address1));
    shop1.setAccountNumber("132345346457568657342343464575685667");
    mongoTemplate.save(shop1);
    Address address2 = new Address();
    address2.setCity("city2");
    address2.setStreet("street2");
    address2.setCountry("country2");
    address2.setProvince("province2");
    address2.setPostalCode("postalCode2");
    address2.setHouseNumber("houseNumber2");
    address2.setApartmentNumber(1);
    Shop shop2 = new Shop();
    shop2.setId(new ObjectId("123456789012345678901235"));
    shop2.setFirstName("firstName2");
    shop2.setLastName("lastName2");
    shop2.setName("shop2");
    shop2.setTotalAmount(0L);
    shop2.setImageUrl("safddb123vberewrr");
    shop2.setPoints(List.of(address2));
    shop2.setAccountNumber("12312312312312312312312312312312312312");
    mongoTemplate.save(shop2);
  }

  @AfterEach
  public void cleanUp() {
    shopRepository.deleteAll();
  }

  @Test
  public void shouldSuccessAtExistsByPointWhenFound() {
    Address address = new Address();
    address.setCity(TEST_CITY);
    address.setStreet(TEST_STREET);
    address.setCountry(TEST_COUNTRY);
    address.setProvince(TEST_PROVINCE);
    address.setPostalCode(TEST_POSTAL_CODE);
    address.setHouseNumber(TEST_HOUSE_NUMBER);
    address.setApartmentNumber(1);

    assertTrue(shopRepository.existsByPoint(address));
  }

  @Test
  public void shouldFailAtExistsByPointWhenNoFound() {
    Address address = new Address();
    address.setCity("city3");
    address.setStreet("street3");
    address.setCountry("country3");
    address.setProvince("province3");
    address.setPostalCode("postalCode3");
    address.setHouseNumber("houseNumber3");

    assertFalse(shopRepository.existsByPoint(address));
  }
}
