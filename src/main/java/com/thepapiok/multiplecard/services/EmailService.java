package com.thepapiok.multiplecard.services;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  private final JavaMailSender javaMailSender;
  private final MessageSource messageSource;

  @Autowired
  public EmailService(JavaMailSender javaMailSender, MessageSource messageSource) {
    this.javaMailSender = javaMailSender;
    this.messageSource = messageSource;
  }

  public void sendEmail(String text, String email, Locale locale) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(email);
    simpleMailMessage.setSubject(messageSource.getMessage("subject.verification", null, locale));
    simpleMailMessage.setText(text);
    javaMailSender.send(simpleMailMessage);
  }
}
