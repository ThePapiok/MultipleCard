package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.BlockedProduct;
import com.thepapiok.multiplecard.repositories.BlockedRepository;
import com.thepapiok.multiplecard.services.ProductService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("prod")
public class Schedule {
  private final BlockedRepository blockedRepository;
  private final ProductService productService;

  @Autowired
  public Schedule(BlockedRepository blockedRepository, ProductService productService) {
    this.blockedRepository = blockedRepository;
    this.productService = productService;
  }

  @Scheduled(fixedRate = 840000)
  public void checkHealth() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate
        .getForEntity("https://multiplecard-neq8.onrender.com/health", String.class)
        .getStatusCode();
  }

  @Scheduled(fixedRate = 86400000)
  public void checkBlocked() {
    for (BlockedProduct blockedProduct :
        blockedRepository.findAllByExpiredAtIsBefore(LocalDate.now())) {
      productService.deleteProduct(blockedProduct.getProductId().toString());
    }
  }
}
