package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
  private Shop shop1;
  private Shop shop2;
  private Shop shop3;
  @Autowired private ShopRepository shopRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final Long amount1 = 100L;
    Address address = new Address();
    address.setApartmentNumber(null);
    address.setPostalCode("87-100");
    address.setStreet("street");
    address.setCity("city");
    address.setProvince("province");
    address.setCountry("country");
    address.setHouseNumber("1");
    shop1 = new Shop();
    shop1.setImageUrl("url1");
    shop1.setName("name1");
    shop1.setLastName("lastName1");
    shop1.setFirstName("firstName1");
    shop1.setTotalAmount(amount1);
    shop1.setAccountNumber("account1");
    shop1.setPoints(List.of(address));
    shop1 = mongoTemplate.save(shop1);
    shop2 = new Shop();
    shop2.setImageUrl("url2");
    shop2.setName("Name2");
    shop2.setLastName("lastName2");
    shop2.setFirstName("firstName2");
    shop2.setTotalAmount(amount1);
    shop2.setAccountNumber("account2");
    shop2.setPoints(List.of(address));
    shop2 = mongoTemplate.save(shop2);
    shop3 = new Shop();
    shop3.setImageUrl("url3");
    shop3.setName("other3");
    shop3.setLastName("lastName3");
    shop3.setFirstName("firstName3");
    shop3.setTotalAmount(amount1);
    shop3.setAccountNumber("account3");
    shop3.setPoints(List.of(address));
    shop3 = mongoTemplate.save(shop3);
  }

  @AfterEach
  public void cleanUp() {
    shopRepository.deleteAll();
  }

  @Test
  public void shouldReturnShopWithImageUrlAndNameAtFindImageUrlAndNameByIdWhenEverythingOk() {
    Shop expectedShop = new Shop();
    expectedShop.setImageUrl(shop1.getImageUrl());
    expectedShop.setName(shop1.getName());

    assertEquals(expectedShop, shopRepository.findImageUrlAndNameById(shop1.getId()));
  }

  @Test
  public void shouldReturnNullAtFindImageUrlAndNameByIdWhenNotFound() {
    assertNull(shopRepository.findImageUrlAndNameById(new ObjectId("123456789012345678901234")));
  }

  @Test
  public void shouldReturnListOf2ShopNamesAtGetShopNamesByPrefixWhenEverythingOk() {
    assertEquals(
        List.of(shop2.getName(), shop1.getName()), shopRepository.getShopNamesByPrefix("^name"));
  }

  @Test
  public void shouldReturnEmptyListAtGetShopNamesByPrefixWhenNotFound() {
    assertEquals(List.of(), shopRepository.getShopNamesByPrefix("^1name"));
  }
}
