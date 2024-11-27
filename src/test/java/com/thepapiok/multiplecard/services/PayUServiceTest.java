package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.misc.ProductPayU;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
public class PayUServiceTest {

  private static final String TEST_IP = "127.0.0.1";
  private static final String TEST_PIN = "1234";
  private static final String TEST_ENCRYPTED_PIN = "saffsasfd123132421";
  private static final String TEST_PHONE = "1234123412341";
  private static final String TEST_CARD_ID = "123456789012345678901234";
  private static final String TEST_ORDER_ID = "593456189012345678901231";
  private static final String PAYU_ORDERS_URL = "https://secure.snd.payu.com/api/v2_1/orders";
  private static final String PAYU_REFUNDS_URL =
      "https://secure.snd.payu.com/api/v2_1/orders/593456189012345678901231/refunds";
  private static final String QUOTATION_MARK = "\"";
  private static final String DESCRIPTION_FIELD = "description";
  private static final String CARD_NAME_FIELD = "cardName";
  private final String testBearerToken = "123wasdasad423412341weqr324";
  private Map<ProductInfo, Integer> productsInfo;
  private PayUService payUService;
  @Mock private RestTemplate restTemplate;
  @Mock private ProductRepository productRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    payUService =
        new PayUService(restTemplate, productRepository, promotionRepository, passwordEncoder);
  }

  @Test
  public void shouldReturnPairOfTrueAndPaymentUrlAtProductsOrderWhenEverythingOk()
      throws JsonProcessingException {
    final String testPaymentUrl = "payu.com";
    final String bodyOrder =
        """
                {
                    "redirectUri": \""""
            + testPaymentUrl
            + QUOTATION_MARK
            + """
                }
                """;
    HttpEntity<Map<String, Object>> requestEntityOrder = setRequestOrder();

    ResponseEntity<String> expectedResponseForOrder =
        new ResponseEntity<>(bodyOrder, HttpStatus.FOUND);
    Pair<Boolean, String> expectedPair = Pair.of(true, testPaymentUrl);

    when(restTemplate.exchange(PAYU_ORDERS_URL, HttpMethod.POST, requestEntityOrder, String.class))
        .thenReturn(expectedResponseForOrder);

    assertEquals(
        expectedPair,
        payUService.productsOrder(
            productsInfo, TEST_CARD_ID, TEST_IP, TEST_ORDER_ID, Locale.getDefault()));
  }

  @Test
  public void shouldReturnPairOfFalseAndErrorAtProductsOrderWhenBadStatus()
      throws JsonProcessingException {
    HttpEntity<Map<String, Object>> requestEntityOrder = setRequestOrder();
    Pair<Boolean, String> expectedPair = Pair.of(false, "error");
    ResponseEntity<String> expectedResponseForOrder = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    when(restTemplate.exchange(PAYU_ORDERS_URL, HttpMethod.POST, requestEntityOrder, String.class))
        .thenReturn(expectedResponseForOrder);

    assertEquals(
        expectedPair,
        payUService.productsOrder(
            productsInfo, TEST_CARD_ID, TEST_IP, TEST_ORDER_ID, Locale.getDefault()));
  }

  @Test
  public void shouldReturnPairOfFalseAndErrorAtProductsOrderWhenGetException()
      throws JsonProcessingException {
    HttpEntity<Map<String, Object>> requestEntityOrder = setRequestOrder();
    Pair<Boolean, String> expectedPair = Pair.of(false, "error");

    when(restTemplate.exchange(PAYU_ORDERS_URL, HttpMethod.POST, requestEntityOrder, String.class))
        .thenThrow(RuntimeException.class);

    assertEquals(
        expectedPair,
        payUService.productsOrder(
            productsInfo, TEST_CARD_ID, TEST_IP, TEST_ORDER_ID, Locale.getDefault()));
  }

  private HttpEntity<Map<String, Object>> setRequestOrder() throws JsonProcessingException {
    final int maxTimeForOrderInSeconds = 900;
    final int expectedTotalAmount = 1745;
    final int testPromotionNewPrice = 245;
    final int testProductPrice = 500;
    final int testProduct1Quantity = 1;
    final int testProduct2Quantity = 3;
    final ObjectId testProduct1Id = new ObjectId("123456789012345678901231");
    final ObjectId testProduct2Id = new ObjectId("123456789012345678902331");
    setToken();

    ProductInfo productInfo1 = new ProductInfo();
    productInfo1.setProductId(testProduct1Id);
    productInfo1.setHasPromotion(true);
    ProductInfo productInfo2 = new ProductInfo();
    productInfo2.setProductId(testProduct2Id);
    productInfo2.setHasPromotion(false);
    productsInfo = new HashMap<>();
    productsInfo.put(productInfo1, testProduct1Quantity);
    productsInfo.put(productInfo2, testProduct2Quantity);
    ObjectMapper objectMapper = new ObjectMapper();
    Promotion promotion = new Promotion();
    promotion.setNewPrice(testPromotionNewPrice);
    Product product = new Product();
    product.setPrice(testProductPrice);
    List<ProductPayU> products = new ArrayList<>();
    Map<String, Object> productName1 = new HashMap<>(2);
    productName1.put("productId", testProduct1Id.toHexString());
    productName1.put("hasPromotion", true);
    ProductPayU productPayU1 = new ProductPayU();
    productPayU1.setName(objectMapper.writeValueAsString(productName1));
    productPayU1.setQuantity(testProduct1Quantity);
    productPayU1.setUnitPrice(testPromotionNewPrice);
    Map<String, Object> productName2 = new HashMap<>(2);
    productName2.put("productId", testProduct2Id.toHexString());
    productName2.put("hasPromotion", false);
    ProductPayU productPayU2 = new ProductPayU();
    productPayU2.setName(objectMapper.writeValueAsString(productName2));
    productPayU2.setQuantity(testProduct2Quantity);
    productPayU2.setUnitPrice(testProductPrice);
    products.add(productPayU2);
    products.add(productPayU1);
    HttpHeaders headersOrder = new HttpHeaders();
    headersOrder.setBearerAuth(testBearerToken);
    headersOrder.setContentType(MediaType.APPLICATION_JSON);
    Map<String, Object> dataOrder = new HashMap<>();
    dataOrder.put("extOrderId", TEST_ORDER_ID);
    dataOrder.put("notifyUrl", "nullbuy_products");
    dataOrder.put("continueUrl", "null?success");
    dataOrder.put("customerIp", TEST_IP);
    dataOrder.put("validityTime", maxTimeForOrderInSeconds);
    dataOrder.put("merchantPosId", null);
    dataOrder.put(DESCRIPTION_FIELD, TEST_CARD_ID);
    dataOrder.put("currencyCode", "PLN");
    dataOrder.put("totalAmount", expectedTotalAmount);
    dataOrder.put("products", products);
    dataOrder.put("additionalDescription", Locale.getDefault());
    HttpEntity<Map<String, Object>> requestEntityOrder = new HttpEntity<>(dataOrder, headersOrder);

    when(promotionRepository.findNewPriceByProductId(testProduct1Id)).thenReturn(promotion);
    when(productRepository.findPriceById(testProduct2Id)).thenReturn(product);

    return requestEntityOrder;
  }

  @Test
  public void shouldReturnTrueAtMakeRefundWhenEverythingOk() {
    HttpEntity<Map<String, Object>> requestEntityRefund = setRequestForRefund();
    ResponseEntity<String> expectedResponseForRefund = new ResponseEntity<>(HttpStatus.OK);

    when(restTemplate.exchange(
            PAYU_REFUNDS_URL, HttpMethod.POST, requestEntityRefund, String.class))
        .thenReturn(expectedResponseForRefund);

    assertTrue(payUService.makeRefund(TEST_ORDER_ID));
  }

  @Test
  public void shouldReturnTrueAtMakeRefundWhenBadStatus() {
    HttpEntity<Map<String, Object>> requestEntityRefund = setRequestForRefund();
    ResponseEntity<String> expectedResponseForRefund =
        new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    when(restTemplate.exchange(
            PAYU_REFUNDS_URL, HttpMethod.POST, requestEntityRefund, String.class))
        .thenReturn(expectedResponseForRefund);

    assertFalse(payUService.makeRefund(TEST_ORDER_ID));
  }

  @Test
  public void shouldReturnTrueAtMakeRefundWhenGetException() {
    HttpEntity<Map<String, Object>> requestEntityRefund = setRequestForRefund();

    when(restTemplate.exchange(
            PAYU_REFUNDS_URL, HttpMethod.POST, requestEntityRefund, String.class))
        .thenThrow(RuntimeException.class);

    assertFalse(payUService.makeRefund(TEST_ORDER_ID));
  }

  private HttpEntity<Map<String, Object>> setRequestForRefund() {
    setToken();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(testBearerToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    Map<String, Object> data = new HashMap<>(1);
    Map<String, String> description = new HashMap<>(1);
    description.put(DESCRIPTION_FIELD, "refund");
    data.put("refund", description);
    return new HttpEntity<>(data, headers);
  }

  @Test
  public void shouldReturnTrueAtCheckNotificationWhenEverythingOk() {
    assertTrue(
        payUService.checkNotification(
            """
                    {
                        "test": "multiplecard",
                        "owner": "ThePapiok"
                    }
                """,
            "test=etstest;signature=3775bbe2ef2d56cd7e5108f38d5399e8;user=testsetse"));
  }

  @Test
  public void shouldReturnFalseAtCheckNotificationWhenBadSignature() {
    assertFalse(
        payUService.checkNotification(
            """
                    {
                        "test": "multiplecard",
                        "owner": "ThePapiok"
                    }
                """,
            "test=etstest;signature=fsadf123123ddfasdfsadfsadf123213;user=testsetse"));
  }

  @Test
  public void shouldReturnNullAtCardOrderWhenBadStatus() throws JsonProcessingException {
    setToken();
    HttpEntity<Map<String, Object>> requestEntityOrder = setRequestForCardOrder();

    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setName(CARD_NAME_FIELD);

    when(passwordEncoder.encode(TEST_PIN)).thenReturn(TEST_ENCRYPTED_PIN);
    when(restTemplate.exchange(PAYU_ORDERS_URL, HttpMethod.POST, requestEntityOrder, String.class))
        .thenReturn(response);

    assertNull(
        payUService.cardOrder(
            TEST_CARD_ID, TEST_IP, Locale.getDefault(), TEST_PHONE, orderCardDTO));
  }

  @Test
  public void shouldReturnNullAtCardOrderWhenGetException() {
    setToken();

    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setName(CARD_NAME_FIELD);

    when(passwordEncoder.encode(TEST_PIN)).thenThrow(RuntimeException.class);

    assertNull(
        payUService.cardOrder(
            TEST_CARD_ID, TEST_IP, Locale.getDefault(), TEST_PHONE, orderCardDTO));
  }

  @Test
  public void shouldReturnPaymentUrlAtCardOrderWhenEverythingOk() throws JsonProcessingException {
    final String redirectUrl = "payu.com";
    final String body =
        """
      {
        "redirectUri": \""""
            + redirectUrl
            + QUOTATION_MARK
            + """
      }
    """;
    setToken();
    HttpEntity<Map<String, Object>> requestEntityOrder = setRequestForCardOrder();

    ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.FOUND);
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin(TEST_PIN);
    orderCardDTO.setName(CARD_NAME_FIELD);

    when(passwordEncoder.encode(TEST_PIN)).thenReturn(TEST_ENCRYPTED_PIN);
    when(restTemplate.exchange(PAYU_ORDERS_URL, HttpMethod.POST, requestEntityOrder, String.class))
        .thenReturn(response);

    assertEquals(
        redirectUrl,
        payUService.cardOrder(
            TEST_CARD_ID, TEST_IP, Locale.getDefault(), TEST_PHONE, orderCardDTO));
  }

  private HttpEntity<Map<String, Object>> setRequestForCardOrder() throws JsonProcessingException {
    final int cardPrice = 2000;
    final int maxTimeForOrderInSeconds = 900;
    ObjectMapper objectMapper = new ObjectMapper();
    HttpHeaders headersOrder = new HttpHeaders();
    headersOrder.setBearerAuth(testBearerToken);
    headersOrder.setContentType(MediaType.APPLICATION_JSON);
    Map<String, String> productName = new HashMap<>(2);
    productName.put("encryptedPin", TEST_ENCRYPTED_PIN);
    productName.put("name", CARD_NAME_FIELD);
    ProductPayU productPayU = new ProductPayU();
    productPayU.setName(objectMapper.writeValueAsString(productName));
    productPayU.setQuantity(1);
    productPayU.setUnitPrice(cardPrice);
    Map<String, Object> dataOrder = new HashMap<>();
    dataOrder.put("extOrderId", TEST_CARD_ID);
    dataOrder.put("notifyUrl", "nullbuy_card");
    dataOrder.put("continueUrl", "nullprofile?success");
    dataOrder.put("customerIp", TEST_IP);
    dataOrder.put("validityTime", maxTimeForOrderInSeconds);
    dataOrder.put("merchantPosId", null);
    dataOrder.put(DESCRIPTION_FIELD, TEST_PHONE);
    dataOrder.put("currencyCode", "PLN");
    dataOrder.put("totalAmount", cardPrice);
    dataOrder.put("products", List.of(productPayU));
    dataOrder.put("additionalDescription", Locale.getDefault());
    return new HttpEntity<>(dataOrder, headersOrder);
  }

  @Test
  public void shouldReturnNewBearerTokenAtTestBearerTokenExpiredWhenEverythingOk()
      throws JsonProcessingException {
    setToken();

    assertEquals(testBearerToken, payUService.testBearerTokenExpired());
  }

  @Test
  public void shouldReturnNewBearerTokenAtTestBearerTokenNotExpiredWhenEverythingOk()
      throws JsonProcessingException {
    setToken();

    assertEquals("testToken", payUService.testBearerTokenNotExpired());
  }

  private void setToken() {
    final String bodyToken =
        """
                {
                    "expires_in": 2314234,
                    "access_token": \""""
            + testBearerToken
            + QUOTATION_MARK
            + """
                }
                """;
    HttpHeaders headersToken = new HttpHeaders();
    headersToken.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> dataToken = new LinkedMultiValueMap<>();
    dataToken.add("grant_type", "client_credentials");
    dataToken.add("client_id", null);
    dataToken.add("client_secret", null);
    HttpEntity<MultiValueMap<String, String>> requestEntityToken =
        new HttpEntity<>(dataToken, headersToken);
    ResponseEntity<String> expectedResponseForToken =
        new ResponseEntity<>(bodyToken, HttpStatus.OK);

    when(restTemplate.exchange(
            "https://secure.snd.payu.com/pl/standard/user/oauth/authorize",
            HttpMethod.POST,
            requestEntityToken,
            String.class))
        .thenReturn(expectedResponseForToken);
  }
}
