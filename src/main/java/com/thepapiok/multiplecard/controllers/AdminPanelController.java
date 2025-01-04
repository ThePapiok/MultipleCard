package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.PageUserDTO;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.services.AccountService;
import com.thepapiok.multiplecard.services.AdminPanelService;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReportService;
import com.thepapiok.multiplecard.services.ResultService;
import com.thepapiok.multiplecard.services.ReviewService;
import com.thepapiok.multiplecard.services.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminPanelController {
  private final AccountService accountService;
  private final UserService userService;
  private final MessageSource messageSource;
  private final ReportService reportService;
  private final ProductService productService;
  private final AdminPanelService adminPanelService;
  private final ReviewService reviewService;
  private final CategoryService categoryService;
  private final ResultService resultService;

  @Autowired
  public AdminPanelController(
      AccountService accountService,
      UserService userService,
      MessageSource messageSource,
      ReportService reportService,
      ProductService productService,
      AdminPanelService adminPanelService,
      ReviewService reviewService,
      CategoryService categoryService,
      ResultService resultService) {
    this.accountService = accountService;
    this.userService = userService;
    this.messageSource = messageSource;
    this.reportService = reportService;
    this.productService = productService;
    this.adminPanelService = adminPanelService;
    this.reviewService = reviewService;
    this.categoryService = categoryService;
    this.resultService = resultService;
  }

  @GetMapping("/admin_panel")
  public String adminPanel(
      @RequestParam(defaultValue = "") String type,
      @RequestParam(defaultValue = "") String value,
      @RequestParam(defaultValue = "0") int page,
      Model model) {
    final PageUserDTO currentPage = accountService.getCurrentPage(type, value, page);
    final List<UserDTO> users = currentPage.getUsers();
    final int maxPage = currentPage.getMaxPage();
    if (users.size() == 0) {
      model.addAttribute("emptyUsers", true);
    } else {
      model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
      model.addAttribute("pageSelected", page + 1);
      model.addAttribute("emptyUsers", false);
      model.addAttribute("users", users);
      model.addAttribute("maxPage", maxPage);
    }
    return "adminPanelPage";
  }

  @PostMapping("/change_user")
  @ResponseBody
  public String changeUser(
      @RequestParam String id,
      @RequestParam String type,
      @RequestParam String value,
      Locale locale) {
    switch (type) {
      case "active":
        return accountService.changeActive(id, Boolean.parseBoolean(value), locale);
      case "banned":
        return accountService.changeBanned(id, Boolean.parseBoolean(value), locale);
      case "role":
        return accountService.changeRole(id, value, locale);
      default:
        return messageSource.getMessage("error.unexpected", null, locale);
    }
  }

  @PostMapping("/report")
  @ResponseBody
  public ResponseEntity<String> reportProduct(
      @RequestParam String id,
      @RequestParam String description,
      @RequestParam boolean isProduct,
      Principal principal,
      Locale locale) {
    final int maxLength = 1000;
    final int minLength = 10;
    String phone = principal.getName();
    if (userService.checkIsRestricted(phone)) {
      return new ResponseEntity<>(
          messageSource.getMessage("reportProduct.error.isRestricted", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (description.length() > maxLength || description.length() < minLength) {
      return new ResponseEntity<>(
          messageSource.getMessage("validation.incorrect_data", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (reportService.checkIsOwner(id, phone, isProduct)) {
      return new ResponseEntity<>(
          messageSource.getMessage("reportProduct.error.is_owner", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (reportService.checkReportAlreadyExists(id, phone)) {
      return new ResponseEntity<>(
          messageSource.getMessage("reportProduct.error.already_reported", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!reportService.addReport(isProduct, id, phone, description)) {
      return new ResponseEntity<>(
          messageSource.getMessage("error.unexpected", null, locale), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(
        messageSource.getMessage("reportProduct.success.report_added", null, locale),
        HttpStatus.OK);
  }

  @PostMapping("/delete_product")
  @ResponseBody
  public Boolean deleteProduct(@RequestParam String id) {
    Account account = accountService.getAccountByProductId(id);
    String email = account.getEmail();
    if (email == null || !productService.deleteProducts(List.of(new ObjectId(id)))) {
      return false;
    }
    adminPanelService.sendInfoAboutDeletedProduct(email, account.getPhone(), id);
    return true;
  }

  @PostMapping("/block_user")
  @ResponseBody
  public Boolean blockUser(
      @RequestParam String id, @RequestParam boolean isProduct, Locale locale) {
    Account account;
    System.out.println(locale);
    if (isProduct) {
      account = accountService.getAccountByProductId(id);
      id = account.getId().toString();
    } else {
      account = accountService.getAccountById(id);
    }
    String email = account.getEmail();
    if (email == null || !accountService.changeBanned(id, true, locale).equals("ok")) {
      return false;
    }
    return true;
  }

  @PostMapping("/delete_review")
  @ResponseBody
  public Boolean deleteReview(@RequestParam String id) {
    Account account = accountService.getAccountById(id);
    String email = account.getEmail();
    if (email == null || !reviewService.removeReview(new ObjectId(id), account.getPhone())) {
      return false;
    }
    adminPanelService.sendInfoAboutDeletedReview(email, account.getPhone(), id);
    return true;
  }

  @PostMapping("/mute_user")
  @ResponseBody
  public Boolean muteUser(@RequestParam String id) {
    Account account = accountService.getAccountById(id);
    String email = account.getEmail();
    if (email == null || !userService.changeRestricted(id, true)) {
      return false;
    }
    adminPanelService.sendInfoAboutMutedUser(email, account.getPhone(), id);
    return true;
  }

  @PostMapping("/delete_category")
  @ResponseBody
  public Boolean deleteCategory(@RequestParam String name) {
    Account account = accountService.getAccountByCategoryName(name);
    String id = categoryService.getCategoryIdByName(name).toString();
    String email = account.getEmail();
    if (email == null || !productService.deleteCategoryAndProducts(id)) {
      return false;
    }
    adminPanelService.sendInfoAboutDeletedCategory(email, account.getPhone(), id);
    return true;
  }
}
