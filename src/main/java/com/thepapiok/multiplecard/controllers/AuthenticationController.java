package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.LoginDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {

  private final CountryService countryService;
  private final AuthenticationService authenticationService;
  private final String errorMessage = "errorMessage";
  private final String successMessage = "successMessage";
  private final String register = "register";

  @Autowired
  public AuthenticationController(
      CountryService countryService, AuthenticationService authenticationService) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
  }

  @GetMapping("/login")
  public String login(
      @RequestParam(required = false) String success, Model model, HttpSession httpSession) {
    if (success != null) {
      model.addAttribute(successMessage, httpSession.getAttribute(successMessage));
      httpSession.removeAttribute(successMessage);
    }
    model.addAttribute("login", new LoginDTO());
    return "loginPage";
  }

  @GetMapping("/register")
  public String registerPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    if (error != null) {
      model.addAttribute(errorMessage, httpSession.getAttribute(errorMessage));
      httpSession.removeAttribute(errorMessage);
      model.addAttribute(register, httpSession.getAttribute(register));
      httpSession.removeAttribute(register);
    } else {
      model.addAttribute(register, new RegisterDTO());
    }
    model.addAttribute("countries", countryService.getDTOs());
    return "registerPage";
  }

  @PostMapping("/register")
  public String createUser(
      @Valid @ModelAttribute RegisterDTO register,
      BindingResult bindingResult,
      HttpSession httpSession) {
    boolean error = false;
    String message = null;
    if (bindingResult.hasErrors()) {
      error = true;
      message = "Podane dane są niepoprawne";
    } else if (authenticationService.getPhones().contains(register.getPhone())) {
      error = true;
      message = "Użytkownik o takim numerze telefonu już istnieje";
    }
    if (error) {
      httpSession.setAttribute(errorMessage, message);
      httpSession.setAttribute(this.register, register);
      return "redirect:/register?error";
    }
    // authenticationService.createUser(register);
    httpSession.setAttribute(successMessage, "Pomyślnie zarejestrowano");
    return "redirect:/login?success";
  }
}
