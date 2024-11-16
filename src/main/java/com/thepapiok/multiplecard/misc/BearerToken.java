package com.thepapiok.multiplecard.misc;

import java.time.LocalDateTime;

public final class BearerToken {
  private String token;
  private LocalDateTime expiresIn;

  public BearerToken(String token, LocalDateTime expiresIn) {
    this.token = token;
    this.expiresIn = expiresIn;
  }

  public String getToken() {
    return token;
  }

  public LocalDateTime getExpiresIn() {
    return expiresIn;
  }
}
