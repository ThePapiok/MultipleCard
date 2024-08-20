package com.thepapiok.multiplecard.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

  private static final String ACCOUNT_SID = System.getenv("ACCOUNT_SID");
  private static final String AUTH_TOKEN = System.getenv("AUTH_TOKEN");
  private static final String PHONE_NUMBER = System.getenv("PHONE_NUMBER");

  @PostConstruct
  public void init() {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
  }

  public void sendSms(String text, String number) {
    Message.creator(new PhoneNumber(number), new PhoneNumber(PHONE_NUMBER), text).create();
  }
}
