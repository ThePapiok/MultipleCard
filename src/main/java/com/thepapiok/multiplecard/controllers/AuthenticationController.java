package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.LoginDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("login", new LoginDTO());
    return "loginPage";
  }
}
