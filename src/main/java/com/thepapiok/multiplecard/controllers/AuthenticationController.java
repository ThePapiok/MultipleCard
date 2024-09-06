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
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

  private static final String SUFFIX_VERIFICATION_MESSAGE = " MultipleCard: ";
  private static final String ERROR_SEND_SMS_PARAM_MESSAGES = "error.send_sms";
  private static final String ERROR_SEND_EMAIL_PARAM_MESSAGES = "error.send_email";
  private static final String MESSAGE_VERIFICATION_CODE_PARAM_MESSAGES =
      "message.verification_code";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String REDIRECT_LOGIN_ERROR = "redirect:/login?error";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String REGISTER_PARAM = "register";
  private static final String PHONE_PARAM = "phone";
  private static final String CODE_SMS_PARAM = "codeSms";
  private static final String CODE_EMAIL_PARAM = "codeEmail";
  private static final String REDIRECT_VERIFICATION_ERROR = "redirect:/account_verifications?error";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  private static final String CODE_AMOUNT_EMAIL_PARAM = "codeAmountEmail";
  private static final String CALLING_CODES_PARAM = "callingCodes";
  private static final String CALLING_CODE_PARAM = "callingCode";
  private static final String ERROR_UNEXPECTED = "error.unexpected";
  private final CountryService countryService;
  private final AuthenticationService authenticationService;
  private final PasswordEncoder passwordEncoder;
  private final SmsService smsService;
  private final EmailService emailService;
  private final MessageSource messageSource;

  @Autowired
  public AuthenticationController(
      CountryService countryService,
      AuthenticationService authenticationService,
      PasswordEncoder passwordEncoder,
      SmsService smsService,
      EmailService emailService,
      MessageSource messageSource) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
    this.passwordEncoder = passwordEncoder;
    this.smsService = smsService;
    this.emailService = emailService;
    this.messageSource = messageSource;
  }

  @GetMapping("/login")
  public String loginPage(
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession) {
    if (success != null) {
      String message = (String) httpSession.getAttribute(SUCCESS_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(SUCCESS_MESSAGE_PARAM, message);
        httpSession.removeAttribute(SUCCESS_MESSAGE_PARAM);
        model.addAttribute(PHONE_PARAM, httpSession.getAttribute(PHONE_PARAM));
        httpSession.removeAttribute(PHONE_PARAM);
        model.addAttribute(CALLING_CODE_PARAM, httpSession.getAttribute(CALLING_CODE_PARAM));
        httpSession.removeAttribute(CALLING_CODE_PARAM);
      }
    } else if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    }
    model.addAttribute(
        CALLING_CODES_PARAM,
        countryService.getAll().stream()
            .map(e -> new CallingCodeDTO(e.getCallingCode(), e.getCode()))
            .toList());
    return "loginPage";
  }

  @GetMapping("/register")
  public String registerPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
    if (error != null && message != null) {
      model.addAttribute(ERROR_MESSAGE_PARAM, message);
      httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      model.addAttribute(REGISTER_PARAM, httpSession.getAttribute(REGISTER_PARAM));
      httpSession.removeAttribute(REGISTER_PARAM);
    } else {
      model.addAttribute(REGISTER_PARAM, new RegisterDTO());
    }
    List<CountryDTO> countries = countryService.getAll();
    model.addAttribute(
        "countries",
        countries.stream()
            .map(e -> new CountryNamesDTO(e.getName(), e.getCode()))
            .distinct()
            .toList());
    model.addAttribute(
        CALLING_CODES_PARAM,
        countries.stream().map(e -> new CallingCodeDTO(e.getCallingCode(), e.getCode())).toList());
    return "registerPage";
  }

  @PostMapping("/register")
  public String createUser(
      @Valid @ModelAttribute RegisterDTO register,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale) {
    boolean error = false;
    String message = null;
    String redirect = "redirect:/register?error";
    httpSession.setAttribute(REGISTER_PARAM, register);
    if (bindingResult.hasErrors()) {
      System.out.println(bindingResult);
      error = true;
      message = messageSource.getMessage("validation.incorrect_data", null, locale);
    } else if (authenticationService
        .getPhones()
        .contains(register.getCallingCode() + register.getPhone())) {
      error = true;
      message =
          messageSource.getMessage("authenticationController.register.same_phone", null, locale);
    } else if (authenticationService.getEmails().contains(register.getEmail())) {
      error = true;
      message =
          messageSource.getMessage("authenticationController.register.same_email", null, locale);
    } else if (!register.getPassword().equals(register.getRetypedPassword())) {
      error = true;
      message =
          messageSource.getMessage(
              "authenticationController.register.passwords_not_the_same", null, locale);
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return redirect;
    }
    if (!getVerificationSms(httpSession, register.getPhone(), register.getCallingCode(), locale)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_SEND_SMS_PARAM_MESSAGES, null, locale));
      return REDIRECT_VERIFICATION_ERROR;
    }
    if (!getVerificationEmail(httpSession, register.getEmail(), locale)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_SEND_EMAIL_PARAM_MESSAGES, null, locale));
      return REDIRECT_VERIFICATION_ERROR;
    }
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 1);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    return "redirect:/account_verifications";
  }

  @GetMapping("/account_verifications")
  public String verificationPage(
      @RequestParam(required = false) String newCodeSms,
      @RequestParam(required = false) String newCodeEmail,
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession,
      Locale locale) {
    final int maxAmount = 3;
    RegisterDTO registerDTO = (RegisterDTO) httpSession.getAttribute(REGISTER_PARAM);
    if (registerDTO == null) {
      return REDIRECT_LOGIN;
    }
    if (newCodeSms != null) {
      Integer codeAmountSmsInt = (Integer) httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM);
      if (codeAmountSmsInt != maxAmount) {
        if (!getVerificationSms(
            httpSession, registerDTO.getPhone(), registerDTO.getCallingCode(), locale)) {
          httpSession.setAttribute(
              ERROR_MESSAGE_PARAM,
              messageSource.getMessage(ERROR_SEND_SMS_PARAM_MESSAGES, null, locale));
          return REDIRECT_VERIFICATION_ERROR;
        }
        httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, codeAmountSmsInt + 1);
      } else {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_sms", null, locale));
        return REDIRECT_VERIFICATION_ERROR;
      }

    } else if (newCodeEmail != null) {
      Integer codeAmountEmailInt = (Integer) httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM);
      if (codeAmountEmailInt != maxAmount) {
        if (!getVerificationEmail(httpSession, registerDTO.getEmail(), locale)) {
          httpSession.setAttribute(
              ERROR_MESSAGE_PARAM,
              messageSource.getMessage(ERROR_SEND_EMAIL_PARAM_MESSAGES, null, locale));
          return REDIRECT_VERIFICATION_ERROR;
        }
        httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, codeAmountEmailInt + 1);
      } else {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_email", null, locale));
        return REDIRECT_VERIFICATION_ERROR;
      }

    } else if (reset != null) {
      resetRegister(httpSession);
      return REDIRECT_LOGIN;
    } else if (error != null) {
      model.addAttribute(ERROR_MESSAGE_PARAM, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
      httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
    }
    return "verificationPage";
  }

  @PostMapping("/account_verifications")
  public String verification(
      @RequestParam String verificationNumberEmail,
      @RequestParam String verificationNumberSms,
      HttpSession httpSession,
      Locale locale) {
    RegisterDTO registerDTO = (RegisterDTO) httpSession.getAttribute(REGISTER_PARAM);
    Integer attempts = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    final int maxAmount = 3;
    if (attempts == maxAmount) {
      resetRegister(httpSession);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_attempts", null, locale));
      return REDIRECT_LOGIN_ERROR;
    } else if (!passwordEncoder.matches(
        verificationNumberEmail, (String) httpSession.getAttribute(CODE_EMAIL_PARAM))) {
      httpSession.setAttribute(ATTEMPTS_PARAM, attempts + 1);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.bad_email_code", null, locale));
      return REDIRECT_VERIFICATION_ERROR;
    }
    try {
      if (passwordEncoder.matches(
          verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_PARAM))) {
        if (!authenticationService.createUser(registerDTO)) {
          resetRegister(httpSession);
          httpSession.setAttribute(
              ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED, null, locale));
          return REDIRECT_LOGIN_ERROR;
        }
      } else {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM, messageSource.getMessage("error.bad_sms_code", null, locale));
        httpSession.setAttribute(ATTEMPTS_PARAM, attempts + 1);
        return REDIRECT_VERIFICATION_ERROR;
      }
    } catch (Exception e) {
      System.out.println(e);
      resetRegister(httpSession);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED, null, locale));
      return REDIRECT_LOGIN_ERROR;
    }
    httpSession.setAttribute(PHONE_PARAM, registerDTO.getPhone());
    httpSession.setAttribute(CALLING_CODE_PARAM, registerDTO.getCallingCode());
    resetRegister(httpSession);
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM, messageSource.getMessage("success.register", null, locale));
    return "redirect:/login?success";
  }

  private void resetRegister(HttpSession httpSession) {
    httpSession.removeAttribute(REGISTER_PARAM);
    httpSession.removeAttribute(CODE_SMS_PARAM);
    httpSession.removeAttribute(CODE_AMOUNT_SMS_PARAM);
    httpSession.removeAttribute(CODE_EMAIL_PARAM);
    httpSession.removeAttribute(CODE_AMOUNT_EMAIL_PARAM);
    httpSession.removeAttribute(ATTEMPTS_PARAM);
  }

  private boolean getVerificationSms(
      HttpSession httpSession, String phone, String callingCode, Locale locale) {
    try {
      String verificationNumber = authenticationService.getVerificationNumber();
      smsService.sendSms(
          messageSource.getMessage(MESSAGE_VERIFICATION_CODE_PARAM_MESSAGES, null, locale)
              + SUFFIX_VERIFICATION_MESSAGE
              + verificationNumber,
          callingCode + phone);
      httpSession.setAttribute(CODE_SMS_PARAM, passwordEncoder.encode(verificationNumber));
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private boolean getVerificationEmail(HttpSession httpSession, String email, Locale locale) {
    try {
      String verificationNumber = authenticationService.getVerificationNumber();
      emailService.sendEmail(
          messageSource.getMessage(MESSAGE_VERIFICATION_CODE_PARAM_MESSAGES, null, locale)
              + SUFFIX_VERIFICATION_MESSAGE
              + verificationNumber,
          email,
          locale);
      httpSession.setAttribute(CODE_EMAIL_PARAM, passwordEncoder.encode(verificationNumber));
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
    return true;
  }
}
