package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LandingPageController {
  private final MessageSource messageSource;

  @Autowired
  public LandingPageController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @GetMapping
  public String getLandingPage(
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String success,
      Model model,
      HttpSession session,
      Locale locale) {
    final String errorMessageParam = "errorMessage";
    final String successParam = "success";
    if (error != null) {
      String message = (String) session.getAttribute(errorMessageParam);
      if (message != null) {
        model.addAttribute(errorMessageParam, message);
        session.removeAttribute(errorMessageParam);
      }
    } else if (success != null) {
      Boolean successSesion = (Boolean) session.getAttribute(successParam);
      if (successSesion != null) {
        model.addAttribute(
            "successMessage",
            messageSource.getMessage("landingPage.review.success_added", null, locale));
        session.removeAttribute(successParam);
      }
    }
    model.addAttribute("review", new ReviewDTO());
    return "landingPage";
  }
}
