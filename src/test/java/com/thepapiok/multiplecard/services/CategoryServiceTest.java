package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CategoryServiceTest {
  @Mock private CategoryRepository categoryRepository;
  private CategoryService categoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    categoryService = new CategoryService(categoryRepository);
  }

  @Test
  public void shouldSuccessAtGetAll() {
    final String category1TestName = "category1";
    final String category2TestName = "category2";
    Category category1 = new Category();
    category1.setName(category1TestName);
    Category category2 = new Category();
    category2.setName(category2TestName);
    List<Category> categories = List.of(category1, category2);
    List<String> expectedNamesOfCategories = List.of(category1TestName, category2TestName);

    when(categoryRepository.findAll()).thenReturn(categories);

    assertEquals(expectedNamesOfCategories, categoryService.getAll());
  }
}
