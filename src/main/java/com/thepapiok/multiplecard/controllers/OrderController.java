package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.SearchCardDTO;
import com.thepapiok.multiplecard.services.OrderService;
import com.thepapiok.multiplecard.services.ProductService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private final ProductService productService;
  private final OrderService orderService;
  private final MessageSource messageSource;

  @Autowired
  public OrderController(
      ProductService productService, OrderService orderService, MessageSource messageSource) {
    this.productService = productService;
    this.orderService = orderService;
    this.messageSource = messageSource;
  }

  @GetMapping("/orders")
  public String orderPage(
      @RequestParam(required = false) String error,
      Model model,
      HttpSession httpSession,
      Principal principal) {
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
      model.addAttribute(
          "products",
          productService.getProductsAtCard(
              principal.getName(), (String) httpSession.getAttribute("cardId")));
      return "ordersPage";
    }
  }

  @PostMapping("/finish_order")
  @ResponseBody
  public boolean finishOrder(
      @RequestBody List<String> ids, HttpSession httpSession, Principal principal, Locale locale) {
    ObjectId cardId = new ObjectId((String) httpSession.getAttribute("cardId"));
    boolean result = orderService.makeOrdersUsed(ids, cardId, principal.getName());
    if (result) {
      httpSession.setAttribute(
          "successMessage",
          messageSource.getMessage("ordersPage.success.finish_order", null, locale));
    }
    return result;
  }
}
