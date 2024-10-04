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
import java.util.regex.Pattern;
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
  private static final String ERROR_TOO_MANY_ATTEMPTS_PARAM = "error.to_many_attempts";
  private static final String ERROR_BAD_SMS_CODE_PARAM = "error.bad_sms_code";
  private static final String ERROR_UNEXPECTED_PARAM = "error.unexpected";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ATTEMPTS_PARAM = "attempts";
  private static final String PHONE_PARAM = "phone";
  private static final String CODE_SMS_CHANGE_PARAM = "codeSmsChange";
  private static final String CODE_SMS_DELETE_PARAM = "codeSmsDelete";
  private static final String ERROR_VALIDATION_MESSAGE = "validation.incorrect_data";
  private static final String REDIRECT_USER_ERROR = "redirect:/user?error";
  private static final String REDIRECT_USER = "redirect:/user";
  private static final String REDIRECT_LOGIN_SUCCESS = "redirect:/login?success";
  private final ProfileService profileService;
  private final CountryService countryService;
  private final MessageSource messageSource;
  private final CardService cardService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationService authenticationService;
  private final AuthenticationController authenticationController;

  @Autowired
  public ProfileController(
      ProfileService profileService,
      CountryService countryService,
      MessageSource messageSource,
      CardService cardService,
      PasswordEncoder passwordEncoder,
      AuthenticationService authenticationService,
      AuthenticationController authenticationController) {
    this.profileService = profileService;
    this.countryService = countryService;
    this.messageSource = messageSource;
    this.cardService = cardService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationService = authenticationService;
    this.authenticationController = authenticationController;
  }

  @GetMapping("/user")
  public String getProfile(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      Principal principal,
      Model model,
      HttpSession httpSession) {
    String phone = principal.getName();
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    } else if (success != null) {
      String message = (String) httpSession.getAttribute(SUCCESS_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(SUCCESS_MESSAGE_PARAM, message);
        httpSession.removeAttribute(SUCCESS_MESSAGE_PARAM);
      }
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
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale));
      return REDIRECT_USER_ERROR;
    } else if (!profileService.editProfile(profile, principal.getName())) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_PARAM, null, locale));
      return REDIRECT_USER_ERROR;
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM, messageSource.getMessage("profilePage.data.updated", null, locale));
    return "redirect:/user?success";
  }

  @GetMapping("/password_change")
  public String changePasswordPage(
      Model model,
      Principal principal,
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      HttpSession httpSession) {
    String url = resetAndErrorParams(error, reset, model, httpSession, CODE_SMS_CHANGE_PARAM);
    if (url != null) {
      return url;
    }
    model.addAttribute("changePassword", new ChangePasswordDTO());
    model.addAttribute(PHONE_PARAM, principal.getName());
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
    final String redirectPasswordChangeError = "redirect:/password_change?error";
    final int maxAmount = 3;
    final String phone = principal.getName();
    final String password = change.getPassword();
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_PARAM, null, locale));
      authenticationController.resetSession(httpSession, CODE_SMS_CHANGE_PARAM, null);
      return REDIRECT_USER_ERROR;
    } else if (bindingResult.hasErrors()) {
      return redirectErrorPage(
          httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, redirectPasswordChangeError);
    } else if (!passwordEncoder.matches(
        change.getCode(), (String) httpSession.getAttribute(CODE_SMS_CHANGE_PARAM))) {
      return redirectErrorPage(
          httpSession, amount, ERROR_BAD_SMS_CODE_PARAM, locale, redirectPasswordChangeError);
    } else if (!authenticationService.checkPassword(change.getOldPassword(), phone)) {
      return redirectErrorPage(
          httpSession,
          amount,
          "changePassword.error.old_password.incorrect",
          locale,
          redirectPasswordChangeError);
    } else if (!password.equals(change.getRetypedPassword())) {
      return redirectErrorPage(
          httpSession, amount, "passwords_not_the_same", locale, redirectPasswordChangeError);
    } else if (change.getOldPassword().equals(password)) {
      return redirectErrorPage(
          httpSession,
          amount,
          "changePassword.error.passwords.the_same",
          locale,
          redirectPasswordChangeError);
    } else if (!authenticationService.changePassword(phone, password)) {
      authenticationController.resetSession(httpSession, CODE_SMS_CHANGE_PARAM, null);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_PARAM, null, locale));
      return REDIRECT_USER_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_CHANGE_PARAM, null);
    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return REDIRECT_LOGIN_SUCCESS;
  }

  @GetMapping("/delete_account")
  public String deleteAccountPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String reset,
      Principal principal,
      Model model,
      HttpSession httpSession) {
    String url = resetAndErrorParams(error, reset, model, httpSession, CODE_SMS_DELETE_PARAM);
    if (url != null) {
      return url;
    }
    model.addAttribute(PHONE_PARAM, principal.getName());
    return "deleteAccountPage";
  }

  @PostMapping("/delete_account")
  public String deleteAccount(
      @RequestParam String verificationNumberSms,
      Locale locale,
      HttpSession httpSession,
      Authentication authentication,
      HttpServletRequest request,
      HttpServletResponse response,
      Principal principal) {
    Pattern pattern = Pattern.compile("^[0-9]{3} [0-9]{3}$");
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    final String redirectDeleteAccountError = "redirect:/delete_account?error";
    final int maxAmount = 3;
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (amount == maxAmount) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM,
          messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_PARAM, null, locale));
      authenticationController.resetSession(httpSession, CODE_SMS_DELETE_PARAM, null);
      return REDIRECT_USER_ERROR;
    } else if (!pattern.matcher(verificationNumberSms).matches()) {
      return redirectErrorPage(
          httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, redirectDeleteAccountError);
    } else if (!passwordEncoder.matches(
        verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_DELETE_PARAM))) {
      return redirectErrorPage(
          httpSession, amount, ERROR_BAD_SMS_CODE_PARAM, locale, redirectDeleteAccountError);
    } else if (!profileService.deleteAccount(principal.getName())) {
      authenticationController.resetSession(httpSession, CODE_SMS_DELETE_PARAM, null);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_PARAM, null, locale));
      return REDIRECT_USER_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_CHANGE_PARAM, null);
    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return REDIRECT_LOGIN_SUCCESS;
  }

  private String resetAndErrorParams(
      String error, String reset, Model model, HttpSession httpSession, String codeSmsParam) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    } else if (reset != null) {
      authenticationController.resetSession(httpSession, codeSmsParam, null);
      return REDIRECT_USER;
    } else {
      authenticationController.resetSession(httpSession, codeSmsParam, null);
    }
    return null;
  }

  private String redirectErrorPage(
      HttpSession httpSession, int amount, String message, Locale locale, String redirectUrl) {
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, messageSource.getMessage(message, null, locale));
    httpSession.setAttribute(ATTEMPTS_PARAM, amount + 1);
    return redirectUrl;
  }
}
