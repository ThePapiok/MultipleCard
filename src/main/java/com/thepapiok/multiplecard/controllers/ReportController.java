package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.dto.ReportsDTO;
import com.thepapiok.multiplecard.services.ConvertService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReportService;
import com.thepapiok.multiplecard.services.ReviewService;
import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportController {
  private final ReportService reportService;
  private final ProductService productService;
  private final ReviewService reviewService;
  private final AdminPanelController adminPanelController;
  private final ConvertService convertService;

  @Autowired
  public ReportController(
      ReportService reportService,
      ProductService productService,
      ReviewService reviewService,
      AdminPanelController adminPanelController,
      ConvertService convertService) {
    this.reportService = reportService;
    this.productService = productService;
    this.reviewService = reviewService;
    this.adminPanelController = adminPanelController;
    this.convertService = convertService;
  }

  @GetMapping("/reports")
  public String reportsPage(Model model, HttpSession httpSession) {
    HashSet<String> ids = convertService.getIds(httpSession);
    ReportsDTO reportsDTO = reportService.getReport(ids);
    if (reportsDTO == null && ids.size() != 0) {
      ids.clear();
      reportsDTO = reportService.getReport(ids);
    }
    if (reportsDTO != null) {
      boolean isProduct = reportsDTO.isProduct();
      String id = reportsDTO.getId();
      ids.add(id);
      httpSession.setAttribute("ids", ids);
      if (isProduct) {
        model.addAttribute("product", productService.getProductWithShopDTOById(id));
      } else {
        model.addAttribute("review", reviewService.getReviewById(id));
      }
      model.addAttribute("id", id);
      model.addAttribute("isProduct", isProduct);
      model.addAttribute("reports", reportsDTO.getReports());
    } else {
      model.addAttribute("reports", null);
    }
    return "reportsPage";
  }

  @PostMapping("/reject_report")
  @ResponseBody
  public boolean nextReport(@RequestParam String id) {
    return reportService.removeReport(id);
  }

  @PostMapping("/block_at_report")
  @ResponseBody
  public boolean blockUser(@RequestParam String id, @RequestParam String isProduct, Locale locale) {
    if (!adminPanelController.blockUser(id, Boolean.parseBoolean(isProduct), locale)) {
      return false;
    }
    return reportService.removeReport(id);
  }

  @PostMapping("/delete_and_block")
  @ResponseBody
  public boolean blockAndDelete(
      @RequestParam String id, @RequestParam String isProduct, Locale locale) {
    boolean isProductBool = Boolean.parseBoolean(isProduct);
    if (!adminPanelController.blockUser(id, isProductBool, locale)) {
      return false;
    }
    if (isProductBool) {
      return adminPanelController.deleteProduct(id);
    } else {
      return adminPanelController.deleteReview(id);
    }
  }
}
