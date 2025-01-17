package com.thepapiok.multiplecard.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.dto.PageProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsWithShopDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ProfileService;
import com.thepapiok.multiplecard.services.ResultService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String ERROR_VALIDATION_MESSAGE = "validation.incorrect_data";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ERROR_UNEXPECTED_MESSAGE = "error.unexpected";
  private static final String ERROR_NOT_OWNER_MESSAGE = "error.not_owner";
  private static final String SUCCESS_OK_MESSAGE = "ok";
  private static final String PRODUCT_PARAM = "product";
  private final CategoryService categoryService;
  private final ShopService shopService;
  private final MessageSource messageSource;
  private final ProductService productService;
  private final AccountRepository accountRepository;
  private final ResultService resultService;
  private final ProfileService profileService;

  @Autowired
  public ProductController(
      CategoryService categoryService,
      ShopService shopService,
      MessageSource messageSource,
      ProductService productService,
      AccountRepository accountRepository,
      ResultService resultService,
      ProfileService profileService) {
    this.categoryService = categoryService;
    this.shopService = shopService;
    this.messageSource = messageSource;
    this.productService = productService;
    this.accountRepository = accountRepository;
    this.resultService = resultService;
    this.profileService = profileService;
  }

  @GetMapping("/products")
  public String productsPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String id,
      @RequestParam(defaultValue = "") String category,
      @RequestParam(defaultValue = "") String shopName,
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
    if (profileService.checkRole(phone, Role.ROLE_SHOP)) {
      if (id == null) {
        PageProductsDTO currentPage =
            productService.getProducts(phone, page, field, isDescending, text, "", "", false);
        List<ProductDTO> allProducts = currentPage.getProducts();
        maxPage = currentPage.getMaxPage();
        model.addAttribute("field", field);
        model.addAttribute("isDescending", isDescending);
        model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
        model.addAttribute("pageSelected", page + 1);
        model.addAttribute("products", allProducts);
        model.addAttribute("productsEmpty", allProducts.size() == 0);
        model.addAttribute("maxPage", maxPage);
        return "productsPage";
      } else {
        if (!productService.isProductOwner(principal.getName(), id)) {
          return "redirect:/products";
        }
        Product product = productService.getProductById(id);
        model.addAttribute("categories", categoryService.getAllNames());
        model.addAttribute("productCategories", productService.getCategoriesNames(product));
        model.addAttribute(PRODUCT_PARAM, productService.getEditProductDTO(product));
        model.addAttribute("id", id);
        return "productPage";
      }
    } else {
      PageProductsWithShopDTO currentPage =
          productService.getProductsWithShops(page, field, isDescending, text, category, shopName);
      maxPage = currentPage.getMaxPage();
      List<ProductWithShopDTO> allProducts = currentPage.getProducts();
      model.addAttribute("field", field);
      model.addAttribute("isDescending", isDescending);
      model.addAttribute("pageSelected", page + 1);
      model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
      model.addAttribute("products", allProducts);
      model.addAttribute("productsEmpty", allProducts.size() == 0);
      model.addAttribute("maxPage", maxPage);
      return "productsAdminPage";
    }
  }

  @GetMapping("/add_product")
  public String addProductPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    final String addProductParam = "addProduct";
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
        AddProductDTO addProductDTO = (AddProductDTO) httpSession.getAttribute(PRODUCT_PARAM);
        model.addAttribute(addProductParam, addProductDTO);
        model.addAttribute("productCategories", addProductDTO.getCategory());
        httpSession.removeAttribute(PRODUCT_PARAM);
      }
    }
    if (model.getAttribute(addProductParam) == null) {
      model.addAttribute(addProductParam, new AddProductDTO());
    }
    model.addAttribute("categories", categoryService.getAllNames());
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
    final int minLengthOfCategory = 2;
    final int maxLengthOfCategory = 30;
    final ObjectId ownerId = accountRepository.findIdByPhone(principal.getName()).getId();
    List<String> categories = addProductDTO.getCategory();
    boolean error = false;
    String message = null;
    httpSession.setAttribute(PRODUCT_PARAM, addProductDTO);
    if (bindingResult.hasErrors()) {
      error = true;
      message = messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale);
    } else if (!categories.stream()
        .allMatch(
            e ->
                (Pattern.compile(
                            "^[A-ZĄĆĘŁŃÓŚŹŻ]([a-ząćęłńóśźż]*|[a-ząćęłńóśźż]* [a-ząćęłńóśźż]+)$")
                        .matcher(e)
                        .matches()
                    && e.length() >= minLengthOfCategory
                    && e.length() <= maxLengthOfCategory))) {
      error = true;
      message = messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale);
    } else if (categories.size() == 0 || categories.size() > maxSize) {
      error = true;
      message = messageSource.getMessage("error.category.bad_size", null, locale);
    } else if (new HashSet<>(categories).size() != categories.size()) {
      error = true;
      message = messageSource.getMessage("error.category.unique", null, locale);
    } else if (!shopService.checkImage(addProductDTO.getFile())) {
      error = true;
      message = messageSource.getMessage("error.bad_file", null, locale);
    } else if (categoryService.checkOwnerHas20Categories(ownerId, categories)) {
      error = true;
      message = messageSource.getMessage("error.category.too_many", null, locale);
    } else if (productService.checkOwnerHasTheSameNameProduct(ownerId, addProductDTO.getName())) {
      error = true;
      message = messageSource.getMessage("error.product.the_same_name", null, locale);
    } else if (productService.checkOwnerHasTheSameBarcode(ownerId, addProductDTO.getBarcode())) {
      error = true;
      message = messageSource.getMessage("error.product.the_same_barcode", null, locale);
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
    } else if (!productService.deleteProducts(List.of(new ObjectId(id)))) {
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

  @PostMapping("/products")
  public String editProduct(
      @Valid @ModelAttribute EditProductDTO editProductDTO,
      BindingResult bindingResult,
      Locale locale,
      Principal principal,
      HttpSession httpSession) {
    final String id = editProductDTO.getId();
    final String name = editProductDTO.getName();
    final String barcode = editProductDTO.getBarcode();
    final ObjectId ownerId = accountRepository.findIdByPhone(principal.getName()).getId();
    final int maxSize = 3;
    final int minLengthOfCategory = 2;
    final int maxLengthOfCategory = 30;
    boolean error = false;
    String message = null;
    List<String> categories = editProductDTO.getCategory();
    Product originalProduct = productService.getProductById(id);
    if (bindingResult.hasErrors()) {
      error = true;
      message = messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale);
    } else if (!categories.stream()
        .allMatch(
            e ->
                (Pattern.compile(
                            "^[A-ZĄĆĘŁŃÓŚŹŻ]([a-ząćęłńóśźż]*|[a-ząćęłńóśźż]* [a-ząćęłńóśźż]+)$")
                        .matcher(e)
                        .matches()
                    && e.length() >= minLengthOfCategory
                    && e.length() <= maxLengthOfCategory))) {
      error = true;
      message = messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale);
    } else if (categories.size() == 0 || categories.size() > maxSize) {
      error = true;
      message = messageSource.getMessage("error.category.bad_size", null, locale);
    } else if (new HashSet<>(categories).size() != categories.size()) {
      error = true;
      message = messageSource.getMessage("error.category.unique", null, locale);
    } else if (categoryService.checkOwnerHas20Categories(ownerId, categories)) {
      error = true;
      message = messageSource.getMessage("error.category.too_many", null, locale);
    } else if (!originalProduct.getName().equals(name)
        && productService.checkOwnerHasTheSameNameProduct(ownerId, name)) {
      error = true;
      message = messageSource.getMessage("error.product.the_same_name", null, locale);
    } else if (!originalProduct.getBarcode().equals(barcode)
        && productService.checkOwnerHasTheSameBarcode(ownerId, barcode)) {
      error = true;
      message = messageSource.getMessage("error.product.the_same_barcode", null, locale);
    } else if (!editProductDTO.getFile().isEmpty()
        && !shopService.checkImage(editProductDTO.getFile())) {
      error = true;
      message = messageSource.getMessage("error.bad_file", null, locale);
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return "redirect:/products?id=" + id + "&error";
    }
    if (!productService.editProduct(editProductDTO, ownerId, categories)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_MESSAGE, null, locale));
      return "redirect:/products?error";
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("productPage.success.edit_product", null, locale));
    return "redirect:/products?success";
  }

  @PostMapping("/get_products")
  @ResponseBody
  public ResponseEntity<List<ProductWithShopDTO>> getProducts(
      @RequestBody List<String> productsInfo) throws JsonProcessingException {
    return new ResponseEntity<>(productService.getProductsByIds(productsInfo), HttpStatus.OK);
  }
}
