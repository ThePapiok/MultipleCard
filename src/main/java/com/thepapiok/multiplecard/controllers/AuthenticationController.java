package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.CallingCodeDTO;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.EmailService;
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
  private static final String ERROR_AT_SMS_SENDING = "Błąd podczas wysyłania sms";
  private static final String ERROR_AT_EMAIL_SENDING = "Błąd podczas wysyłania emaila";
  private static final String VERIFICATION_MESSAGE = "Twój kod weryfikacyjny MultipleCard to: ";

  private final CountryService countryService;
  private final AuthenticationService authenticationService;
  private final PasswordEncoder passwordEncoder;
  private final SmsService smsService;
  private final EmailService emailService;
  private final String errorMessage = "errorMessage";
  private final String successMessage = "successMessage";
  private final String register = "register";
  private final String phone = "phone";
  private final String codeSms = "codeSms";
  private final String codeEmail = "codeEmail";
  private final String redirectVerificationError = "redirect:/account_verifications?error";
  private final String redirectLogin = "redirect:/login";

  private final String codeAmountSms = "codeAmountSms";
  private final String codeAmountEmail = "codeAmountEmail";

  private final String callingCodes = "callingCodes";
  private final String callingCode = "callingCode";

  @Autowired
  public AuthenticationController(
      CountryService countryService,
      AuthenticationService authenticationService,
      PasswordEncoder passwordEncoder,
      SmsService smsService,
      EmailService emailService) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
    this.passwordEncoder = passwordEncoder;
    this.smsService = smsService;
    this.emailService = emailService;
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
        model.addAttribute(callingCode, httpSession.getAttribute(callingCode));
        httpSession.removeAttribute(callingCode);
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
    model.addAttribute(
        callingCodes,
        countryService.getAll().stream()
            .map(e -> new CallingCodeDTO(e.getCallingCode(), e.getCode()))
            .toList());
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
        countries.stream()
            .map(e -> new CountryNamesDTO(e.getName(), e.getCode()))
            .distinct()
            .toList());
    model.addAttribute(
        callingCodes,
        countries.stream().map(e -> new CallingCodeDTO(e.getCallingCode(), e.getCode())).toList());
    return "registerPage";
  }

  @PostMapping("/register")
  public String createUser(
      @Valid @ModelAttribute RegisterDTO register,
      BindingResult bindingResult,
      HttpSession httpSession) {
    // TODO - add check field isActive
    boolean error = false;
    String message = null;
    String redirect = "redirect:/register?error";
    httpSession.setAttribute(this.register, register);
    if (bindingResult.hasErrors()) {
      System.out.println(bindingResult);
      error = true;
      message = "Podane dane są niepoprawne";
    } else if (authenticationService
        .getPhones()
        .contains(register.getCallingCode() + register.getPhone())) {
      error = true;
      message = "Użytkownik o takim numerze telefonu już istnieje";
    } else if (authenticationService.getEmails().contains(register.getEmail())) {
      error = true;
      message = "Użytkownik o takim emailu już istnieje";
    } else if (!register.getPassword().equals(register.getRetypedPassword())) {
      error = true;
      message = "Podane hasła różnią się";
    }
    if (error) {
      httpSession.setAttribute(errorMessage, message);
      return redirect;
    }
    if (!getVerificationSms(httpSession, register.getPhone(), register.getCallingCode())) {
      httpSession.setAttribute(errorMessage, ERROR_AT_SMS_SENDING);
      return redirectVerificationError;
    }
    if (!getVerificationEmail(httpSession, register.getEmail())) {
      httpSession.setAttribute(errorMessage, ERROR_AT_EMAIL_SENDING);
      return redirectVerificationError;
    }
    httpSession.setAttribute(codeAmountSms, 1);
    httpSession.setAttribute(codeAmountEmail, 1);
    return "redirect:/account_verifications";
  }

  @GetMapping("/account_verifications")
  public String verificationPage(
      @RequestParam(required = false) String newCodeSms,
      @RequestParam(required = false) String newCodeEmail,
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession) {
    final int maxAmount = 3;
    RegisterDTO registerDTO = (RegisterDTO) httpSession.getAttribute(register);
    if (registerDTO == null) {
      return redirectLogin;
    }
    if (newCodeSms != null) {
      Integer codeAmountSmsInt = (Integer) httpSession.getAttribute(codeAmountSms);
      if (codeAmountSmsInt != maxAmount) {
        if (!getVerificationSms(
            httpSession, registerDTO.getPhone(), registerDTO.getCallingCode())) {
          httpSession.setAttribute(errorMessage, ERROR_AT_SMS_SENDING);
          return redirectVerificationError;
        }
        httpSession.setAttribute(codeAmountSms, codeAmountSmsInt + 1);
      } else {
        httpSession.setAttribute(errorMessage, "Za dużo razy poprosiłeś o nowy kod sms");
        return redirectVerificationError;
      }

    } else if (newCodeEmail != null) {
      Integer codeAmountEmailInt = (Integer) httpSession.getAttribute(codeAmountEmail);
      if (codeAmountEmailInt != maxAmount) {
        if (!getVerificationEmail(httpSession, registerDTO.getEmail())) {
          httpSession.setAttribute(errorMessage, ERROR_AT_EMAIL_SENDING);
          return redirectVerificationError;
        }
        httpSession.setAttribute(codeAmountEmail, codeAmountEmailInt + 1);
      } else {
        httpSession.setAttribute(errorMessage, "Za dużo razy poprosiłeś o nowy kod email");
        return redirectVerificationError;
      }

    } else if (reset != null) {
      resetRegister(httpSession);
      return redirectLogin;
    } else if (error != null) {
      model.addAttribute(errorMessage, httpSession.getAttribute(errorMessage));
      httpSession.removeAttribute(errorMessage);
    }
    return "verificationPage";
  }

  @PostMapping("/account_verifications")
  public String verification(
      @RequestParam String verificationNumberEmail,
      @RequestParam String verificationNumberSms,
      HttpSession httpSession) {
    RegisterDTO registerDTO = (RegisterDTO) httpSession.getAttribute(register);
    if (!passwordEncoder.matches(
        verificationNumberEmail, (String) httpSession.getAttribute(codeEmail))) {
      httpSession.setAttribute(errorMessage, "Nieprawidłowy kod email");
      return redirectVerificationError;
    }
    try {
      if (passwordEncoder.matches(
          verificationNumberSms, (String) httpSession.getAttribute(codeSms))) {
        authenticationService.createUser(registerDTO);
      } else {
        httpSession.setAttribute(errorMessage, "Nieprawidłowy kod sms");
        return redirectVerificationError;
      }
    } catch (Exception e) {
      System.out.println(e);
      resetRegister(httpSession);
      httpSession.setAttribute(errorMessage, "Nieoczekiwany błąd");
      return "redirect:/login?error";
    }
    httpSession.setAttribute(phone, registerDTO.getPhone());
    httpSession.setAttribute(callingCode, registerDTO.getCallingCode());
    resetRegister(httpSession);
    httpSession.setAttribute(successMessage, "Pomyślnie zarejestrowano");
    return "redirect:/login?success";
  }

  private void resetRegister(HttpSession httpSession) {
    httpSession.removeAttribute(register);
    httpSession.removeAttribute(codeSms);
    httpSession.removeAttribute(codeAmountSms);
    httpSession.removeAttribute(codeEmail);
    httpSession.removeAttribute(codeAmountEmail);
  }

  private boolean getVerificationSms(HttpSession httpSession, String phone, String callingCode) {
    try {
      String verificationNumber = authenticationService.getVerificationNumber();
      smsService.sendSms(VERIFICATION_MESSAGE + verificationNumber, callingCode + phone);
      httpSession.setAttribute(codeSms, passwordEncoder.encode(verificationNumber));
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private boolean getVerificationEmail(HttpSession httpSession, String email) {
    try {
      String verificationNumber = authenticationService.getVerificationNumber();
      System.out.println(verificationNumber);
      emailService.sendEmail(VERIFICATION_MESSAGE + verificationNumber, email);
      httpSession.setAttribute(codeEmail, passwordEncoder.encode(verificationNumber));
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
