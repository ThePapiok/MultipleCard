package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.LoginDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
  public String registerPage(@RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    if(error!=null)
    {
      model.addAttribute("errorMessage", httpSession.getAttribute("errorMessage"));
      model.addAttribute("register", httpSession.getAttribute("register"));
    }
    else{
      model.addAttribute("register", new RegisterDTO());
    }
    model.addAttribute("countries", countryService.getDTOs());
    return "registerPage";
  }

  @PostMapping("/register")
  public String createUser(@ModelAttribute RegisterDTO register, Model model, HttpSession httpSession) {
    if(authenticationService.getPhones().contains(register.getPhone()))
    {
      httpSession.setAttribute("errorMessage", "Użytkownik o takim numerze telefonu już istnieje");
      httpSession.setAttribute("register", register);
      return "redirect:/register?error";
    }
    authenticationService.createUser(register);
    return "redirect:/login";
  }
}
