package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductController {

  private final CategoryService categoryService;

  @Autowired
  public ProductController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping("/products")
  public String productsPage() {
    return "productsPage";
  }

  @GetMapping("/add_product")
  public String addProductPage(Model model) {
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("addProduct", new AddProductDTO());
    return "addProductPage";
  }

  @PostMapping("/add_product")
  public String addProduct(AddProductDTO addProductDTO) {
    System.out.println(addProductDTO);
    return "redirect:/login";
  }
}
