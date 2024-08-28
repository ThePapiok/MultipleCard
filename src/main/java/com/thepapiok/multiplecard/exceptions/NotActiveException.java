package com.thepapiok.multiplecard.exceptions;

import org.springframework.security.core.AuthenticationException;

public class NotActiveException extends AuthenticationException {
  public NotActiveException(String msg) {
    super(msg);
  }

  public NotActiveException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
