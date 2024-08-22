package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.SmsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
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
  private final SmsService smsService;
  private final String errorMessage = "errorMessage";
  private final String successMessage = "successMessage";
  private final String register = "register";
  private final String phone = "phone";
  private final String code = "code";
  private final String redirectVerificationError = "redirect:/account_verifications?error";
  private final String codeAmount = "codeAmount";

  @Autowired
  public AuthenticationController(
      CountryService countryService,
      AuthenticationService authenticationService,
      PasswordEncoder passwordEncoder,
      SmsService smsService) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
    this.passwordEncoder = passwordEncoder;
    this.smsService = smsService;
  }

  @GetMapping("/login")
  public String loginPage(
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession) {
    if (success != null) {
      String message = (String) httpSession.getAttribute(successMessage);
      if (message != null) {
        model.addAttribute(successMessage, message);
        httpSession.removeAttribute(successMessage);
        model.addAttribute(phone, httpSession.getAttribute(phone));
        httpSession.removeAttribute(phone);
      }
    } else if (error != null) {
      String message = (String) httpSession.getAttribute(errorMessage);
      if (message != null) {
        model.addAttribute(errorMessage, message);
        httpSession.removeAttribute(message);
      } else {
        model.addAttribute(errorMessage, "Niepoprawny login lub hasło");
      }
    }
    return "loginPage";
  }

  @GetMapping("/register")
  public String registerPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    String message = (String) httpSession.getAttribute(errorMessage);
    if (error != null && message != null) {
      model.addAttribute(errorMessage, message);
      httpSession.removeAttribute(errorMessage);
      model.addAttribute(register, httpSession.getAttribute(register));
      httpSession.removeAttribute(register);
    } else {
      model.addAttribute(register, new RegisterDTO());
    }
    List<CountryDTO> countries = countryService.getAll();
    model.addAttribute(
        "countries",
        countries.stream().map(e -> new CountryNamesDTO(e.getName(), e.getCode())).toList());
    model.addAttribute("areaCodes", countries.stream().map(CountryDTO::getAreaCode).toList());
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
    getVerificationNumber(httpSession, register.getPhone());
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
    RegisterDTO registerDTO = (RegisterDTO) httpSession.getAttribute(register);
    if (newCode != null) {
      Integer codeAmountInt = (Integer) httpSession.getAttribute(codeAmount);
      if (codeAmountInt != maxAmount) {
        if (!getVerificationNumber(httpSession, registerDTO.getPhone())) {
          httpSession.setAttribute(errorMessage, "Błąd podczas wysyłania sms");
          return redirectVerificationError;
        }
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
    model.addAttribute("registerDTO", registerDTO);
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
    httpSession.setAttribute(phone, ((RegisterDTO) httpSession.getAttribute(register)).getPhone());
    resetRegister(httpSession);
    httpSession.setAttribute(successMessage, "Pomyślnie zarejestrowano");
    return "redirect:/login?success";
  }

  private void resetRegister(HttpSession httpSession) {
    httpSession.removeAttribute(register);
    httpSession.removeAttribute(code);
    httpSession.removeAttribute(codeAmount);
  }

  private boolean getVerificationNumber(HttpSession httpSession, String phone) {
    try {
      String verificationNumber = authenticationService.getVerificationNumber();
      smsService.sendSms("Twój kod weryfikacyjny MultipleCard to: " + verificationNumber, phone);
      httpSession.setAttribute(code, passwordEncoder.encode(verificationNumber));
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
