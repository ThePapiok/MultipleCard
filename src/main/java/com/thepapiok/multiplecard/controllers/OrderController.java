package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.SearchCardDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";

  @GetMapping("/orders")
  public String orderPage(
      @RequestParam(required = false) String error, Model model, HttpSession httpSession) {
    if (error != null) {
      String message = (String) httpSession.getAttribute(ERROR_MESSAGE_PARAM);
      if (message != null) {
        model.addAttribute(ERROR_MESSAGE_PARAM, message);
        httpSession.removeAttribute(ERROR_MESSAGE_PARAM);
      }
    }
    Integer step = (Integer) httpSession.getAttribute("step");
    if (step == null) {
      model.addAttribute("searchCard", new SearchCardDTO());
      return "searchCardPage";
    } else if (step == 1) {
      return "typePinPage";
    } else {
      return "ordersPage";
    }
  }
}
