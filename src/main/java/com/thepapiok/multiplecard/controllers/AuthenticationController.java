package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.CallingCodeDTO;
import com.thepapiok.multiplecard.dto.CountryDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import com.thepapiok.multiplecard.dto.ResetPasswordDTO;
import com.thepapiok.multiplecard.misc.LocaleChanger;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.ShopService;
import com.thepapiok.multiplecard.services.SmsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthenticationController {

  private static final String SUFFIX_VERIFICATION_MESSAGE = " MultipleCard: ";
  private static final String ERROR_SEND_SMS_PARAM_MESSAGE = "error.send_sms";
  private static final String ERROR_SEND_EMAIL_PARAM_MESSAGE = "error.send_email";
  private static final String ERROR_TOO_MANY_SMS_MESSAGE = "error.to_many_sms";
  private static final String ERROR_TOO_MANY_ATTEMPTS_MESSAGE = "error.to_many_attempts";
  private static final String ERROR_VALIDATION_INCORRECT_DATA_MESSAGE = "validation.incorrect_data";
  private static final String ERROR_REGISTER_SAME_PHONE_MESSAGE =
      "authenticationController.register.same_phone";
  private static final String ERROR_REGISTER_SAME_EMAIL_MESSAGE =
      "authenticationController.register.same_email";
  private static final String ERROR_BAD_SMS_CODE_MESSAGE = "error.bad_sms_code";
  private static final String ERROR_PASSWORDS_NOT_THE_SAME_MESSAGE = "passwords_not_the_same";
  private static final String ERROR_USER_NOT_FOUND_MESSAGE = "resetPasswordPage.user.not_found";
  private static final String MESSAGE_VERIFICATION_CODE_PARAM_MESSAGES =
      "message.verification_code";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String REDIRECT_LOGIN_ERROR = "redirect:/login?error";
  private static final String REDIRECT_LOGIN_SUCCESS = "redirect:/login?success";
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String REGISTER_PARAM = "register";
  private static final String RESET_PARAM = "reset";
  private static final String PHONE_PARAM = "phone";
  private static final String CODE_SMS_PARAM_REGISTER = "codeSmsRegister";
  private static final String CODE_SMS_PARAM_REGISTER_SHOP = "codeSmsRegisterShop";
  private static final String CODE_SMS_PARAM_RESET = "codeSmsReset";
  private static final String CODE_EMAIL_PARAM = "codeEmail";
  private static final String REDIRECT_VERIFICATION_ERROR = "redirect:/account_verifications?error";
  private static final String REDIRECT_SHOP_VERIFICATION_ERROR =
      "redirect:/shop_verifications?error";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String CODE_AMOUNT_SMS_PARAM = "codeAmountSms";
  private static final String CODE_AMOUNT_EMAIL_PARAM = "codeAmountEmail";
  private static final String CALLING_CODES_PARAM = "callingCodes";
  private static final String COUNTRIES_PARAM = "countries";
  private static final String CALLING_CODE_PARAM = "callingCode";
  private static final String ERROR_UNEXPECTED = "error.unexpected";
  private final CountryService countryService;
  private final AuthenticationService authenticationService;
  private final PasswordEncoder passwordEncoder;
  private final SmsService smsService;
  private final EmailService emailService;
  private final MessageSource messageSource;
  private final LocaleChanger localeChanger;
  private final ShopService shopService;

  @Autowired
  public AuthenticationController(
      CountryService countryService,
      AuthenticationService authenticationService,
      PasswordEncoder passwordEncoder,
      SmsService smsService,
      EmailService emailService,
      MessageSource messageSource,
      LocaleChanger localeChanger,
      ShopService shopService) {
    this.countryService = countryService;
    this.authenticationService = authenticationService;
    this.passwordEncoder = passwordEncoder;
    this.smsService = smsService;
    this.emailService = emailService;
    this.messageSource = messageSource;
    this.localeChanger = localeChanger;
    this.shopService = shopService;
  }

  @GetMapping("/login")
  public String loginPage(
      @RequestParam(required = false) String success,
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession,
      Locale locale) {
    localeChanger.setLocale(locale);
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
    }
    if (error == null || message == null) {
      model.addAttribute(REGISTER_PARAM, new RegisterDTO());
    }
    List<CountryDTO> countries = countryService.getAll();
    model.addAttribute(
        COUNTRIES_PARAM,
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
      message = messageSource.getMessage(ERROR_VALIDATION_INCORRECT_DATA_MESSAGE, null, locale);
    } else if (authenticationService
        .getPhones()
        .contains(register.getCallingCode() + register.getPhone())) {
      error = true;
      message = messageSource.getMessage(ERROR_REGISTER_SAME_PHONE_MESSAGE, null, locale);
    } else if (authenticationService.getEmails().contains(register.getEmail())) {
      error = true;
      message = messageSource.getMessage(ERROR_REGISTER_SAME_EMAIL_MESSAGE, null, locale);
    } else if (!register.getPassword().equals(register.getRetypedPassword())) {
      error = true;
      message = messageSource.getMessage(ERROR_PASSWORDS_NOT_THE_SAME_MESSAGE, null, locale);
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return redirect;
    }
    if (!getVerificationSms(
        httpSession,
        register.getPhone(),
        register.getCallingCode(),
        locale,
        CODE_SMS_PARAM_REGISTER)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_SEND_SMS_PARAM_MESSAGE, null, locale));
      return REDIRECT_VERIFICATION_ERROR;
    }
    if (!getVerificationEmail(httpSession, register.getEmail(), locale)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_SEND_EMAIL_PARAM_MESSAGE, null, locale));
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
            httpSession,
            registerDTO.getPhone(),
            registerDTO.getCallingCode(),
            locale,
            CODE_SMS_PARAM_REGISTER)) {
          httpSession.setAttribute(
              ERROR_MESSAGE_PARAM,
              messageSource.getMessage(ERROR_SEND_SMS_PARAM_MESSAGE, null, locale));
          return REDIRECT_VERIFICATION_ERROR;
        }
        httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, codeAmountSmsInt + 1);
      } else {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM,
            messageSource.getMessage(ERROR_TOO_MANY_SMS_MESSAGE, null, locale));
        return REDIRECT_VERIFICATION_ERROR;
      }

    } else if (newCodeEmail != null) {
      Integer codeAmountEmailInt = (Integer) httpSession.getAttribute(CODE_AMOUNT_EMAIL_PARAM);
      if (codeAmountEmailInt != maxAmount) {
        if (!getVerificationEmail(httpSession, registerDTO.getEmail(), locale)) {
          httpSession.setAttribute(
              ERROR_MESSAGE_PARAM,
              messageSource.getMessage(ERROR_SEND_EMAIL_PARAM_MESSAGE, null, locale));
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
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, null, locale));
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
          verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_PARAM_REGISTER))) {
        if (!authenticationService.createUser(registerDTO)) {
          resetRegister(httpSession);
          httpSession.setAttribute(
              ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED, null, locale));
          return REDIRECT_LOGIN_ERROR;
        }
      } else {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM,
            messageSource.getMessage(ERROR_BAD_SMS_CODE_MESSAGE, null, locale));
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
    return REDIRECT_LOGIN_SUCCESS;
  }

  private void resetRegister(HttpSession httpSession) {
    httpSession.removeAttribute(REGISTER_PARAM);
    httpSession.removeAttribute(CODE_SMS_PARAM_REGISTER);
    httpSession.removeAttribute(CODE_AMOUNT_SMS_PARAM);
    httpSession.removeAttribute(CODE_EMAIL_PARAM);
    httpSession.removeAttribute(CODE_AMOUNT_EMAIL_PARAM);
    httpSession.removeAttribute(ATTEMPTS_PARAM);
  }

  private boolean getVerificationSms(
      HttpSession httpSession, String phone, String callingCode, Locale locale, String param) {
    try {
      String verificationNumber = authenticationService.getVerificationNumber();
      smsService.sendSms(
          messageSource.getMessage(MESSAGE_VERIFICATION_CODE_PARAM_MESSAGES, null, locale)
              + SUFFIX_VERIFICATION_MESSAGE
              + verificationNumber,
          callingCode + phone);
      httpSession.setAttribute(param, passwordEncoder.encode(verificationNumber));
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
      return false;
    }
    return true;
  }

  @GetMapping("/password_reset")
  public String passwordResetPage(
      Model model,
      HttpSession httpSession,
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String reset) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute("error", message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
        model.addAttribute("isSent", true);
      }
    } else {
      resetResetPassword(httpSession);
    }
    if (reset != null) {
      resetResetPassword(httpSession);
      return REDIRECT_LOGIN;
    }
    ResetPasswordDTO resetPasswordDTO = (ResetPasswordDTO) httpSession.getAttribute(RESET_PARAM);
    if (resetPasswordDTO != null) {
      model.addAttribute(RESET_PARAM, resetPasswordDTO);
      httpSession.removeAttribute(RESET_PARAM);
    } else {
      model.addAttribute(RESET_PARAM, new ResetPasswordDTO());
    }
    model.addAttribute(
        CALLING_CODES_PARAM,
        countryService.getAll().stream()
            .map(e -> new CallingCodeDTO(e.getCallingCode(), e.getCode()))
            .toList());
    return "resetPasswordPage";
  }

  @PostMapping("/password_reset")
  public String resetPassword(
      @Valid @ModelAttribute ResetPasswordDTO reset,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale) {
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    final String fullPhone = reset.getCallingCode() + reset.getPhone();
    final int maxAmount = 3;
    if (amount == maxAmount) {
      resetResetPassword(httpSession);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_MESSAGE, null, locale));
      return REDIRECT_LOGIN_ERROR;
    }
    if (bindingResult.hasErrors()) {
      return redirectPasswordResetError(
          httpSession, amount, reset, ERROR_VALIDATION_INCORRECT_DATA_MESSAGE, locale);
    }
    if (!passwordEncoder.matches(
        reset.getCode(), (String) httpSession.getAttribute(CODE_SMS_PARAM_RESET))) {
      return redirectPasswordResetError(
          httpSession, amount, reset, ERROR_BAD_SMS_CODE_MESSAGE, locale);
    }
    if (!reset.getRetypedPassword().equals(reset.getPassword())) {
      return redirectPasswordResetError(
          httpSession, amount, reset, ERROR_PASSWORDS_NOT_THE_SAME_MESSAGE, locale);
    }
    if (!authenticationService.getAccountByPhone(fullPhone)) {
      return redirectPasswordResetError(
          httpSession, amount, reset, ERROR_USER_NOT_FOUND_MESSAGE, locale);
    }
    if (!authenticationService.changePassword(fullPhone, reset.getPassword())) {
      resetResetPassword(httpSession);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED, null, locale));
      return REDIRECT_LOGIN_ERROR;
    }
    resetResetPassword(httpSession);
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM,
        messageSource.getMessage("resetPasswordPage.success.reset_password", null, locale));
    return REDIRECT_LOGIN_SUCCESS;
  }

  private String redirectPasswordResetError(
      HttpSession httpSession, int amount, ResetPasswordDTO reset, String message, Locale locale) {
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, messageSource.getMessage(message, null, locale));
    httpSession.setAttribute(ATTEMPTS_PARAM, amount + 1);
    httpSession.setAttribute(RESET_PARAM, reset);
    return "redirect:/password_reset?error";
  }

  @PostMapping("/get_verification_number")
  @ResponseBody
  public String getVerificationNumber(
      HttpSession httpSession,
      @RequestParam String callingCode,
      @RequestParam String phone,
      @RequestParam String param,
      Locale locale) {
    final int maxAmount = 3;
    Integer codeAmount = (Integer) httpSession.getAttribute(CODE_AMOUNT_SMS_PARAM);
    String fullPhone = callingCode + phone;
    int length = fullPhone.length();
    final int maxLength = 16;
    final int minLength = 9;
    Pattern phonePattern = Pattern.compile("^\\+[0-9]+[1-9][0-9]*$");
    if (!phonePattern.matcher(fullPhone).matches() || length > maxLength || length < minLength) {
      return messageSource.getMessage(ERROR_VALIDATION_INCORRECT_DATA_MESSAGE, null, locale);
    }
    if (!authenticationService.getAccountByPhone(fullPhone)) {
      return messageSource.getMessage(ERROR_USER_NOT_FOUND_MESSAGE, null, locale);
    }
    if (codeAmount == null) {
      httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 0);
      codeAmount = 0;
    } else if (codeAmount == maxAmount) {
      return messageSource.getMessage(ERROR_TOO_MANY_SMS_MESSAGE, null, locale);
    }
    if (!getVerificationSms(httpSession, phone, callingCode, locale, param)) {
      return messageSource.getMessage(ERROR_SEND_SMS_PARAM_MESSAGE, null, locale);
    }
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, codeAmount + 1);
    return "ok";
  }

  private void resetResetPassword(HttpSession httpSession) {
    httpSession.removeAttribute(CODE_SMS_PARAM_RESET);
    httpSession.removeAttribute(CODE_AMOUNT_SMS_PARAM);
    httpSession.removeAttribute(ATTEMPTS_PARAM);
    httpSession.removeAttribute(RESET_PARAM);
  }

  @GetMapping("/register_shop")
  public String registerShopPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    }
    RegisterShopDTO registerShopDTO = (RegisterShopDTO) httpSession.getAttribute(REGISTER_PARAM);
    if (registerShopDTO == null) {
      registerShopDTO = new RegisterShopDTO();
    } else {
      httpSession.removeAttribute(REGISTER_PARAM);
    }
    List<CountryDTO> countries = countryService.getAll();
    model.addAttribute(REGISTER_PARAM, registerShopDTO);
    model.addAttribute(
        COUNTRIES_PARAM,
        countries.stream()
            .map(e -> new CountryNamesDTO(e.getName(), e.getCode()))
            .distinct()
            .toList());
    model.addAttribute(
        CALLING_CODES_PARAM,
        countries.stream().map(e -> new CallingCodeDTO(e.getCallingCode(), e.getCode())).toList());
    return "registerShopPage";
  }

  @PostMapping("/register_shop")
  public String registerShop(
      @ModelAttribute @Valid RegisterShopDTO register,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale) {
    final MultipartFile file = register.getFile();
    final String accountNumber = register.getAccountNumber();
    final List<AddressDTO> points = register.getAddress();
    final int maxListSize = 5;
    boolean error = false;
    String message = "";
    httpSession.setAttribute(REGISTER_PARAM, register);
    if (bindingResult.hasErrors()) {
      error = true;
      message = messageSource.getMessage(ERROR_VALIDATION_INCORRECT_DATA_MESSAGE, null, locale);
    } else if (authenticationService
        .getPhones()
        .contains(register.getCallingCode() + register.getPhone())) {
      error = true;
      message = messageSource.getMessage(ERROR_REGISTER_SAME_PHONE_MESSAGE, null, locale);
    } else if (authenticationService.getEmails().contains(register.getEmail())) {
      error = true;
      message = messageSource.getMessage(ERROR_REGISTER_SAME_EMAIL_MESSAGE, null, locale);
    } else if (shopService.checkShopNameExists(register.getName())) {
      error = true;
      message =
          messageSource.getMessage("authenticationController.register.same_name", null, locale);
    } else if (shopService.checkAccountNumberExists(accountNumber)) {
      error = true;
      message =
          messageSource.getMessage(
              "authenticationController.register.same_account_number", null, locale);
    } else if (!register.getPassword().equals(register.getRetypedPassword())) {
      error = true;
      message = messageSource.getMessage(ERROR_PASSWORDS_NOT_THE_SAME_MESSAGE, null, locale);
    } else if (!shopService.checkAccountNumber(accountNumber)) {
      error = true;
      message =
          messageSource.getMessage(
              "authenticationController.register.bad_account_number", null, locale);
    } else if (points.size() == 0 || points.size() > maxListSize) {
      error = true;
      message =
          messageSource.getMessage(
              "authenticationController.register.bad_size_points", null, locale);
    } else if (new HashSet<>(points).size() != points.size()) {
      error = true;
      message =
          messageSource.getMessage("authenticationController.register.same_points", null, locale);
    } else if (!shopService.checkPointsExists(points)) {
      error = true;
      message =
          messageSource.getMessage(
              "authenticationController.register.same_other_points", null, locale);
    } else if (!shopService.checkFile(file)) {
      error = true;
      message =
          messageSource.getMessage("authenticationController.register.bad_file", null, locale);
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return "redirect:/register_shop?error";
    }
    if (!getVerificationSms(
        httpSession,
        register.getPhone(),
        register.getCallingCode(),
        locale,
        CODE_SMS_PARAM_REGISTER_SHOP)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_SEND_SMS_PARAM_MESSAGE, null, locale));
      return REDIRECT_SHOP_VERIFICATION_ERROR;
    }
    if (!getVerificationEmail(httpSession, register.getEmail(), locale)) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_SEND_EMAIL_PARAM_MESSAGE, null, locale));
      return REDIRECT_SHOP_VERIFICATION_ERROR;
    }
    httpSession.setAttribute(CODE_AMOUNT_SMS_PARAM, 1);
    httpSession.setAttribute(CODE_AMOUNT_EMAIL_PARAM, 1);
    httpSession.setAttribute(ATTEMPTS_PARAM, 0);
    return "redirect:/shop_verifications";
  }
}
