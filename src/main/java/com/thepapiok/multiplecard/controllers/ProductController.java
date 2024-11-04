package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductGetDTO;
import com.thepapiok.multiplecard.dto.PromotionGetDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ResultService;
import com.thepapiok.multiplecard.services.ShopService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ERROR_UNEXPECTED_MESSAGE = "error.unexpected";
  private static final String ERROR_NOT_OWNER_MESSAGE = "error.not_owner";
  private static final String SUCCESS_OK_MESSAGE = "ok";
  private static final String CATEGORIES_PARAM = "categories";
  private final CategoryService categoryService;
  private final ShopService shopService;
  private final MessageSource messageSource;
  private final ProductService productService;
  private final AccountRepository accountRepository;
  private final ResultService resultService;

  @Autowired
  public ProductController(
      CategoryService categoryService,
      ShopService shopService,
      MessageSource messageSource,
      ProductService productService,
      AccountRepository accountRepository,
      ResultService resultService) {
    this.categoryService = categoryService;
    this.shopService = shopService;
    this.messageSource = messageSource;
    this.productService = productService;
    this.accountRepository = accountRepository;
    this.resultService = resultService;
  }

  @GetMapping("/products")
  public String productsPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String id,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "count") String field,
      @RequestParam(defaultValue = "true") Boolean isDescending,
      @RequestParam(defaultValue = "") String text,
      HttpSession httpSession,
      Model model,
      Principal principal) {
    final String phone = principal.getName();
    int maxPage;
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
    if (id == null) {
      List<ProductGetDTO> products =
          productService.getProductsOwner(phone, page, field, isDescending, text);
      List<Promotion> promotions =
          products.stream().map(ProductGetDTO::getPromotion).filter(Objects::nonNull).toList();
      List<PromotionGetDTO> promotionGetDTOS = null;
      if (promotions.size() != 0) {
        promotionGetDTOS =
            promotions.stream()
                .map(
                    e ->
                        new PromotionGetDTO(
                            e.getProductId().toString(),
                            e.getStartAt(),
                            e.getExpiredAt(),
                            e.getAmount(),
                            e.getCount()))
                .toList();
      }
      maxPage = productService.getMaxPage(text, phone);
      model.addAttribute("field", field);
      model.addAttribute("isDescending", isDescending);
      model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
      model.addAttribute("pageSelected", page + 1);
      model.addAttribute(
          "products",
          products.stream()
              .map(e -> new ProductDTO(e.getBlocked() == null, e.getProduct()))
              .toList());
      model.addAttribute("promotions", promotionGetDTOS);
      model.addAttribute("productsSize", products.size());
      model.addAttribute("maxPage", maxPage);
      return "productsPage";
    } else {
      if (!productService.isProductOwner(principal.getName(), id)) {
        return "redirect:/products";
      }
      Product product = productService.getProductById(id);
      model.addAttribute(CATEGORIES_PARAM, categoryService.getAllNames());
      model.addAttribute("productCategories", productService.getCategoriesNames(product));
      model.addAttribute("product", product);
      return "productPage";
    }
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
    model.addAttribute(CATEGORIES_PARAM, categoryService.getAllNames());
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
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale));
      return "redirect:/products?error";
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("addProductPage.success.add_product", null, locale));
    return "redirect:/products?success";
  }

  @DeleteMapping("/products")
  @ResponseBody
  public String deleteProduct(@RequestParam String id, Locale locale, Principal principal) {
    if (!productService.isProductOwner(principal.getName(), id)) {
      return messageSource.getMessage(ERROR_NOT_OWNER_MESSAGE, null, locale);
    } else if (!productService.deleteProduct(id)) {
      return messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale);
    }
    return SUCCESS_OK_MESSAGE;
  }

  @PostMapping("/block_product")
  @ResponseBody
  public String blockProduct(@RequestParam String id, Locale locale, Principal principal) {
    if (!productService.isProductOwner(principal.getName(), id)) {
      return messageSource.getMessage(ERROR_NOT_OWNER_MESSAGE, null, locale);
    } else if (productService.hasBlock(id)) {
      return messageSource.getMessage("blockProduct.error.block_already", null, locale);
    } else if (!productService.blockProduct(id)) {
      return messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale);
    }
    return SUCCESS_OK_MESSAGE;
  }

  @PostMapping("/unblock_product")
  @ResponseBody
  public String unblockProduct(@RequestParam String id, Locale locale, Principal principal) {
    if (!productService.isProductOwner(principal.getName(), id)) {
      return messageSource.getMessage(ERROR_NOT_OWNER_MESSAGE, null, locale);
    } else if (!productService.hasBlock(id)) {
      return messageSource.getMessage("blockProduct.error.no_block", null, locale);
    } else if (!productService.unblockProduct(id)) {
      return messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale);
    }
    return SUCCESS_OK_MESSAGE;
  }
}
