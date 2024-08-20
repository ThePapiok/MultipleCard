package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.LoginDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;
  private final String errorMessage = "errorMessage";
  private final String successMessage = "successMessage";
  private final String register = "register";
  private final String phone = "phone";
  private final String login = "login";
  private final String code = "code";
  private final String redirectVerificationError = "redirect:/account_verifications?error";
  private final String codeAmount = "codeAmount";

  @Autowired
  public AuthenticationController(
      CountryService countryService,
      AuthenticationService authenticationService,
      PasswordEncoder passwordEncoder) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/login")
  public String loginPage(
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession) {
    if (success != null) {
      model.addAttribute(successMessage, httpSession.getAttribute(successMessage));
      httpSession.removeAttribute(successMessage);
      LoginDTO loginDTO = new LoginDTO();
      loginDTO.setPhone((String) httpSession.getAttribute(phone));
      httpSession.removeAttribute(phone);
      model.addAttribute(login, loginDTO);

    } else if (error != null) {
      String message = (String) httpSession.getAttribute(errorMessage);
      if (message == null) {
        model.addAttribute(errorMessage, message);
        httpSession.removeAttribute(message);
      } else {
        model.addAttribute(errorMessage, "Niepoprawny login lub hasło");
      }
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
    httpSession.setAttribute(this.register, register);
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
      return redirect;
    }
    String verificationNumber = authenticationService.getVerificationNumber();
    System.out.println(verificationNumber);
    httpSession.setAttribute(code, passwordEncoder.encode(verificationNumber));
    httpSession.setAttribute(codeAmount, 1);
    return "redirect:/account_verifications";
  }

  @GetMapping("/account_verifications")
  public String verificationPage(
      @RequestParam(required = false) String newCode,
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession) {
    final int maxAmount = 3;
    if (newCode != null) {
      Integer codeAmountInt = (Integer) httpSession.getAttribute(codeAmount);
      if (codeAmountInt != maxAmount) {
        String verificationNumber = authenticationService.getVerificationNumber();
        System.out.println(verificationNumber);
        httpSession.setAttribute(code, passwordEncoder.encode(verificationNumber));
        httpSession.setAttribute(codeAmount, codeAmountInt + 1);
      } else {
        httpSession.setAttribute(errorMessage, "Za dużo razy poprosiłeś o nowy kod");
        return redirectVerificationError;
      }

    } else if (reset != null) {
      resetRegister(httpSession);
      return "redirect:/login";
    } else if (error != null) {
      model.addAttribute(errorMessage, httpSession.getAttribute(errorMessage));
      httpSession.removeAttribute(errorMessage);
    }
    model.addAttribute(register, httpSession.getAttribute(register));
    return "verificationPage";
  }

  @PostMapping("/account_verifications")
  public String verification(@ModelAttribute RegisterDTO registerDTO, HttpSession httpSession) {
    try {
      if (passwordEncoder.matches(
          registerDTO.getVerificationNumber(), (String) httpSession.getAttribute(code))) {
        authenticationService.createUser((RegisterDTO) httpSession.getAttribute(register));
      } else {
        httpSession.setAttribute(errorMessage, "Nieprawidłowy kod");
        return redirectVerificationError;
      }
    } catch (Exception e) {
      System.out.println(e);
      resetRegister(httpSession);
      httpSession.setAttribute(errorMessage, "Nieoczekiwany błąd");
      return "redirect:/login?error";
      // TODO maybe add better exception
    }
    resetRegister(httpSession);
    httpSession.setAttribute(successMessage, "Pomyślnie zarejestrowano");
    httpSession.setAttribute(phone, registerDTO.getPhone());
    return "redirect:/login?success";
  }

  private void resetRegister(HttpSession httpSession) {
    httpSession.removeAttribute(register);
    httpSession.removeAttribute(code);
    httpSession.removeAttribute(codeAmount);
  }
}
