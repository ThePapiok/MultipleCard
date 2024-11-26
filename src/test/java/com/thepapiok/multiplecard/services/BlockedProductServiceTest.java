package com.thepapiok.multiplecard.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

public class BlockedProductServiceTest {
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_SHOP_ID = new ObjectId("234567889009876543214321");
  private static final String TEST_EMAIL = "test@test.pl";
  private BlockedProductService blockedProductService;
  @Mock private EmailService emailService;
  @Mock private AccountRepository accountRepository;
  @Mock private ProductRepository productRepository;
  @Mock private MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    blockedProductService =
        new BlockedProductService(
            emailService, accountRepository, productRepository, messageSource);
  }

  @Test
  public void shouldDoNothingAtSendWarningWhenProductNotFound() {
    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());

    blockedProductService.sendWarning(TEST_PRODUCT_ID, "bad");
    verifyNoInteractions(accountRepository);
    verifyNoInteractions(emailService);
  }

  @Test
  public void shouldDoNothingAtSendWarningWhenAccountNotFound() {
    Product product = new Product();
    product.setShopId(TEST_SHOP_ID);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(accountRepository.findById(TEST_SHOP_ID)).thenReturn(Optional.empty());

    blockedProductService.sendWarning(TEST_PRODUCT_ID, "bad");
    verifyNoInteractions(emailService);
  }

  @Test
  public void shouldSendEmailAtSendWarningWhenLocalePL() {
    final String testProductName = "product";
    Locale locale = Locale.getDefault();
    Product product = new Product();
    product.setShopId(TEST_SHOP_ID);
    product.setName(testProductName);
    Account account = new Account();
    account.setPhone("+4823412341234");
    account.setEmail(TEST_EMAIL);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(accountRepository.findById(TEST_SHOP_ID)).thenReturn(Optional.of(account));
    when(messageSource.getMessage("blockedProductService.warning.text_7days", null, locale))
        .thenReturn(
            "Przypominamy, że za 7 dni zostanie usunięty ten produkt. Odblokuj go w odpowiednim czasie.");
    when(messageSource.getMessage("blockedProductService.warning.title", null, locale))
        .thenReturn("Ostrzeżenie");

    blockedProductService.sendWarning(TEST_PRODUCT_ID, "blockedProductService.warning.text_7days");
    verify(emailService)
        .sendEmail(
            "Przypominamy, że za 7 dni zostanie usunięty ten produkt. Odblokuj go w odpowiednim czasie.",
            TEST_EMAIL,
            "Ostrzeżenie - " + testProductName);
  }

  @Test
  public void shouldSendEmailAtSendWarningWhenLocaleEN() {
    final String testProductName = "product";
    Locale locale = new Locale.Builder().setLanguage("eng").build();
    Product product = new Product();
    product.setShopId(TEST_SHOP_ID);
    product.setName(testProductName);
    Account account = new Account();
    account.setPhone("+1123412341234");
    account.setEmail(TEST_EMAIL);

    when(productRepository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(product));
    when(accountRepository.findById(TEST_SHOP_ID)).thenReturn(Optional.of(account));
    when(messageSource.getMessage("blockedProductService.warning.text_1day", null, locale))
        .thenReturn("Tomorrow your product will be deleted. You can still unblock it today.");
    when(messageSource.getMessage("blockedProductService.warning.title", null, locale))
        .thenReturn("Warning");

    blockedProductService.sendWarning(TEST_PRODUCT_ID, "blockedProductService.warning.text_1day");
    verify(emailService)
        .sendEmail(
            "Tomorrow your product will be deleted. You can still unblock it today.",
            TEST_EMAIL,
            "Warning - " + testProductName);
  }
}