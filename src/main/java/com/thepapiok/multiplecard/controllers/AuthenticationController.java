package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.LoginDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

  private final CountryService countryService;
  private final AuthenticationService authenticationService;

  @Autowired
  public AuthenticationController(
      CountryService countryService, AuthenticationService authenticationService) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
  }

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("login", new LoginDTO());
    return "loginPage";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("countries", countryService.getDTOs());
    model.addAttribute("register", new RegisterDTO());
    return "registerPage";
  }

  @PostMapping("/register")
  public String createUser(@ModelAttribute RegisterDTO register) {
    authenticationService.createUser(register);
    return "redirect:/login";
  }
}
