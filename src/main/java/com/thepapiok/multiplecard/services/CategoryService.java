package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<String> getAll() {
    return categoryRepository.findAll().stream().map(Category::getName).toList();
  }
}
