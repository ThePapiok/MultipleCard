package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.configs.DbConfig;
import java.util.List;
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

  @Autowired private ShopRepository shopRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @Test
  public void shouldReturnShopWithImageUrlAndNameAtFindImageUrlAndNameByIdWhenEverythingOk() {
    final Long amount1 = 100L;
    final String imageUrl = "url1";
    final String shopName = "name1";
    Address address = new Address();
    address.setApartmentNumber(null);
    address.setPostalCode("87-100");
    address.setStreet("street");
    address.setCity("city");
    address.setProvince("province");
    address.setCountry("country");
    address.setHouseNumber("1");
    Shop shop = new Shop();
    shop.setImageUrl(imageUrl);
    shop.setName(shopName);
    shop.setLastName("lastName1");
    shop.setFirstName("firstName1");
    shop.setTotalAmount(amount1);
    shop.setAccountNumber("account1");
    shop.setPoints(List.of(address));
    shop = mongoTemplate.save(shop);
    Shop expectedShop = new Shop();
    expectedShop.setImageUrl(imageUrl);
    expectedShop.setName(shopName);

    assertEquals(expectedShop, shopRepository.findImageUrlAndNameById(shop.getId()));
  }
}
