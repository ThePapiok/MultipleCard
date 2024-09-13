package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ChangePasswordDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_PARAM = "success";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String CODE_SMS_CHANGE_PARAM = "codeSmsChange";
  private static final String ERROR_VALIDATION_MESSAGE = "validation.incorrect_data";
  private static final String REDIRECT_USER_ERROR = "redirect:/user?error";
  private final ProfileService profileService;
  private final CountryService countryService;
  private final MessageSource messageSource;
  private final CardService cardService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationService authenticationService;

  @Autowired
  public ProfileController(
      ProfileService profileService,
      CountryService countryService,
      MessageSource messageSource,
      CardService cardService,
      PasswordEncoder passwordEncoder,
      AuthenticationService authenticationService) {
    this.profileService = profileService;
    this.countryService = countryService;
    this.messageSource = messageSource;
    this.cardService = cardService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationService = authenticationService;
  }

  @GetMapping("/user")
  public String getProfile(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      Principal principal,
      Model model,
      HttpSession httpSession,
      Locale locale) {
    String phone = principal.getName();
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    }
    if (success != null && httpSession.getAttribute(SUCCESS_PARAM) != null) {
      model.addAttribute(
          "successMessage", messageSource.getMessage("profilePage.data.updated", null, locale));
      httpSession.removeAttribute(SUCCESS_PARAM);
    }
    model.addAttribute("profile", profileService.getProfile(phone));
    model.addAttribute("card", cardService.getCard(phone));
    model.addAttribute(
        "countries",
        countryService.getAll().stream()
            .map(e -> new CountryNamesDTO(e.getName(), e.getCode()))
            .distinct()
            .toList());
    return "profilePage";
  }

  @PostMapping("/user")
  public String editProfile(
      @Valid @ModelAttribute ProfileDTO profile,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    if (bindingResult.hasErrors()) {
      System.out.println(bindingResult);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    }
    if (!profileService.editProfile(profile, principal.getName())) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return REDIRECT_USER_ERROR;
    }
    httpSession.setAttribute(SUCCESS_PARAM, true);
    return "redirect:/user?success";
  }

  @GetMapping("/password_change")
  public String changePasswordPage(
      Model model,
      Principal principal,
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      HttpSession httpSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      } else {
        resetChangePassword(httpSession);
      }
    } else {
      resetChangePassword(httpSession);
    }
    if (reset != null) {
      resetChangePassword(httpSession);
      return "redirect:/user";
    }
    model.addAttribute("changePassword", new ChangePasswordDTO());
    model.addAttribute("phone", principal.getName());
    return "changePasswordPage";
  }

  @PostMapping("/password_change")
  public String changePassword(
      @Valid @ModelAttribute ChangePasswordDTO change,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal,
      Authentication authentication,
      HttpServletRequest request,
      HttpServletResponse response) {
    final int maxAmount = 3;
    final String phone = principal.getName();
    final String password = change.getNewPassword();
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.to_many_attempts", null, locale));
      resetChangePassword(httpSession);
      return REDIRECT_USER_ERROR;
    }
    if (bindingResult.hasErrors()) {
      System.out.println(bindingResult);
      return redirectPasswordChangeError(httpSession, amount, ERROR_VALIDATION_MESSAGE, locale);
    }
    if (!passwordEncoder.matches(
        change.getCode(), (String) httpSession.getAttribute(CODE_SMS_CHANGE_PARAM))) {
      return redirectPasswordChangeError(httpSession, amount, "error.bad_sms_code", locale);
    }
    if (!authenticationService.checkPassword(change.getOldPassword(), phone)) {
      return redirectPasswordChangeError(
          httpSession, amount, "changePassword.error.old_password.incorrect", locale);
    }
    if (!password.equals(change.getRetypedPassword())) {
      return redirectPasswordChangeError(httpSession, amount, "passwords_not_the_same", locale);
    }
    if (change.getOldPassword().equals(password)) {
      return redirectPasswordChangeError(
          httpSession, amount, "changePassword.error.passwords.the_same", locale);
    }
    if (!authenticationService.changePassword(phone, password)) {
      resetChangePassword(httpSession);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    }
    resetChangePassword(httpSession);
    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return "redirect:/login?success";
  }

  private String redirectPasswordChangeError(
      HttpSession httpSession, int amount, String message, Locale locale) {
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, messageSource.getMessage(message, null, locale));
    httpSession.setAttribute(ATTEMPTS_PARAM, amount + 1);
    return "redirect:/password_change?error";
  }

  private void resetChangePassword(HttpSession httpSession) {
    httpSession.removeAttribute(CODE_SMS_CHANGE_PARAM);
    httpSession.removeAttribute(ATTEMPTS_PARAM);
    httpSession.removeAttribute("codeAmountSms");
  }
}
