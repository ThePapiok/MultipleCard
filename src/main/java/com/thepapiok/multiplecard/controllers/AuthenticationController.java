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
  private final String login = "login";

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
      LoginDTO loginDTO = new LoginDTO();
      loginDTO.setLogin((String) httpSession.getAttribute(login));
      httpSession.removeAttribute(login);
      model.addAttribute(login, loginDTO);

    } else {
      model.addAttribute(login, new LoginDTO());
    }
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
    String redirect = "redirect:/register?error";
    if (bindingResult.hasErrors()) {
      error = true;
      message = "Podane dane są niepoprawne";
    } else if (authenticationService.getPhones().contains(register.getPhone())) {
      error = true;
      message = "Użytkownik o takim numerze telefonu już istnieje";
    } else if (!register.getPassword().equals(register.getRetypedPassword())) {
      error = true;
      message = "Podane hasła różnią się";
    }
    if (error) {
      httpSession.setAttribute(errorMessage, message);
      httpSession.setAttribute(this.register, register);
      return redirect;
    }
    try {
      authenticationService.createUser(register);
    } catch (Exception e) {
      httpSession.setAttribute(errorMessage, "Nieoczekiwany błąd");
      httpSession.setAttribute(this.register, register);
      return redirect;
    }
    // TODO maybe add better exception
    httpSession.setAttribute(successMessage, "Pomyślnie zarejestrowano");
    httpSession.setAttribute(login, register.getPhone());
    return "redirect:/login?success";
  }
}
