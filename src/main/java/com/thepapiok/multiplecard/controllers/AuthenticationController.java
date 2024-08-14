package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.LoginDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

  private final CountryService countryService;

  @Autowired
  public AuthenticationController(CountryService countryService) {
    this.countryService = countryService;
  }

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("login", new LoginDTO());
    return "loginPage";
  }

  @GetMapping("/register")
  public String register(Model model) {
    model.addAttribute("countries", countryService.getDTOs());
    model.addAttribute("register", new RegisterDTO());
    return "registerPage";
  }
}
