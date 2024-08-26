package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReviewDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

  @GetMapping
  public String getLandingPage(Model model) {
    model.addAttribute("review", new ReviewDTO());
    return "landingPage";
  }
}
