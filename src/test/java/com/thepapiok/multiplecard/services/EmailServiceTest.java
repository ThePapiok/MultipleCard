package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class EmailServiceTest {
  private EmailService emailService;
  @Mock private JavaMailSender javaMailSender;
  @Mock private MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    emailService = new EmailService(javaMailSender, messageSource);
  }

  @Test
  public void shouldSendEmailAtSendEmailWhenEverythingOk() {
    final String testTitle = "testTitle";
    final String testText = "testText";
    final String testTo = "testTo";
    SimpleMailMessage expectedSimpleMailMessage = new SimpleMailMessage();
    expectedSimpleMailMessage.setText(testText);
    expectedSimpleMailMessage.setTo(testTo);
    expectedSimpleMailMessage.setSubject(testTitle);

    emailService.sendEmail(testText, testTo, testTitle);
    verify(javaMailSender).send(expectedSimpleMailMessage);
  }

  @Test
  public void shouldSendEmailAtSendEmailWithAttachmentWhenEverythingOk() throws Exception {
    final String testShopId = "123456789012345678901234";
    final String testFirstName = "shopFirstName";
    final String testLastName = "shopLastName";
    final String testName = "shopName";
    final String testAccountNumber = "2134312243213432142134";
    final String testImageUrl = "imageUrl";
    final String testCity1 = "city1";
    final String testStreet1 = "street1";
    final String testCountry1 = "country1";
    final String testProvince1 = "province1";
    final String testPostalCode1 = "postalCode1";
    final String testHouseNumber1 = "houseNumber1";
    final String testCity2 = "city2";
    final String testStreet2 = "street2";
    final String testCountry2 = "country2";
    final String testProvince2 = "province2";
    final String testPostalCode2 = "postalCode2";
    final String testHouseNumber2 = "houseNumber2";
    final String testEmail = "email";
    final String testPhone = "phone";
    final String newLine = "\n";
    final byte[] file1Bytes = "testFile1".getBytes();
    final byte[] file2Bytes = "testFile2".getBytes();
    final Integer testApartmentNumber1 = null;
    final Integer testApartmentNumber2 = 5;
    DataSource expectedDataSource;
    DataSource dataSource;
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    Address address1 = new Address();
    address1.setCity(testCity1);
    address1.setStreet(testStreet1);
    address1.setCountry(testCountry1);
    address1.setProvince(testProvince1);
    address1.setHouseNumber(testHouseNumber1);
    address1.setApartmentNumber(testApartmentNumber1);
    address1.setPostalCode(testPostalCode1);
    Address address2 = new Address();
    address2.setCity(testCity2);
    address2.setStreet(testStreet2);
    address2.setCountry(testCountry2);
    address2.setProvince(testProvince2);
    address2.setHouseNumber(testHouseNumber2);
    address2.setApartmentNumber(testApartmentNumber2);
    address2.setPostalCode(testPostalCode2);
    Shop shop = new Shop();
    shop.setId(new ObjectId(testShopId));
    shop.setFirstName(testFirstName);
    shop.setLastName(testLastName);
    shop.setName(testName);
    shop.setAccountNumber(testAccountNumber);
    shop.setImageUrl(testImageUrl);
    shop.setPoints(List.of(address1, address2));
    Account account = new Account();
    account.setEmail(testEmail);
    account.setPhone(testPhone);
    List<MultipartFile> files = new ArrayList<>();
    MockMultipartFile file1 = new MockMultipartFile("file1", "file1", "pdf", file1Bytes);
    MockMultipartFile file2 = new MockMultipartFile("file2", "file2", "pdf", file2Bytes);
    files.add(file1);
    files.add(file2);
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessage expectedMimeMessage = mailSender.createMimeMessage();
    Locale locale = Locale.getDefault();
    StringBuilder text = new StringBuilder();

    MimeMessageHelper expectedMimeMessageHelper = new MimeMessageHelper(expectedMimeMessage, true);
    expectedMimeMessageHelper.setTo("multipleCard@gmail.com");
    expectedMimeMessageHelper.setSubject("Weryfikacja sklepu - " + testShopId);
    expectedMimeMessageHelper.addAttachment(file1.getOriginalFilename(), file1);
    expectedMimeMessageHelper.addAttachment(file2.getOriginalFilename(), file2);
    text.append("Imię: ")
        .append(testFirstName)
        .append(newLine)
        .append("Nazwisko: ")
        .append(testLastName)
        .append(newLine)
        .append("Email: ")
        .append(testEmail)
        .append(newLine)
        .append("Numer telefonu: ")
        .append(testPhone)
        .append(newLine)
        .append("Nazwa lokalu: ")
        .append(testName)
        .append(newLine)
        .append("Numer konta bankowego: ")
        .append(testAccountNumber)
        .append(newLine)
        .append("Logo sklepu: ")
        .append(testImageUrl)
        .append(newLine)
        .append("Lokal 1 :")
        .append(newLine)
        .append(" - Miejscowość: ")
        .append(testCity1)
        .append(newLine)
        .append(" - Kod pocztowy: ")
        .append(testPostalCode1)
        .append(newLine)
        .append(" - Ulica: ")
        .append(testStreet1)
        .append(newLine)
        .append(" - Nr domu: ")
        .append(testHouseNumber1)
        .append(newLine)
        .append(" - Nr lokalu: ")
        .append(testApartmentNumber1)
        .append(newLine)
        .append(" - Województwo: ")
        .append(testProvince1)
        .append(newLine)
        .append("Lokal 2 :")
        .append(newLine)
        .append(" - Miejscowość: ")
        .append(testCity2)
        .append(newLine)
        .append(" - Kod pocztowy: ")
        .append(testPostalCode2)
        .append(newLine)
        .append(" - Ulica: ")
        .append(testStreet2)
        .append(newLine)
        .append(" - Nr domu: ")
        .append(testHouseNumber2)
        .append(newLine)
        .append(" - Nr lokalu: ")
        .append(testApartmentNumber2)
        .append(newLine)
        .append(" - Województwo: ")
        .append(testProvince2);
    expectedMimeMessageHelper.setText(text.toString());

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(messageSource.getMessage("subject.verification_shop", null, locale))
        .thenReturn("Weryfikacja sklepu");
    when(messageSource.getMessage("firstName.text", null, locale)).thenReturn("Imię");
    when(messageSource.getMessage("lastName.text", null, locale)).thenReturn("Nazwisko");
    when(messageSource.getMessage("phone.text", null, locale)).thenReturn("Numer telefonu");
    when(messageSource.getMessage("shop_name.text", null, locale)).thenReturn("Nazwa lokalu");
    when(messageSource.getMessage("account_number.text", null, locale))
        .thenReturn("Numer konta bankowego");
    when(messageSource.getMessage("upload_image.text", null, locale)).thenReturn("Logo sklepu");
    when(messageSource.getMessage("place.text", null, locale)).thenReturn("Lokal");
    when(messageSource.getMessage("city.text", null, locale)).thenReturn("Miejscowość");
    when(messageSource.getMessage("postalCode.text", null, locale)).thenReturn("Kod pocztowy");
    when(messageSource.getMessage("street.text", null, locale)).thenReturn("Ulica");
    when(messageSource.getMessage("houseNumber.text", null, locale)).thenReturn("Nr domu");
    when(messageSource.getMessage("apartmentNumber.text", null, locale)).thenReturn("Nr lokalu");
    when(messageSource.getMessage("province.text", null, locale)).thenReturn("Województwo");

    emailService.sendEmailWithAttachment(shop, account, Locale.getDefault(), files);
    MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage);
    mimeMessageParser.parse();
    MimeMessageParser expectedMimeMessageParser = new MimeMessageParser(expectedMimeMessage);
    expectedMimeMessageParser.parse();
    assertEquals(expectedMimeMessageParser.getSubject(), mimeMessageParser.getSubject());
    assertEquals(expectedMimeMessageParser.getTo(), mimeMessageParser.getTo());
    assertEquals(expectedMimeMessageParser.getPlainContent(), mimeMessageParser.getPlainContent());
    for (int i = 0; i < mimeMessageParser.getAttachmentList().size(); i++) {
      dataSource = mimeMessageParser.getAttachmentList().get(i);
      expectedDataSource = expectedMimeMessageParser.getAttachmentList().get(i);
      assertEquals(expectedDataSource.getContentType(), dataSource.getContentType());
      assertEquals(expectedDataSource.getName(), dataSource.getName());
    }
  }
}
