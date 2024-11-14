package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.services.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping("/get_categories")
  public ResponseEntity<List<String>> getCategories(@RequestParam String prefix) {
    return new ResponseEntity<>(categoryService.getCategoriesByPrefix(prefix), HttpStatus.OK);
  }
}
