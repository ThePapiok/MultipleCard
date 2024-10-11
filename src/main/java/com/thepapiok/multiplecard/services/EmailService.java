package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  public void sendEmailWithAttachment(
      Shop shop, Account account, Locale locale, List<MultipartFile> fileList)
      throws MessagingException {
    final String newLine = "\n";
    final String colon = ": ";
    final String newLineWithMinus = "\n - ";
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
    mimeMessageHelper.setTo("multiplecard@gmail.com");
    mimeMessageHelper.setSubject(
        messageSource.getMessage("subject.verification_shop", null, locale) + " - " + shop.getId());
    for (MultipartFile multipartFile : fileList) {
      if (multipartFile != null && !multipartFile.isEmpty()) {
        mimeMessageHelper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
      }
    }
    StringBuilder text =
        new StringBuilder(
            messageSource.getMessage("firstName.text", null, locale)
                + colon
                + shop.getFirstName()
                + newLine
                + messageSource.getMessage("lastName.text", null, locale)
                + colon
                + shop.getLastName()
                + "\nEmail: "
                + account.getEmail()
                + newLine
                + messageSource.getMessage("phone.text", null, locale)
                + colon
                + account.getPhone()
                + newLine
                + messageSource.getMessage("shop_name.text", null, locale)
                + colon
                + shop.getName()
                + newLine
                + messageSource.getMessage("account_number.text", null, locale)
                + colon
                + shop.getAccountNumber()
                + newLine
                + messageSource.getMessage("upload_image.text", null, locale)
                + colon
                + shop.getImageUrl());
    int index = 1;
    String placeLocale = messageSource.getMessage("place.text", null, locale);
    String cityLocale = messageSource.getMessage("city.text", null, locale);
    String postalCodeLocale = messageSource.getMessage("postalCode.text", null, locale);
    String streetLocale = messageSource.getMessage("street.text", null, locale);
    String houseNumberLocale = messageSource.getMessage("houseNumber.text", null, locale);
    String apartmentNumberLocale = messageSource.getMessage("apartmentNumber.text", null, locale);
    String provinceLocale = messageSource.getMessage("province.text", null, locale);
    for (Address address : shop.getPoints()) {
      text.append(newLine).append(placeLocale).append(" ").append(index).append(" :");
      text.append(newLineWithMinus).append(cityLocale).append(colon).append(address.getCity());
      text.append(newLineWithMinus)
          .append(postalCodeLocale)
          .append(colon)
          .append(address.getPostalCode());
      text.append(newLineWithMinus).append(streetLocale).append(colon).append(address.getStreet());
      text.append(newLineWithMinus)
          .append(houseNumberLocale)
          .append(colon)
          .append(address.getHouseNumber());
      text.append(newLineWithMinus)
          .append(apartmentNumberLocale)
          .append(colon)
          .append(address.getApartmentNumber());
      text.append(newLineWithMinus)
          .append(provinceLocale)
          .append(colon)
          .append(address.getProvince());
      index++;
    }
    mimeMessageHelper.setText(text.toString());
    javaMailSender.send(message);
  }
}
