package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReviewController {

  @PostMapping("/review")
  public String addReview(@ModelAttribute("review") ReviewDTO review) {
    return "redirect:/?added";
  }
}
