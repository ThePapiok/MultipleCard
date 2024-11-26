package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<String> getAllNames() {
    return categoryRepository.findAll().stream().map(Category::getName).toList();
  }

  public ObjectId getCategoryIdByName(String name) {
    Category category = categoryRepository.findIdByName(name);
    if (category != null) {
      return category.getId();
    }
    return null;
  }

  public boolean checkOwnerHas20Categories(ObjectId ownerId, List<String> nameOfCategories) {
    Integer count = categoryRepository.countExistingCategories(nameOfCategories);
    if (count == null) {
      count = 0;
    }
    Boolean find = categoryRepository.countByOwnerIsGTE20(ownerId, nameOfCategories.size() - count);
    return find != null && find;
  }

  public List<String> getCategoriesByPrefix(String prefix) {
    if ("".equals(prefix)) {
      return List.of();
    }
    return categoryRepository.getCategoryNamesByPrefix("^" + prefix);
  }
}
