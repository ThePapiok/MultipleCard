package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.services.ReviewService;
import com.thepapiok.multiplecard.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

  private final MessageSource messageSource;
  private final ReviewService reviewService;
  private final UserService userService;

  @Autowired
  public ReviewController(
      MessageSource messageSource, ReviewService reviewService, UserService userService) {
    this.messageSource = messageSource;
    this.reviewService = reviewService;
    this.userService = userService;
  }

  @PostMapping
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

  @DeleteMapping
  @ResponseBody
  public boolean removeReview(@RequestParam String id, Principal principal) {
    return reviewService.removeReview(id, principal.getName());
  }

  @PostMapping("/addLike")
  @ResponseBody
  public boolean addLike(@RequestParam String id, Principal principal) {
    return reviewService.addLike(id, principal.getName());
  }

  @PostMapping("/deleteLike")
  @ResponseBody
  public boolean deleteLike(@RequestParam String id, Principal principal) {
    return reviewService.deleteLike(id, principal.getName());
  }

  @GetMapping
  public String reviewPage(Model model, Principal principal) {
    String phone = null;
    if (principal != null) {
      phone = principal.getName();
    }
    List<ReviewGetDTO> reviews = userService.getReviews(phone);
    model.addAttribute("reviews", reviews);
    model.addAttribute("reviewsSize", reviews.size());
    model.addAttribute("principal", principal != null);
    return "reviewPage";
  }
}
