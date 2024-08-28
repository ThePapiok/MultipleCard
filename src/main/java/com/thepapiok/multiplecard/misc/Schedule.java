package com.thepapiok.multiplecard.misc;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("prod")
public class Schedule {

  @Scheduled(fixedRate = 840000)
  public void checkHealth() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate
        .getForEntity("https://multiplecard-neq8.onrender.com/health", String.class)
        .getStatusCode();
  }
}
