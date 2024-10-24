package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ShopService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private final CategoryService categoryService;
  private final ShopService shopService;
  private final MessageSource messageSource;
  private final ProductService productService;
  private final AccountRepository accountRepository;

  @Autowired
  public ProductController(
      CategoryService categoryService,
      ShopService shopService,
      MessageSource messageSource,
      ProductService productService,
      AccountRepository accountRepository) {
    this.categoryService = categoryService;
    this.shopService = shopService;
    this.messageSource = messageSource;
    this.productService = productService;
    this.accountRepository = accountRepository;
  }

  @GetMapping("/products")
  public String productsPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      HttpSession httpSession,
      Model model) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    } else if (success != null) {
      String message = (String) httpSession.getAttribute(SUCCESS_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(SUCCESS_MESSAGE_PARAM, message);
        httpSession.removeAttribute(SUCCESS_MESSAGE_PARAM);
      }
    }
    return "productsPage";
  }

  @GetMapping("/add_product")
  public String addProductPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    }
    model.addAttribute("categories", categoryService.getAllNames());
    model.addAttribute("addProduct", new AddProductDTO());
    return "addProductPage";
  }

  @PostMapping("/add_product")
  public String addProduct(
      @Valid AddProductDTO addProductDTO,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    final int maxSize = 3;
    final ObjectId ownerId = accountRepository.findIdByPhone(principal.getName()).getId();
    final String errorValidationMessage = "validation.incorrect_data";
    List<String> categories = addProductDTO.getCategory();
    boolean error = false;
    String message = null;
    Pattern patternCategory =
        Pattern.compile("^[A-ZĄĆĘŁŃÓŚŹŻ]([a-ząćęłńóśźż]*|[a-ząćęłńóśźż]* [a-ząćęłńóśźż]+)$");
    if (bindingResult.hasErrors()) {
      error = true;
      message = messageSource.getMessage(errorValidationMessage, null, locale);
    } else if (!categories.stream().allMatch(e -> patternCategory.matcher(e).matches())) {
      error = true;
      message = messageSource.getMessage(errorValidationMessage, null, locale);
    } else if (categories.size() == 0 || categories.size() > maxSize) {
      error = true;
      message = messageSource.getMessage("addProductPage.error.category.bad_size", null, locale);
    } else if (new HashSet<>(categories).size() != categories.size()) {
      error = true;
      message = messageSource.getMessage("addProductPage.error.category.unique", null, locale);
    } else if (!shopService.checkImage(addProductDTO.getFile())) {
      error = true;
      message = messageSource.getMessage("error.bad_file", null, locale);
    } else if (categoryService.checkOwnerHas20Categories(ownerId, categories)) {
      error = true;
      message = messageSource.getMessage("addProductPage.error.category.too_many", null, locale);
    } else if (productService.checkOwnerHasTheSameNameProduct(ownerId, addProductDTO.getName())) {
      error = true;
      message =
          messageSource.getMessage("addProductPage.error.product.the_same_name", null, locale);
    } else if (productService.checkOwnerHasTheSameBarcode(ownerId, addProductDTO.getBarcode())) {
      error = true;
      message =
          messageSource.getMessage("addProductPage.error.product.the_same_barcode", null, locale);
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return "redirect:/add_product?error";
    }
    if (!productService.addProduct(addProductDTO, ownerId, categories)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return "redirect:/products?error";
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("addProductPage.success.add_product", null, locale));
    return "redirect:/products?success";
  }
}
