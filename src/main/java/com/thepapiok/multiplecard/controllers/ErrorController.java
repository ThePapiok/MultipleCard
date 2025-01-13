package com.thepapiok.multiplecard.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

  @GetMapping("/access_denied")
  public String accessDeniedPage() {
    return "accessDeniedPage";
  }
}
