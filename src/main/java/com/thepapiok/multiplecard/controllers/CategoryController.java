package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.CategoryDTO;
import com.thepapiok.multiplecard.dto.PageCategoryDTO;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ResultService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CategoryController {
  private final CategoryService categoryService;
  private final ResultService resultService;

  @Autowired
  public CategoryController(CategoryService categoryService, ResultService resultService) {
    this.categoryService = categoryService;
    this.resultService = resultService;
  }

  @PostMapping("/get_categories")
  @ResponseBody
  public ResponseEntity<List<String>> getCategories(@RequestParam String prefix) {
    return new ResponseEntity<>(categoryService.getCategoriesByPrefix(prefix), HttpStatus.OK);
  }

  @GetMapping("/categories")
  public String categoryPage(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "") String name,
      Model model) {
    final PageCategoryDTO currentPage = categoryService.getCurrentPage(page, name);
    final List<CategoryDTO> categories = currentPage.getCategories();
    final int maxPage = currentPage.getMaxPage();
    model.addAttribute("pageSelected", page + 1);
    model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
    model.addAttribute("categories", categories);
    model.addAttribute("categoriesEmpty", categories.size() == 0);
    model.addAttribute("maxPage", maxPage);
    return "categoryPage";
  }
}
