package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.misc.AddressConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class ShopServiceTest {
  private static final String TEST_ACCOUNT_NUMBER = "12312312312312312312312";
  private static final String TEST_SHOP_NAME = "test";
  private static final String TEST_CONTENT_TYPE = "image/png";
  private static final String TEST_FILE_NAME = "file";
  private static final String TEST_FILE1_NAME = "file1";
  private static final String TEST_FILE2_NAME = "file2";
  private static final String TEST_FILE3_NAME = "file3";
  private static final String TEST_FILE4_NAME = "file4";
  private static final String TEST_OTHER_CONTENT_TYPE = "application/pdf";
  private static final String TEST_CARD_ID = "523456789012341678901234";
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012341678901234");
  private static final LocalDateTime TEST_LOCALE_DATE_TIME = LocalDateTime.of(2024, 12, 2, 2, 2);
  private static MockedStatic<LocalDateTime> localDateTimeMockedStatic;
  private Order order;
  private Map<String, Integer> productsId;
  @Mock private AddressConverter addressConverter;
  @Mock private RestTemplate restTemplate;
  @Mock private AccountRepository accountRepository;
  @Mock private ShopRepository shopRepository;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  private ShopService shopService;

  @BeforeAll
  public static void setStatic() {
    localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    localDateTimeMockedStatic.when(LocalDateTime::now).thenReturn(TEST_LOCALE_DATE_TIME);
  }

  @AfterAll
  public static void cleanStatic() {
    localDateTimeMockedStatic.close();
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    shopService =
        new ShopService(
            addressConverter,
            accountRepository,
            shopRepository,
            restTemplate,
            mongoTransactionManager,
            mongoTemplate);
  }

  @Test
  public void shouldReturnTrueAtCheckImageWhenEverythingOk() throws IOException {
    final int goodWidth = 460;
    final int goodHeight = 460;
    MultipartFile multipartFile = setFile(TEST_CONTENT_TYPE, goodWidth, goodHeight);

    assertTrue(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldReturnFalseAtCheckImageWhenEmptyFile() {
    assertFalse(shopService.checkImage(new MockMultipartFile(TEST_FILE_NAME, new byte[0])));
  }

  @Test
  public void shouldReturnFalseAtCheckImageWhenToLowHeight() throws IOException {
    final int goodWidth = 460;
    final int badHeight = 410;
    MultipartFile multipartFile = setFile(TEST_CONTENT_TYPE, goodWidth, badHeight);

    assertFalse(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldReturnFalseAtCheckImageWhenToLowWidth() throws IOException {
    final int badWidth = 410;
    final int goodHeight = 460;
    MultipartFile multipartFile = setFile(TEST_CONTENT_TYPE, badWidth, goodHeight);

    assertFalse(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldReturnFalseAtCheckImageWhenBadType() throws IOException {
    final int goodWidth = 460;
    final int goodHeight = 460;
    MultipartFile multipartFile = setFile("pdf", goodWidth, goodHeight);

    assertFalse(shopService.checkImage(multipartFile));
  }

  private MultipartFile setFile(String contentType, int width, int height) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
    return new MockMultipartFile(
        TEST_FILE_NAME, TEST_FILE_NAME, contentType, byteArrayOutputStream.toByteArray());
  }

  @Test
  public void shouldReturnFalseAtCheckImageWhenTooMuchSize() throws IOException {
    final int width = 1000;
    final int height = 1000;
    final int maxRGB = 256;
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    Random random = new Random();
    graphics2D.setColor(Color.RED);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        graphics2D.setColor(
            new Color(
                random.nextInt(0, maxRGB), random.nextInt(0, maxRGB), random.nextInt(0, maxRGB)));
        graphics2D.drawRect(i, j, 1, 1);
      }
    }
    graphics2D.dispose();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            TEST_FILE_NAME, TEST_FILE_NAME, TEST_CONTENT_TYPE, byteArrayOutputStream.toByteArray());

    assertFalse(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldReturnTrueAtCheckAccountNumberExistsWhenNullPhone() {
    when(accountRepository.existsByAccountNumberOtherThanPhone(TEST_ACCOUNT_NUMBER, null))
        .thenReturn(true);

    assertTrue(shopService.checkAccountNumberExists(TEST_ACCOUNT_NUMBER, null));
  }

  @Test
  public void shouldReturnFalseAtCheckAccountNumberExistsWhenNotFoundAndNullPhone() {
    when(accountRepository.existsByAccountNumberOtherThanPhone(TEST_ACCOUNT_NUMBER, null))
        .thenReturn(false);

    assertFalse(shopService.checkAccountNumberExists(TEST_ACCOUNT_NUMBER, null));
  }

  @Test
  public void shouldReturnTrueAtCheckShopNameExistsWhenNullPhone() {
    when(accountRepository.existsByNameOtherThanPhone(TEST_SHOP_NAME, null)).thenReturn(true);

    assertTrue(shopService.checkShopNameExists(TEST_SHOP_NAME, null));
  }

  @Test
  public void shouldReturnFalseAtCheckShopNameExistsWhenNotFoundAndNullPhone() {
    when(accountRepository.existsByNameOtherThanPhone(TEST_SHOP_NAME, null)).thenReturn(false);

    assertFalse(shopService.checkShopNameExists(TEST_SHOP_NAME, null));
  }

  @Test
  public void shouldReturnTrueAtCheckAccountNumberWhenEverythingOk() {
    final int statusOk = 200;
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatusCode.valueOf(statusOk));

    when(restTemplate.exchange(
            "https://api.ibanapi.com/v1/validate-basic/PL12312312312312312312312?api_key=null",
            HttpMethod.GET,
            null,
            String.class))
        .thenReturn(response);

    assertTrue(shopService.checkAccountNumber(TEST_ACCOUNT_NUMBER));
  }

  @Test
  public void shouldReturnFalseAtCheckAccountNumberWhenInvalidAccountNumber() {
    when(restTemplate.exchange(
            "https://api.ibanapi.com/v1/validate-basic/PL12312312312312312312312?api_key=null",
            HttpMethod.GET,
            null,
            String.class))
        .thenThrow(HttpClientErrorException.class);

    assertFalse(shopService.checkAccountNumber(TEST_ACCOUNT_NUMBER));
  }

  @Test
  public void shouldReturnTrueAtCheckPointExistsWhenNullPhone() {
    AddressDTO addressDTO1 = new AddressDTO();
    AddressDTO addressDTO2 = new AddressDTO();
    List<AddressDTO> addressDTOList = List.of(addressDTO1, addressDTO2);
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = List.of(address1, address2);

    when(addressConverter.getEntities(addressDTOList)).thenReturn(addresses);
    when(accountRepository.existsByPointsOtherThanPhone(address1, null)).thenReturn(true);

    assertTrue(shopService.checkPointsExists(addressDTOList, null));
  }

  @Test
  public void shouldReturnFalseAtCheckPointExistsWhenNoTheSamePlacesWhenNullPhone() {
    AddressDTO addressDTO1 = new AddressDTO();
    AddressDTO addressDTO2 = new AddressDTO();
    List<AddressDTO> addressDTOList = List.of(addressDTO1, addressDTO2);
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = List.of(address1, address2);

    when(addressConverter.getEntities(addressDTOList)).thenReturn(addresses);

    assertFalse(shopService.checkPointsExists(addressDTOList, null));
  }

  @Test
  public void shouldSuccessAtSaveTempFile() throws IOException {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);

    String filePath = shopService.saveTempFile(multipartFile);
    assertTrue(filePath.contains("upload_"));
    assertTrue(filePath.contains(".tmp"));

    Path path = Path.of(filePath);
    Files.deleteIfExists(path);
  }

  @Test
  public void shouldReturnTrueAtCheckFilesWhenEverythingOk() {
    MockMultipartFile multipartFile1 =
        new MockMultipartFile(
            TEST_FILE1_NAME, TEST_FILE1_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile2 =
        new MockMultipartFile(
            TEST_FILE2_NAME, TEST_FILE2_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile3 =
        new MockMultipartFile(
            TEST_FILE3_NAME, TEST_FILE3_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile4 = new MockMultipartFile(TEST_FILE4_NAME, new byte[0]);
    List<MultipartFile> list =
        List.of(multipartFile1, multipartFile2, multipartFile3, multipartFile4);

    assertTrue(shopService.checkFiles(list));
  }

  @Test
  public void shouldReturnFalseAtCheckFilesWhenNoPdf() {
    MockMultipartFile multipartFile1 =
        new MockMultipartFile(
            TEST_FILE1_NAME, TEST_FILE1_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile2 =
        new MockMultipartFile(TEST_FILE2_NAME, TEST_FILE2_NAME, "application/cos", new byte[1]);
    MockMultipartFile multipartFile3 =
        new MockMultipartFile(
            TEST_FILE3_NAME, TEST_FILE3_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile4 = new MockMultipartFile(TEST_FILE4_NAME, new byte[0]);
    List<MultipartFile> list =
        List.of(multipartFile1, multipartFile2, multipartFile3, multipartFile4);

    assertFalse(shopService.checkFiles(list));
  }

  @Test
  public void shouldReturnFalseAtCheckFilesWhenTooManyEmptyFiles() {
    MockMultipartFile multipartFile1 =
        new MockMultipartFile(
            TEST_FILE1_NAME, TEST_FILE1_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile2 =
        new MockMultipartFile(
            TEST_FILE2_NAME, TEST_FILE2_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile3 = new MockMultipartFile(TEST_FILE3_NAME, new byte[0]);
    MockMultipartFile multipartFile4 = new MockMultipartFile(TEST_FILE4_NAME, new byte[0]);
    List<MultipartFile> list =
        List.of(multipartFile1, multipartFile2, multipartFile3, multipartFile4);

    assertFalse(shopService.checkFiles(list));
  }

  @Test
  public void shouldReturnListOfShopNamesAtGetShopNamesByPrefixWhenEverythingOk() {
    List<String> shopNames = List.of("Zahir", "Zamek");

    when(shopRepository.getShopNamesByPrefix("^Za")).thenReturn(shopNames);

    assertEquals(shopNames, shopService.getShopNamesByPrefix("Za"));
  }

  @Test
  public void shouldReturnEmptyListAtGetShopNamesByPrefixWhenPrefixIsBlank() {
    assertEquals(List.of(), shopService.getShopNamesByPrefix(""));
  }

  @Test
  public void shouldReturnFalseAtBuyProductsWhenGetException() {
    assertFalse(shopService.buyProducts(Map.of(), "1234"));
  }

  @Test
  public void shouldReturnFalseAtBuyProductsWhenNotFoundProduct() {
    Map<String, Integer> productsId = Map.of(TEST_PRODUCT_ID.toString(), 1);

    when(mongoTemplate.findOne(query(Criteria.where("id").is(TEST_PRODUCT_ID)), Product.class))
        .thenReturn(null);

    assertFalse(shopService.buyProducts(productsId, "523456789012341678901234"));
  }

  @Test
  public void shouldReturnTrueAtBuyProductsWhenPromotionNotFound() {
    final int amount = 3000;
    setDataForBuyProducts(amount, null);

    assertTrue(shopService.buyProducts(productsId, TEST_CARD_ID));
    verify(mongoTemplate).save(order);
  }

  @Test
  public void shouldReturnTrueAtBuyProductsWhenPromotionFoundAndCountNothing() {
    final int amountPromotion = 2000;
    Promotion promotion = new Promotion();
    promotion.setAmount(amountPromotion);
    promotion.setCount(null);
    promotion.setProductId(TEST_PRODUCT_ID);
    setDataForBuyProducts(amountPromotion, promotion);

    assertTrue(shopService.buyProducts(productsId, TEST_CARD_ID));
    verify(mongoTemplate).save(order);
  }

  @Test
  public void shouldReturnTrueAtBuyProductsWhenPromotionFoundAndCountLast() {
    final int amountPromotion = 2000;
    Promotion promotion = new Promotion();
    promotion.setAmount(amountPromotion);
    promotion.setCount(1);
    promotion.setProductId(TEST_PRODUCT_ID);
    setDataForBuyProducts(amountPromotion, promotion);

    assertTrue(shopService.buyProducts(productsId, TEST_CARD_ID));
    verify(mongoTemplate).save(order);
    verify(mongoTemplate).remove(promotion);
  }

  @Test
  public void shouldReturnTrueAtBuyProductsWhenPromotionFoundAndCountNotLast() {
    final int amountPromotion = 2000;
    Promotion promotion = new Promotion();
    promotion.setAmount(amountPromotion);
    promotion.setCount(2);
    promotion.setProductId(TEST_PRODUCT_ID);
    Promotion expectedPromotion = new Promotion();
    expectedPromotion.setAmount(amountPromotion);
    expectedPromotion.setCount(1);
    expectedPromotion.setProductId(TEST_PRODUCT_ID);
    setDataForBuyProducts(amountPromotion, promotion);

    assertTrue(shopService.buyProducts(productsId, TEST_CARD_ID));
    verify(mongoTemplate).save(order);
    verify(mongoTemplate).save(expectedPromotion);
  }

  private void setDataForBuyProducts(int orderPromotion, Promotion promotion) {
    final int amount = 3000;
    final ObjectId testCardId = new ObjectId(TEST_CARD_ID);
    final ObjectId testShopId = new ObjectId("123456789011541678901234");
    productsId = Map.of(TEST_PRODUCT_ID.toString(), 1);
    Product product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setShopId(testShopId);
    product.setAmount(amount);
    order = new Order();
    order.setUsed(false);
    order.setCreatedAt(TEST_LOCALE_DATE_TIME);
    order.setCardId(testCardId);
    order.setProductId(TEST_PRODUCT_ID);
    order.setShopId(testShopId);
    order.setAmount(orderPromotion);

    when(mongoTemplate.findOne(query(Criteria.where("id").is(TEST_PRODUCT_ID)), Product.class))
        .thenReturn(product);
    when(mongoTemplate.findOne(
            query(Criteria.where("productId").is(TEST_PRODUCT_ID)), Promotion.class))
        .thenReturn(promotion);
  }
}
