package com.thepapiok.multiplecard.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

  @Value("${TWILIO_ACCOUNT_SID}")
  private String accountSID;

  @Value("${TWILIO_AUTH_TOKEN}")
  private String authToken;

  @Value("${TWILIO_MESSAGING_SERVICE_SID}")
  private String messagingServiceSID;

  @PostConstruct
  public void init() {
    Twilio.init(accountSID, authToken);
  }

  public void sendSms(String text, String number) {
    Message.creator(new PhoneNumber(number), messagingServiceSID, text).create();
  }
}
