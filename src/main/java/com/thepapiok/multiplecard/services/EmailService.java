package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
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
      RegisterShopDTO registerShopDTO,
      Locale locale,
      String id,
      List<MultipartFile> fileList,
      String url)
      throws MessagingException {
    final String newLine = "\n";
    final String colon = ": ";
    final String newLineWithMinus = "\n - ";
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
    mimeMessageHelper.setTo("multiplecard@gmail.com");
    mimeMessageHelper.setSubject(
        messageSource.getMessage("subject.verification_shop", null, locale) + " - " + id);
    for (MultipartFile multipartFile : fileList) {
      if (multipartFile != null && !multipartFile.isEmpty()) {
        mimeMessageHelper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
      }
    }
    StringBuilder text =
        new StringBuilder(
            messageSource.getMessage("firstName.text", null, locale)
                + colon
                + registerShopDTO.getFirstName()
                + newLine
                + messageSource.getMessage("lastName.text", null, locale)
                + colon
                + registerShopDTO.getLastName()
                + "\nEmail: "
                + registerShopDTO.getEmail()
                + newLine
                + messageSource.getMessage("phone.text", null, locale)
                + colon
                + registerShopDTO.getCallingCode()
                + registerShopDTO.getPhone()
                + newLine
                + messageSource.getMessage("shop_name.text", null, locale)
                + colon
                + registerShopDTO.getName()
                + newLine
                + messageSource.getMessage("registerShopPage.account_number.text", null, locale)
                + colon
                + registerShopDTO.getAccountNumber()
                + newLine
                + messageSource.getMessage("registerShopPage.upload_image.text", null, locale)
                + colon
                + url);
    int index = 1;
    String placeLocale = messageSource.getMessage("registerShopPage.place.text", null, locale);
    String cityLocale = messageSource.getMessage("city.text", null, locale);
    String postalCodeLocale = messageSource.getMessage("postalCode.text", null, locale);
    String streetLocale = messageSource.getMessage("street.text", null, locale);
    String houseNumberLocale = messageSource.getMessage("houseNumber.text", null, locale);
    String apartmentNumberLocale = messageSource.getMessage("apartmentNumber.text", null, locale);
    String provinceLocale = messageSource.getMessage("province.text", null, locale);
    for (AddressDTO addressDTO : registerShopDTO.getAddress()) {
      text.append(newLine).append(placeLocale).append(" ").append(index).append(" :");
      text.append(newLineWithMinus).append(cityLocale).append(colon).append(addressDTO.getCity());
      text.append(newLineWithMinus)
          .append(postalCodeLocale)
          .append(colon)
          .append(addressDTO.getPostalCode());
      text.append(newLineWithMinus)
          .append(streetLocale)
          .append(colon)
          .append(addressDTO.getStreet());
      text.append(newLineWithMinus)
          .append(houseNumberLocale)
          .append(colon)
          .append(addressDTO.getHouseNumber());
      text.append(newLineWithMinus)
          .append(apartmentNumberLocale)
          .append(colon)
          .append(addressDTO.getApartmentNumber());
      text.append(newLineWithMinus)
          .append(provinceLocale)
          .append(colon)
          .append(addressDTO.getProvince());
      index++;
    }
    mimeMessageHelper.setText(text.toString());
    javaMailSender.send(message);
  }
}
