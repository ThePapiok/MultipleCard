package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.services.AccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminPanelController {
  private final AccountService accountService;

  @Autowired
  public AdminPanelController(AccountService accountService) {
    this.accountService = accountService;
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
}
