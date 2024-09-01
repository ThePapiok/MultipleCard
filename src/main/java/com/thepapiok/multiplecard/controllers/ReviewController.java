package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.services.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReviewController {

  private final MessageSource messageSource;
  private final ReviewService reviewService;

  @Autowired
  public ReviewController(MessageSource messageSource, ReviewService reviewService) {
    this.messageSource = messageSource;
    this.reviewService = reviewService;
  }

  @PostMapping("/reviews")
  public String addReview(
      @Valid @ModelAttribute ReviewDTO review,
      BindingResult bindingResult,
      Locale locale,
      HttpSession session,
      Principal principal) {
    final String errorMessageParam = "errorMessage";
    final String redirectLandingPageError = "redirect:/?error";
    if (bindingResult.hasErrors()) {
      session.setAttribute(
          errorMessageParam, messageSource.getMessage("validation.incorrect_data", null, locale));
      return redirectLandingPageError;
    } else if (!reviewService.addReview(review, principal.getName())) {
      session.setAttribute(
          errorMessageParam, messageSource.getMessage("error.unexpected", null, locale));
      return redirectLandingPageError;
    }
    session.setAttribute("success", true);
    return "redirect:/?success";
  }
}
