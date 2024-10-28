package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.configs.DbConfig;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
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
public class CategoryRepositoryTest {
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123456789012345678901234");
  private static final String TEST_CATEGORY1_NAME = "category1";
  private static final String TEST_CATEGORY2_NAME = "category2";
  private static final String TEST_CATEGORY3_NAME = "category3";
  private static final String TEST_CATEGORY4_NAME = "category4";
  private static final String TEST_CATEGORY5_NAME = "category5";
  private static final String TEST_CATEGORY6_NAME = "category6";
  private static final String TEST_CATEGORY7_NAME = "category7";
  private static final String TEST_CATEGORY8_NAME = "category8";
  private static final String TEST_CATEGORY9_NAME = "category9";
  private static final String TEST_CATEGORY10_NAME = "category10";
  private static final String TEST_CATEGORY11_NAME = "category11";
  private static final String TEST_CATEGORY12_NAME = "category12";
  private static final String TEST_CATEGORY13_NAME = "category13";
  private static final String TEST_CATEGORY14_NAME = "category14";
  private static final String TEST_CATEGORY15_NAME = "category15";
  private static final String TEST_CATEGORY16_NAME = "category16";
  private static final String TEST_CATEGORY17_NAME = "category17";
  private static final String TEST_CATEGORY18_NAME = "category18";
  private static final String TEST_CATEGORY19_NAME = "category19";
  private static final String TEST_CATEGORY20_NAME = "category20";
  @Autowired private CategoryRepository categoryRepository;
  @MockBean private RestTemplate restTemplate;
  @Autowired private MongoTemplate mongoTemplate;

  @AfterEach
  public void cleanUp() {
    categoryRepository.deleteAll();
  }

  @Test
  public void shouldReturnTrueAtCountByOwnerIsGTE20WhenFoundMoreOrEqual20Category() {
    final int count = 3;
    Category category1 = new Category();
    category1.setName(TEST_CATEGORY1_NAME);
    category1.setOwnerId(TEST_OWNER_ID);
    Category category2 = new Category();
    category2.setName(TEST_CATEGORY2_NAME);
    category2.setOwnerId(TEST_OWNER_ID);
    Category category3 = new Category();
    category3.setName(TEST_CATEGORY3_NAME);
    category3.setOwnerId(TEST_OWNER_ID);
    Category category4 = new Category();
    category4.setName(TEST_CATEGORY4_NAME);
    category4.setOwnerId(TEST_OWNER_ID);
    Category category5 = new Category();
    category5.setName(TEST_CATEGORY5_NAME);
    category5.setOwnerId(TEST_OWNER_ID);
    Category category6 = new Category();
    category6.setName(TEST_CATEGORY6_NAME);
    category6.setOwnerId(TEST_OWNER_ID);
    Category category7 = new Category();
    category7.setName(TEST_CATEGORY7_NAME);
    category7.setOwnerId(TEST_OWNER_ID);
    Category category8 = new Category();
    category8.setName(TEST_CATEGORY8_NAME);
    category8.setOwnerId(TEST_OWNER_ID);
    Category category9 = new Category();
    category9.setName(TEST_CATEGORY9_NAME);
    category9.setOwnerId(TEST_OWNER_ID);
    Category category10 = new Category();
    category10.setName(TEST_CATEGORY10_NAME);
    category10.setOwnerId(TEST_OWNER_ID);
    Category category11 = new Category();
    category11.setName(TEST_CATEGORY11_NAME);
    category11.setOwnerId(TEST_OWNER_ID);
    Category category12 = new Category();
    category12.setName(TEST_CATEGORY12_NAME);
    category12.setOwnerId(TEST_OWNER_ID);
    Category category13 = new Category();
    category13.setName(TEST_CATEGORY13_NAME);
    category13.setOwnerId(TEST_OWNER_ID);
    Category category14 = new Category();
    category14.setName(TEST_CATEGORY14_NAME);
    category14.setOwnerId(TEST_OWNER_ID);
    Category category15 = new Category();
    category15.setName(TEST_CATEGORY15_NAME);
    category15.setOwnerId(TEST_OWNER_ID);
    Category category16 = new Category();
    category16.setName(TEST_CATEGORY16_NAME);
    category16.setOwnerId(TEST_OWNER_ID);
    Category category17 = new Category();
    category17.setName(TEST_CATEGORY17_NAME);
    category17.setOwnerId(TEST_OWNER_ID);
    Category category18 = new Category();
    category18.setName(TEST_CATEGORY18_NAME);
    category18.setOwnerId(TEST_OWNER_ID);
    Category category19 = new Category();
    category19.setName(TEST_CATEGORY19_NAME);
    category19.setOwnerId(TEST_OWNER_ID);
    Category category20 = new Category();
    category20.setName(TEST_CATEGORY20_NAME);
    category20.setOwnerId(TEST_OWNER_ID);
    mongoTemplate.save(category1);
    mongoTemplate.save(category2);
    mongoTemplate.save(category3);
    mongoTemplate.save(category4);
    mongoTemplate.save(category5);
    mongoTemplate.save(category6);
    mongoTemplate.save(category7);
    mongoTemplate.save(category8);
    mongoTemplate.save(category9);
    mongoTemplate.save(category10);
    mongoTemplate.save(category11);
    mongoTemplate.save(category12);
    mongoTemplate.save(category13);
    mongoTemplate.save(category14);
    mongoTemplate.save(category15);
    mongoTemplate.save(category16);
    mongoTemplate.save(category17);
    mongoTemplate.save(category18);
    mongoTemplate.save(category19);

    assertTrue(categoryRepository.countByOwnerIsGTE20(TEST_OWNER_ID, count));
  }

  @Test
  public void shouldReturnFalseAtCountByOwnerIsGTE20WhenUseExistingCategoriesAndIsLessThan20() {
    Category category1 = new Category();
    category1.setName(TEST_CATEGORY1_NAME);
    category1.setOwnerId(TEST_OWNER_ID);
    mongoTemplate.save(category1);

    assertFalse(categoryRepository.countByOwnerIsGTE20(TEST_OWNER_ID, 0));
  }

  @Test
  public void shouldReturnFalseAtCountByOwnerIsGTE20WhenNoCategories() {
    final int count = 3;

    assertNull(categoryRepository.countByOwnerIsGTE20(TEST_OWNER_ID, count));
  }

  @Test
  public void shouldReturn2AtCountExistingCategoriesWhenFound2Categories() {
    Category category1 = new Category();
    category1.setName(TEST_CATEGORY1_NAME);
    category1.setOwnerId(TEST_OWNER_ID);
    Category category2 = new Category();
    category2.setName(TEST_CATEGORY2_NAME);
    category2.setOwnerId(TEST_OWNER_ID);
    Category category3 = new Category();
    category3.setName(TEST_CATEGORY3_NAME);
    category3.setOwnerId(TEST_OWNER_ID);
    mongoTemplate.save(category1);
    mongoTemplate.save(category2);
    mongoTemplate.save(category3);

    assertEquals(
        2,
        categoryRepository.countExistingCategories(
            List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY4_NAME)));
  }

  @Test
  public void shouldReturnNullAtCountExistingCategoriesWhenNotFound() {
    assertNull(categoryRepository.countExistingCategories(List.of(TEST_CATEGORY1_NAME)));
  }

  @Test
  public void shouldReturnCategoryEntityWithOnlyFieldIdAtFindIdByNameWhenEverythingOk() {
    Category category1 = new Category();
    category1.setName(TEST_CATEGORY1_NAME);
    category1.setOwnerId(TEST_OWNER_ID);
    category1 = mongoTemplate.save(category1);
    Category expectedCategory = new Category();
    expectedCategory.setId(category1.getId());

    assertEquals(expectedCategory, categoryRepository.findIdByName(TEST_CATEGORY1_NAME));
  }

  @Test
  public void shouldReturnNullAtFindIdByNameWhenNotFound() {
    assertNull(categoryRepository.findIdByName(TEST_CATEGORY1_NAME));
  }
}
