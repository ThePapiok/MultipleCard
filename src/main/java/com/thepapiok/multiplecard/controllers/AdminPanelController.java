package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.services.AccountService;
import com.thepapiok.multiplecard.services.AdminPanelService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReportService;
import com.thepapiok.multiplecard.services.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
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

  @Autowired
  public AdminPanelController(
      AccountService accountService,
      UserService userService,
      MessageSource messageSource,
      ReportService reportService,
      ProductService productService,
      AdminPanelService adminPanelService) {
    this.accountService = accountService;
    this.userService = userService;
    this.messageSource = messageSource;
    this.reportService = reportService;
    this.productService = productService;
    this.adminPanelService = adminPanelService;
  }

  @GetMapping("/admin_panel")
  public String adminPanel(
      @RequestParam(required = false) Integer type,
      @RequestParam(required = false) String value,
      Model model) {
    List<UserDTO> users = accountService.getUsers(type, value);
    if (users.size() == 0) {
      model.addAttribute("emptyUsers", true);
    } else {
      model.addAttribute("emptyUsers", false);
      model.addAttribute("users", users);
    }
    return "adminPanelPage";
  }

  @PostMapping("/change_user")
  @ResponseBody
  public Boolean changeUser(
      @RequestParam String id, @RequestParam boolean type, @RequestParam boolean value) {
    if (type) {
      return accountService.changeActive(id, value);
    } else {
      return accountService.changeBanned(id, value);
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
    if (email == null || !productService.deleteProduct(id)) {
      return false;
    }
    adminPanelService.sendInfoAboutDeletedProduct(email, account.getPhone(), id);
    return true;
  }
}
