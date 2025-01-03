package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.BlockedProduct;
import com.thepapiok.multiplecard.repositories.BlockedIpRepository;
import com.thepapiok.multiplecard.repositories.BlockedProductRepository;
import com.thepapiok.multiplecard.services.BlockedProductService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.ProductService;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("prod")
public class Schedule {
  private final BlockedProductRepository blockedProductRepository;
  private final BlockedIpRepository blockedIpRepository;
  private final ProductService productService;
  private final BlockedProductService blockedProductService;
  private final RestTemplate restTemplate;
  private final EmailService emailService;

  @Autowired
  public Schedule(
      BlockedProductRepository blockedProductRepository,
      BlockedIpRepository blockedIpRepository,
      ProductService productService,
      BlockedProductService blockedProductService,
      RestTemplate restTemplate,
      EmailService emailService) {
    this.blockedProductRepository = blockedProductRepository;
    this.blockedIpRepository = blockedIpRepository;
    this.productService = productService;
    this.blockedProductService = blockedProductService;
    this.restTemplate = restTemplate;
    this.emailService = emailService;
  }

  @Scheduled(fixedRate = 840000)
  public void checkHealth() {
    restTemplate.getForEntity("https://multiplecard-neq8.onrender.com/health", String.class);
  }

  @Scheduled(fixedRate = 86400000)
  public void checkBlocked() {
    final int periodIs7 = 7;
    final int periodIs1 = 1;
    final int periodIs0 = 0;
    int days;
    for (BlockedProduct blockedProduct : blockedProductRepository.findAll()) {
      days = Period.between(blockedProduct.getExpiredAt(), LocalDate.now()).getDays();
      if (days == periodIs7) {
        blockedProductService.sendWarning(
            blockedProduct.getProductId(), "blockedProductService.warning.text_7days");
      } else if (days == periodIs1) {
        blockedProductService.sendWarning(
            blockedProduct.getProductId(), "blockedProductService.warning.text_1day");
      } else if (days == periodIs0) {
        if (!productService.deleteProducts(List.of(blockedProduct.getProductId()))) {
          emailService.sendEmail(
              "Błąd - " + blockedProduct.getProductId(),
              "multiplecard@gmail.com",
              "Wystąpił błąd podczas usuwania produktu po upłynięciu terminu blokady.");
        }
      }
    }
    blockedIpRepository.deleteAll();
  }
}
