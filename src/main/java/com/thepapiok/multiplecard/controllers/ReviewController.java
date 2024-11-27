package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.services.ResultService;
import com.thepapiok.multiplecard.services.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import org.bson.types.ObjectId;
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
  private final ResultService resultService;

  @Autowired
  public ReviewController(
      MessageSource messageSource, ReviewService reviewService, ResultService resultService) {
    this.messageSource = messageSource;
    this.reviewService = reviewService;
    this.resultService = resultService;
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
    session.setAttribute(
        "successMessage", messageSource.getMessage("addReview.success.review_added", null, locale));
    return "redirect:/?success";
  }

  @DeleteMapping
  @ResponseBody
  public boolean removeReview(@RequestParam String id, Principal principal) {
    return reviewService.removeReview(new ObjectId(id), principal.getName());
  }

  @PostMapping("/addLike")
  @ResponseBody
  public boolean addLike(@RequestParam String id, Principal principal) {
    return reviewService.addLike(new ObjectId(id), principal.getName());
  }

  @PostMapping("/deleteLike")
  @ResponseBody
  public boolean deleteLike(@RequestParam String id, Principal principal) {
    return reviewService.deleteLike(new ObjectId(id), principal.getName());
  }

  @GetMapping
  public String reviewPage(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "count") String field,
      @RequestParam(defaultValue = "true") Boolean isDescending,
      @RequestParam(defaultValue = "") String text,
      Model model,
      Principal principal) {
    String phone = null;
    if (principal != null) {
      phone = principal.getName();
      model.addAttribute("yourReview", reviewService.getReview(phone));
    }
    List<ReviewGetDTO> reviews = reviewService.getReviews(phone, page, field, isDescending, text);
    int maxPage = reviewService.getMaxPage();
    model.addAttribute("field", field);
    model.addAttribute("isDescending", isDescending);
    model.addAttribute("pages", resultService.getPages(page + 1, maxPage));
    model.addAttribute("pageSelected", page + 1);
    model.addAttribute("reviews", reviews);
    model.addAttribute("reviewsEmpty", reviews.size() == 0);
    model.addAttribute("principal", principal != null);
    model.addAttribute("maxPage", maxPage);
    return "reviewPage";
  }
}
