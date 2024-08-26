package com.thepapiok.multiplecard.misc;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("prod")
public class Schedule {

  @Scheduled(fixedRate = 840000)
  public void checkHealth() {
    RestTemplate restTemplate = new RestTemplate();
    try {
      if (restTemplate.getForEntity("/health", String.class).getStatusCode() != HttpStatus.OK) {
        // TODO - send mail to multiplecard
      }
    } catch (Exception e) {
      // TODO - send mail to multiplecard
    }
  }
}
