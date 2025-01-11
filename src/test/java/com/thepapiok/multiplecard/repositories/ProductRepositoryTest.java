package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import java.time.LocalDate;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class ProductRepositoryTest {
  private static Shop testShop;
  private Product product1;
  private Product product2;
  private Account account;
  private Category category;
  private Shop shop;
  private Promotion promotion;

  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private ShopRepository shopRepository;
  @Autowired private BlockedProductRepository blockedProductRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final int price = 500;
    final int pricePromotion = 300;
    final int quantityPromotion = 2;
    final int testYearOfCreatedAt = 2024;
    final int testMonthOfCreatedAt = 5;
    final int testDayOfCreatedAt = 5;
    final int testHourOfCreatedAt = 5;
    final int testMinuteOfCreatedAt = 1;
    final int testSecondOfCreatedAt = 1;
    final LocalDateTime localDateTime =
        LocalDateTime.of(
            testYearOfCreatedAt,
            testMonthOfCreatedAt,
            testDayOfCreatedAt,
            testHourOfCreatedAt,
            testMinuteOfCreatedAt,
            testSecondOfCreatedAt);
    Address address = new Address();
    address.setHouseNumber("0");
    address.setCountry("country1");
    address.setProvince("province1");
    address.setStreet("street1");
    address.setCity("city1");
    address.setPostalCode("postalCode1");
    address.setApartmentNumber(null);
    shop = new Shop();
    shop.setName("shop1");
    shop.setImageUrl("shopImageUrl1");
    shop.setFirstName("firstName1");
    shop.setLastName("lastName1");
    shop.setAccountNumber("accountNumber1");
    shop.setPoints(List.of(address));
    testShop = mongoTemplate.save(shop);
    category = new Category();
    category.setName("category");
    category.setOwnerId(testShop.getId());
    category = mongoTemplate.save(category);
    product1 = new Product();
    product1.setShopId(shop.getId());
    product1.setImageUrl("url1");
    product1.setName("name1");
    product1.setBarcode("barcode1");
    product1.setDescription("description1");
    product1.setPrice(price);
    product1.setCategories(List.of(category.getId()));
    product1.setUpdatedAt(localDateTime);
    product1 = mongoTemplate.save(product1);
    promotion = new Promotion();
    promotion.setNewPrice(pricePromotion);
    promotion.setQuantity(quantityPromotion);
    promotion.setProductId(product1.getId());
    promotion.setStartAt(LocalDate.now());
    promotion.setExpiredAt(LocalDate.now().plusDays(1));
    promotion = promotionRepository.save(promotion);
    product2 = new Product();
    product2.setShopId(testShop.getId());
    product2.setImageUrl("url2");
    product2.setName("name2");
    product2.setBarcode("barcode2");
    product2.setDescription("description2");
    product2.setPrice(price);
    product2.setCategories(List.of(category.getId()));
    product2.setUpdatedAt(localDateTime);
    product2 = mongoTemplate.save(product2);
    account = new Account();
    account.setBanned(false);
    account.setActive(false);
    account.setRole(Role.ROLE_SHOP);
    account.setPassword("afsdasdf123");
    account.setEmail("testEmail");
    account.setPhone("+24142134321");
    account = mongoTemplate.save(account);
  }

  @AfterEach
  public void cleanUp() {
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    promotionRepository.deleteAll();
    blockedProductRepository.deleteAll();
    shopRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void shouldReturnProductAtFindShopIdByIdWhenEverythingOk() {
    ObjectId productId;
    productId = product1.getId();
    Product expectedProduct = new Product();
    expectedProduct.setShopId(testShop.getId());

    assertEquals(expectedProduct, productRepository.findShopIdById(productId));
  }

  @Test
  public void shouldReturnProductWithOnlyPriceFieldAtFindPriceByIdWhenEverythingOk() {
    final int expectedPrice = 500;
    Product expectedProduct = new Product();
    expectedProduct.setPrice(expectedPrice);

    assertEquals(expectedProduct, productRepository.findPriceById(product1.getId()));
  }

  @Test
  public void shouldReturnAccountAtFindAccountByProductIdWhenEverythingOk() {
    Account account = new Account();
    account.setEmail(account.getEmail());
    account.setPhone(account.getPhone());
    account.setId(account.getId());

    assertEquals(account, productRepository.findAccountByProductId(product1.getId()));
  }

  @Test
  public void shouldReturnNullAtFindAccountByProductIdWhenProductNotFound() {
    assertNull(productRepository.findAccountByProductId(new ObjectId()));
  }

  @Test
  public void shouldReturnListOfProductsIdAtGetProductsIdByCategoryIdWhenEverythingOk() {
    assertEquals(
        List.of(product1.getId(), product2.getId()),
        productRepository.getProductsIdByCategoryId(category.getId()));
  }

  @Test
  public void shouldReturnProductWithShopDTOAtGetProductWithShopDTOByIdWhenEverythingOk() {
    ProductWithShopDTO productWithShopDTO = new ProductWithShopDTO();
    productWithShopDTO.setProductId(product1.getId().toString());
    productWithShopDTO.setProductName(product1.getName());
    productWithShopDTO.setProductImageUrl(product1.getImageUrl());
    productWithShopDTO.setDescription(product1.getDescription());
    productWithShopDTO.setPrice(product1.getPrice());
    productWithShopDTO.setShopId(shop.getId());
    productWithShopDTO.setShopName(shop.getName());
    productWithShopDTO.setActive(true);
    productWithShopDTO.setShopImageUrl(shop.getImageUrl());
    productWithShopDTO.setNewPricePromotion(promotion.getNewPrice());
    productWithShopDTO.setQuantityPromotion(promotion.getQuantity());
    productWithShopDTO.setStartAtPromotion(promotion.getStartAt());
    productWithShopDTO.setExpiredAtPromotion(promotion.getExpiredAt());

    assertEquals(productWithShopDTO, productRepository.getProductWithShopDTOById(product1.getId()));
  }
}
