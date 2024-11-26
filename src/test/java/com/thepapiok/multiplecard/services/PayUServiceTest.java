package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
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
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
public class PayUServiceTest {

  private static final String TEST_IP = "127.0.0.1";
  private static final String TEST_CARD_ID = "123456789012345678901234";
  private static final String TEST_ORDER_ID = "593456189012345678901231";
  private static final String PAYU_ORDERS_URL = "https://secure.snd.payu.com/api/v2_1/orders";
  private static final String PAYU_REFUNDS_URL =
      "https://secure.snd.payu.com/api/v2_1/orders/593456189012345678901231/refunds";
  private final String testBearerToken = "123wasdasad423412341weqr324";
  private Map<ProductInfo, Integer> productsInfo;
  private PayUService payUService;
  @Mock private RestTemplate restTemplate;
  @Mock private ProductRepository productRepository;
  @Mock private PromotionRepository promotionRepository;
  @Mock private MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    payUService =
        new PayUService(restTemplate, productRepository, promotionRepository, messageSource);
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
            + "\""
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
    dataOrder.put("continueUrl", null);
    dataOrder.put("customerIp", TEST_IP);
    dataOrder.put("validityTime", maxTimeForOrderInSeconds);
    dataOrder.put("merchantPosId", null);
    dataOrder.put("description", TEST_CARD_ID);
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

    assertTrue(payUService.makeRefund(TEST_ORDER_ID, Locale.getDefault()));
  }

  @Test
  public void shouldReturnTrueAtMakeRefundWhenBadStatus() {
    HttpEntity<Map<String, Object>> requestEntityRefund = setRequestForRefund();
    ResponseEntity<String> expectedResponseForRefund =
        new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    when(restTemplate.exchange(
            PAYU_REFUNDS_URL, HttpMethod.POST, requestEntityRefund, String.class))
        .thenReturn(expectedResponseForRefund);

    assertFalse(payUService.makeRefund(TEST_ORDER_ID, Locale.getDefault()));
  }

  @Test
  public void shouldReturnTrueAtMakeRefundWhenGetException() {
    HttpEntity<Map<String, Object>> requestEntityRefund = setRequestForRefund();

    when(restTemplate.exchange(
            PAYU_REFUNDS_URL, HttpMethod.POST, requestEntityRefund, String.class))
        .thenThrow(RuntimeException.class);

    assertFalse(payUService.makeRefund(TEST_ORDER_ID, Locale.getDefault()));
  }

  private HttpEntity<Map<String, Object>> setRequestForRefund() {
    setToken();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(testBearerToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    Map<String, Object> data = new HashMap<>(1);
    Map<String, String> description = new HashMap<>(1);
    description.put(
        "description",
        """
            Przepraszamy, wystąpił nieoczekiwany błąd związany z twoją transakcją.
            W najbliższym czasie pieniądze trafią z powrotem na twoje konto.""");
    data.put("refund", description);
    HttpEntity<Map<String, Object>> requestEntityRefund = new HttpEntity<>(data, headers);

    when(messageSource.getMessage("buyProducts.refund.message", null, Locale.getDefault()))
        .thenReturn(
            """
            Przepraszamy, wystąpił nieoczekiwany błąd związany z twoją transakcją.
            W najbliższym czasie pieniądze trafią z powrotem na twoje konto.""");
    return requestEntityRefund;
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
            + "\""
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
