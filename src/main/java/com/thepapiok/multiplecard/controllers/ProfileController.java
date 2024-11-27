package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.ChangePasswordDTO;
import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.services.AuthenticationService;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import com.thepapiok.multiplecard.services.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

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
  private static final String CODE_SMS_EDIT_PARAM = "codeSmsEdit";
  private static final String CODE_SMS_EDIT_SHOP_PARAM = "codeSmsEditShop";
  private static final String EDIT_PARAM = "edit";
  private static final String EDIT_SHOP_PARAM = "editShop";
  private static final String CODE_SMS_DELETE_PARAM = "codeSmsDelete";
  private static final String ERROR_VALIDATION_MESSAGE = "validation.incorrect_data";
  private static final String REDIRECT_PROFILE_ERROR = "redirect:/profile?error";
  private static final String REDIRECT_PROFILE = "redirect:/profile";
  private static final Pattern PATTERN_CODE = Pattern.compile("^[0-9]{3} [0-9]{3}$");
  private final ProfileService profileService;
  private final CountryService countryService;
  private final MessageSource messageSource;
  private final CardService cardService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationService authenticationService;
  private final AuthenticationController authenticationController;
  private final ShopService shopService;

  @Autowired
  public ProfileController(
      ProfileService profileService,
      CountryService countryService,
      MessageSource messageSource,
      CardService cardService,
      PasswordEncoder passwordEncoder,
      AuthenticationService authenticationService,
      AuthenticationController authenticationController,
      ShopService shopService) {
    this.profileService = profileService;
    this.countryService = countryService;
    this.messageSource = messageSource;
    this.cardService = cardService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationService = authenticationService;
    this.authenticationController = authenticationController;
    this.shopService = shopService;
  }

  @GetMapping("/profile")
  public String getProfile(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      Principal principal,
      Model model,
      HttpSession httpSession,
      Locale locale) {
    final String countriesParam = "countries";
    String phone = principal.getName();
    if (error != null) {
      if ("501".equals(error)) {
        httpSession.removeAttribute(SUCCESS_MESSAGE_PARAM);
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM,
            messageSource.getMessage("buyCard.error.canceled_order", null, locale));
        return "redirect:/profile?error";
      }
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
    model.addAttribute(
        countriesParam,
        countryService.getAll().stream()
            .map(e -> new CountryNamesDTO(e.getName(), e.getCode()))
            .distinct()
            .toList());
    if (profileService.checkRole(phone, Role.ROLE_SHOP)) {
      model.addAttribute("profileShop", profileService.getShop(phone));
      return "profileShopPage";
    } else {
      model.addAttribute("profile", profileService.getProfile(phone));
      model.addAttribute("card", cardService.getCard(phone));
      return "profilePage";
    }
  }

  @PostMapping("/shop")
  public String editProfileShop(
      @Valid @ModelAttribute ProfileShopDTO profileShop,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    boolean error = false;
    String message = null;
    final String phone = principal.getName();
    final String accountNumber = profileShop.getAccountNumber();
    final List<AddressDTO> points = profileShop.getAddress();
    final int maxListSize = 5;
    final MultipartFile file = profileShop.getFile();
    if (bindingResult.hasErrors()) {
      error = true;
      message = messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale);
    } else if (shopService.checkShopNameExists(profileShop.getName(), phone)) {
      error = true;
      message = messageSource.getMessage("error.same_name", null, locale);
    } else if (shopService.checkAccountNumberExists(accountNumber, phone)) {
      error = true;
      message = messageSource.getMessage("error.same_account_number", null, locale);
    } else if (!shopService.checkAccountNumber(accountNumber)) {
      error = true;
      message = messageSource.getMessage("error.bad_account_number", null, locale);
    } else if (points.size() == 0 || points.size() > maxListSize) {
      error = true;
      message = messageSource.getMessage("error.bad_size_points", null, locale);
    } else if (new HashSet<>(points).size() != points.size()) {
      error = true;
      message = messageSource.getMessage("error.same_points", null, locale);
    } else if (shopService.checkPointsExists(points, phone)) {
      error = true;
      message = messageSource.getMessage("error.same_other_points", null, locale);
    } else if (!file.isEmpty() && !shopService.checkImage(file)) {
      error = true;
      message = messageSource.getMessage("error.bad_file", null, locale);
    } else if (!file.isEmpty()) {
      String filePath = shopService.saveTempFile(file);
      if (filePath == null) {
        error = true;
        message = messageSource.getMessage(ERROR_UNEXPECTED_PARAM, null, locale);
      } else {
        httpSession.setAttribute("filePath", filePath);
      }
    }
    if (error) {
      httpSession.setAttribute(ERROR_MESSAGE_PARAM, message);
      return REDIRECT_PROFILE_ERROR;
    }
    httpSession.setAttribute(EDIT_SHOP_PARAM, profileShop);
    return "redirect:/edit_profile";
  }

  @PostMapping("/user")
  public String editProfile(
      @Valid @ModelAttribute ProfileDTO profile,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale) {
    if (bindingResult.hasErrors()) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_VALIDATION_MESSAGE, null, locale));
      return REDIRECT_PROFILE_ERROR;
    }
    httpSession.setAttribute(EDIT_PARAM, profile);
    return "redirect:/edit_profile";
  }

  @GetMapping("/edit_profile")
  public String verificationEditProfilePage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String reset,
      HttpSession httpSession,
      Principal principal,
      Model model) {
    final String phone = principal.getName();
    model.addAttribute(PHONE_PARAM, principal.getName());
    if (profileService.checkRole(phone, Role.ROLE_SHOP)) {
      ProfileShopDTO profileShopDTO = (ProfileShopDTO) httpSession.getAttribute(EDIT_SHOP_PARAM);
      if (profileShopDTO == null) {
        return REDIRECT_PROFILE;
      }
      String url =
          resetAndErrorParams(
              error, reset, model, httpSession, CODE_SMS_EDIT_SHOP_PARAM, EDIT_SHOP_PARAM, false);
      if (url != null) {
        return url;
      }
      return "editProfileShopPage";
    } else {
      ProfileDTO profileDTO = (ProfileDTO) httpSession.getAttribute(EDIT_PARAM);
      if (profileDTO == null) {
        return REDIRECT_PROFILE;
      }
      String url =
          resetAndErrorParams(
              error, reset, model, httpSession, CODE_SMS_EDIT_PARAM, EDIT_PARAM, false);
      if (url != null) {
        return url;
      }
      return "editProfilePage";
    }
  }

  @PostMapping("/edit_profile")
  public String verificationEditProfile(
      @RequestParam(required = false) List<MultipartFile> file,
      @RequestParam String verificationNumberSms,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    final String redirectEditProfileError = "redirect:/edit_profile?error";
    final int maxAmount = 3;
    final String phone = principal.getName();
    Integer amount = (Integer) httpSession.getAttribute(ATTEMPTS_PARAM);
    if (amount == null) {
      httpSession.setAttribute(ATTEMPTS_PARAM, 0);
      amount = 0;
    }
    if (profileService.checkRole(phone, Role.ROLE_SHOP)) {
      final int maxSize = 9;
      ProfileShopDTO profileShopDTO = (ProfileShopDTO) httpSession.getAttribute(EDIT_SHOP_PARAM);
      if (amount == maxAmount) {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM,
            messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_PARAM, null, locale));
        authenticationController.resetSession(
            httpSession, CODE_SMS_EDIT_SHOP_PARAM, EDIT_SHOP_PARAM);
        return REDIRECT_PROFILE_ERROR;
      } else if (file == null) {
        return redirectErrorPage(
            httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, redirectEditProfileError);
      } else if (!PATTERN_CODE.matcher(verificationNumberSms).matches()) {
        return redirectErrorPage(
            httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, redirectEditProfileError);
      } else if (!passwordEncoder.matches(
          verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_EDIT_SHOP_PARAM))) {
        return redirectErrorPage(
            httpSession, amount, ERROR_BAD_SMS_CODE_PARAM, locale, redirectEditProfileError);
      } else if (file.size() <= 1 || file.size() >= maxSize) {
        return redirectErrorPage(
            httpSession, amount, "error.bad_size", locale, redirectEditProfileError);
      } else if (!shopService.checkFiles(file)) {
        return redirectErrorPage(
            httpSession, amount, "error.bad_files", locale, redirectEditProfileError);
      } else if (!profileService.editProfileShop(
          profileShopDTO, (String) httpSession.getAttribute("filePath"), locale, file, phone)) {
        authenticationController.resetSession(
            httpSession, CODE_SMS_EDIT_SHOP_PARAM, EDIT_SHOP_PARAM);
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_PARAM, null, locale));
        return REDIRECT_PROFILE_ERROR;
      }
      authenticationController.resetSession(httpSession, CODE_SMS_EDIT_SHOP_PARAM, EDIT_SHOP_PARAM);
    } else {
      ProfileDTO profileDTO = (ProfileDTO) httpSession.getAttribute(EDIT_PARAM);
      if (amount == maxAmount) {
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM,
            messageSource.getMessage(ERROR_TOO_MANY_ATTEMPTS_PARAM, null, locale));
        authenticationController.resetSession(httpSession, CODE_SMS_EDIT_PARAM, EDIT_PARAM);
        return REDIRECT_PROFILE_ERROR;
      } else if (!PATTERN_CODE.matcher(verificationNumberSms).matches()) {
        return redirectErrorPage(
            httpSession, amount, ERROR_VALIDATION_MESSAGE, locale, redirectEditProfileError);
      } else if (!passwordEncoder.matches(
          verificationNumberSms, (String) httpSession.getAttribute(CODE_SMS_EDIT_PARAM))) {
        return redirectErrorPage(
            httpSession, amount, ERROR_BAD_SMS_CODE_PARAM, locale, redirectEditProfileError);
      } else if (!profileService.editProfile(profileDTO, phone)) {
        authenticationController.resetSession(httpSession, CODE_SMS_EDIT_PARAM, EDIT_PARAM);
        httpSession.setAttribute(
            ERROR_MESSAGE_PARAM, messageSource.getMessage(ERROR_UNEXPECTED_PARAM, null, locale));
        return REDIRECT_PROFILE_ERROR;
      }
      authenticationController.resetSession(httpSession, CODE_SMS_EDIT_PARAM, EDIT_PARAM);
    }
    httpSession.setAttribute(
        SUCCESS_MESSAGE_PARAM, messageSource.getMessage("success.data.updated", null, locale));
    return "redirect:/profile?success";
  }

  @GetMapping("/password_change")
  public String changePasswordPage(
      Model model,
      Principal principal,
      @RequestParam(required = false) String reset,
      @RequestParam(required = false) String error,
      HttpSession httpSession) {
    String url =
        resetAndErrorParams(error, reset, model, httpSession, CODE_SMS_CHANGE_PARAM, null, true);
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
      return REDIRECT_PROFILE_ERROR;
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
      return REDIRECT_PROFILE_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_CHANGE_PARAM, null);
    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return "redirect:/login?success";
  }

  @GetMapping("/delete_account")
  public String deleteAccountPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String reset,
      Principal principal,
      Model model,
      HttpSession httpSession) {
    String url =
        resetAndErrorParams(error, reset, model, httpSession, CODE_SMS_DELETE_PARAM, null, true);
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
      return REDIRECT_PROFILE_ERROR;
    } else if (!PATTERN_CODE.matcher(verificationNumberSms).matches()) {
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
      return REDIRECT_PROFILE_ERROR;
    }
    authenticationController.resetSession(httpSession, CODE_SMS_CHANGE_PARAM, null);
    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return "redirect:/login?success";
  }

  private String resetAndErrorParams(
      String error,
      String reset,
      Model model,
      HttpSession httpSession,
      String codeSmsParam,
      String formObjectParam,
      boolean resetSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    } else if (reset != null) {
      authenticationController.resetSession(httpSession, codeSmsParam, formObjectParam);
      return REDIRECT_PROFILE;
    } else if (resetSession) {
      authenticationController.resetSession(httpSession, codeSmsParam, formObjectParam);
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
