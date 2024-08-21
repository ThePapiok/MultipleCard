package com.thepapiok.multiplecard.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

  @Value("${ACCOUNT_SID}")
  private String accountSid;

  @Value("${AUTH_TOKEN}")
  private String authToken;

  @Value("${PHONE_NUMBER}")
  private String phoneNumber;

  @PostConstruct
  public void init() {
    Twilio.init(accountSid, authToken);
  }

  public void sendSms(String text, String number) {
    Message.creator(new PhoneNumber(number), new PhoneNumber(phoneNumber), text).create();
  }
}
