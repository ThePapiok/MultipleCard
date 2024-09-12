package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.CountryNamesDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.services.CardService;
import com.thepapiok.multiplecard.services.CountryService;
import com.thepapiok.multiplecard.services.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class ProfileController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_PARAM = "success";
  private final ProfileService profileService;
  private final CountryService countryService;
  private final MessageSource messageSource;
  private final CardService cardService;

  @Autowired
  public ProfileController(
      ProfileService profileService,
      CountryService countryService,
      MessageSource messageSource,
      CardService cardService) {
    this.profileService = profileService;
    this.countryService = countryService;
    this.messageSource = messageSource;
    this.cardService = cardService;
  }

  @GetMapping
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

  @PostMapping
  public String editProfile(
      @Valid @ModelAttribute ProfileDTO profile,
      BindingResult bindingResult,
      HttpSession httpSession,
      Locale locale,
      Principal principal) {
    final String redirectUserError = "redirect:/user?error";
    if (bindingResult.hasErrors()) {
      System.out.println(bindingResult);
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("validation.incorrect_data", null, locale));
      return redirectUserError;
    }
    if (!profileService.editProfile(profile, principal.getName())) {
      httpSession.setAttribute(
          ERROR_MESSAGE_PARAM, messageSource.getMessage("error.unexpected", null, locale));
      return redirectUserError;
    }
    httpSession.setAttribute(SUCCESS_PARAM, true);
    return "redirect:/user?success";
  }
}
