package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Role;
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
public class CategoryRepositoryTest {
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123456789012345678901234");
  private static final String TEST_CATEGORY1_NAME = "category1";
  private static final String TEST_CATEGORY4_NAME = "Category4";

  private Category category1;
  private Account account;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private AccountRepository accountRepository;
  @MockBean private RestTemplate restTemplate;
  @Autowired private MongoTemplate mongoTemplate;

  @BeforeEach
  public void setUp() {
    category1 = new Category();
    category1.setName(TEST_CATEGORY1_NAME);
    category1.setOwnerId(TEST_OWNER_ID);
    Category category2 = new Category();
    category2.setName("1category2");
    category2.setOwnerId(TEST_OWNER_ID);
    Category category3 = new Category();
    category3.setName("Category3");
    category3.setOwnerId(TEST_OWNER_ID);
    Category category4 = new Category();
    category4.setName(TEST_CATEGORY4_NAME);
    category4.setOwnerId(TEST_OWNER_ID);
    Category category5 = new Category();
    category5.setName("Category5");
    category5.setOwnerId(TEST_OWNER_ID);
    Category category6 = new Category();
    category6.setName("1category6");
    category6.setOwnerId(TEST_OWNER_ID);
    Category category7 = new Category();
    category7.setName("1category7");
    category7.setOwnerId(TEST_OWNER_ID);
    Category category8 = new Category();
    category8.setName("1category8");
    category8.setOwnerId(TEST_OWNER_ID);
    Category category9 = new Category();
    category9.setName("1category9");
    category9.setOwnerId(TEST_OWNER_ID);
    Category category10 = new Category();
    category10.setName("1category10");
    category10.setOwnerId(TEST_OWNER_ID);
    Category category11 = new Category();
    category11.setName("1category11");
    category11.setOwnerId(TEST_OWNER_ID);
    Category category12 = new Category();
    category12.setName("1catory12");
    category12.setOwnerId(TEST_OWNER_ID);
    Category category13 = new Category();
    category13.setName("1cagory13");
    category13.setOwnerId(TEST_OWNER_ID);
    Category category14 = new Category();
    category14.setName("1category14");
    category14.setOwnerId(TEST_OWNER_ID);
    Category category15 = new Category();
    category15.setName("1categy15");
    category15.setOwnerId(TEST_OWNER_ID);
    Category category16 = new Category();
    category16.setName("1catego16");
    category16.setOwnerId(TEST_OWNER_ID);
    Category category17 = new Category();
    category17.setName("1category17");
    category17.setOwnerId(TEST_OWNER_ID);
    Category category18 = new Category();
    category18.setName("1category18");
    category18.setOwnerId(TEST_OWNER_ID);
    Category category19 = new Category();
    category19.setName("1category19");
    category19.setOwnerId(TEST_OWNER_ID);
    Category category20 = new Category();
    category20.setName("cos category");
    category20.setOwnerId(TEST_OWNER_ID);
    category1 = mongoTemplate.save(category1);
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
    account = new Account();
    account.setRole(Role.ROLE_SHOP);
    account.setId(TEST_OWNER_ID);
    account.setPhone("+21342134312434");
    account.setEmail("testEmail");
    account.setActive(true);
    account.setBanned(false);
    account.setPassword("testPassword");
    accountRepository.save(account);
  }

  @AfterEach
  public void cleanUp() {
    categoryRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void shouldReturnTrueAtCountByOwnerIsGTE20WhenFoundMoreOrEqual20Category() {
    final int count = 3;

    assertTrue(categoryRepository.countByOwnerIsGTE20(TEST_OWNER_ID, count));
  }

  @Test
  public void shouldReturnFalseAtCountByOwnerIsGTE20WhenUseExistingCategoriesAndIsLessThan20() {
    assertFalse(categoryRepository.countByOwnerIsGTE20(TEST_OWNER_ID, 0));
  }

  @Test
  public void shouldReturnNullAtCountByOwnerIsGTE20WhenNoCategories() {
    final int count = 3;
    categoryRepository.deleteAll();

    assertNull(categoryRepository.countByOwnerIsGTE20(TEST_OWNER_ID, count));
  }

  @Test
  public void shouldReturn3AtCountExistingCategoriesWhenFound3Categories() {
    final int count = 3;

    assertEquals(
        count,
        categoryRepository.countExistingCategories(
            List.of(TEST_CATEGORY1_NAME, "1category2", TEST_CATEGORY4_NAME)));
  }

  @Test
  public void shouldReturnNullAtCountExistingCategoriesWhenNotFound() {
    assertNull(categoryRepository.countExistingCategories(List.of("sda")));
  }

  @Test
  public void shouldReturnCategoryEntityWithOnlyFieldIdAtFindIdByNameWhenEverythingOk() {
    Category expectedCategory = new Category();
    expectedCategory.setId(category1.getId());

    assertEquals(expectedCategory, categoryRepository.findIdByName(TEST_CATEGORY1_NAME));
  }

  @Test
  public void shouldReturnNullAtFindIdByNameWhenNotFound() {
    assertNull(categoryRepository.findIdByName("oot"));
  }

  @Test
  public void shouldReturnListOfCategoryNamesAtGetCategoryNamesByPrefixWhenEverythingOk() {
    boolean otherName = false;
    List<String> expectedCategoryNames =
        List.of("Category3", TEST_CATEGORY4_NAME, "Category5", TEST_CATEGORY1_NAME);

    List<String> categoryNames = categoryRepository.getCategoryNamesByPrefix("^category");
    for (String name : categoryNames) {
      if (!expectedCategoryNames.contains(name)) {
        otherName = true;
        break;
      }
    }
    assertEquals(expectedCategoryNames.size(), categoryNames.size());
    assertFalse(otherName);
  }

  @Test
  public void shouldReturnEmptyListAtGetCategoryNamesByPrefixWhenNotFound() {
    assertEquals(List.of(), categoryRepository.getCategoryNamesByPrefix("^pp"));
  }

  @Test
  public void shouldReturnAccountAtFindAccountByCategoryNameWhenEverythingOk() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(account.getPhone());
    expectedAccount.setEmail(account.getEmail());

    assertEquals(
        expectedAccount, categoryRepository.findAccountByCategoryName(TEST_CATEGORY1_NAME));
  }
}
