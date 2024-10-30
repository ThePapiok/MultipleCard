package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.configs.DbConfig;
import java.util.List;
import org.bson.types.ObjectId;
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
public class ProductRepositoryTest {

  @Autowired private ProductRepository productRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @Test
  public void shouldReturnProductAtFindShopIdById() {
    final ObjectId shopId = new ObjectId("123456789012345678901234");
    final int amount = 500;
    ObjectId productId;
    Category category = new Category();
    category.setName("category");
    category.setOwnerId(shopId);
    mongoTemplate.save(category);
    Product product = new Product();
    product.setShopId(shopId);
    product.setActive(true);
    product.setImageUrl("url");
    product.setName("name");
    product.setBarcode("barcode");
    product.setDescription("description");
    product.setAmount(amount);
    product.setCategories(List.of(category.getId()));
    product = mongoTemplate.save(product);
    productId = product.getId();
    Product expectedProduct = new Product();
    expectedProduct.setShopId(shopId);

    assertEquals(expectedProduct, productRepository.findShopIdById(productId));
  }
}
