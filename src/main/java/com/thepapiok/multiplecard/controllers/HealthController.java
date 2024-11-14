package com.thepapiok.multiplecard.controllers;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("prod")
public class HealthController {
  @GetMapping("/health")
  public ResponseEntity<String> getStatus() {
    return ResponseEntity.status(HttpStatus.OK).body("Server is running");
  }
}
