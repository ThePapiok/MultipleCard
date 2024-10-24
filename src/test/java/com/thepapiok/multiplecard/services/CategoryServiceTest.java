package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoryServiceTest {
  private static final String TEST_CATEGORY_NAME = "category1";
  private static final String TEST_CATEGORY_OTHER_NAME = "category2";
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123456789012345678905678");
  private static final List<String> TEST_NAME_CATEGORIES =
      List.of(TEST_CATEGORY_NAME, TEST_CATEGORY_OTHER_NAME);
  @Mock private CategoryRepository categoryRepository;
  private CategoryService categoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    categoryService = new CategoryService(categoryRepository);
  }

  @Test
  public void shouldSuccessAtGetAllNames() {
    Category category1 = new Category();
    category1.setName(TEST_CATEGORY_NAME);
    Category category2 = new Category();
    category2.setName(TEST_CATEGORY_OTHER_NAME);
    List<Category> categories = List.of(category1, category2);
    List<String> expectedNamesOfCategories = List.of(TEST_CATEGORY_NAME, TEST_CATEGORY_OTHER_NAME);

    when(categoryRepository.findAll()).thenReturn(categories);

    assertEquals(expectedNamesOfCategories, categoryService.getAllNames());
  }

  @Test
  public void shouldSuccessAtGetCategoryIdByNameWhenFoundCategory() {
    final ObjectId categoryId = new ObjectId("123456789012345678901234");
    Category category = new Category();
    category.setName(TEST_CATEGORY_NAME);
    category.setId(categoryId);

    when(categoryRepository.findIdByName(TEST_CATEGORY_NAME)).thenReturn(category);

    assertEquals(categoryId, categoryService.getCategoryIdByName(TEST_CATEGORY_NAME));
  }

  @Test
  public void shouldFailAtGetCategoryIdByNameWhenNotFoundCategory() {
    when(categoryRepository.findIdByName(TEST_CATEGORY_NAME)).thenReturn(null);

    assertNull(categoryService.getCategoryIdByName(TEST_CATEGORY_NAME));
  }

  @Test
  public void shouldSuccessAtCheckOwnerHas20Categories() {
    when(categoryRepository.countExistingCategories(TEST_NAME_CATEGORIES)).thenReturn(null);
    when(categoryRepository.countByOwnerIsEqual20(TEST_OWNER_ID, 2)).thenReturn(true);

    assertTrue(categoryService.checkOwnerHas20Categories(TEST_OWNER_ID, TEST_NAME_CATEGORIES));
  }

  @Test
  public void shouldFailAtCheckOwnerHas20CategoriesWhenLessThan20() {
    when(categoryRepository.countExistingCategories(TEST_NAME_CATEGORIES)).thenReturn(1);
    when(categoryRepository.countByOwnerIsEqual20(TEST_OWNER_ID, 1)).thenReturn(false);

    assertFalse(categoryService.checkOwnerHas20Categories(TEST_OWNER_ID, TEST_NAME_CATEGORIES));
  }

  @Test
  public void shouldFailAtCheckOwnerHas20CategoriesWhenNoFound() {
    when(categoryRepository.countExistingCategories(TEST_NAME_CATEGORIES)).thenReturn(1);
    when(categoryRepository.countByOwnerIsEqual20(TEST_OWNER_ID, 1)).thenReturn(null);

    assertFalse(categoryService.checkOwnerHas20Categories(TEST_OWNER_ID, TEST_NAME_CATEGORIES));
  }
}
