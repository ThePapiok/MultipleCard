package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
public class PayUServiceTest {
  private static final String TEST_IP = "127.0.0.1";
  private static final String TEST_CARD_ID = "123456789012345678901234";
  private static final String TEST_ORDER_ID = "593456189012345678901231";
  private static final String NAME_PARAM = "name";
  private static final String UNIT_PRICE_PARAM = "unitPrice";
  private static final String QUANTITY_PARAM = "quantity";
  private String testBearerToken = "123wasdasad423412341weqr324";
  private PayUService payUService;
  @Mock private RestTemplate restTemplate;
  @Mock private ProductRepository productRepository;
  @Mock private PromotionRepository promotionRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    payUService = new PayUService(restTemplate, productRepository, promotionRepository);
  }

  @Test
  public void shouldReturnPairOfTrueAndPaymentUrlAtProductsOrderWhenEverythingOk()
      throws JsonProcessingException {
    final int maxTimeForOrderInSeconds = 900;
    final int expectedTotalAmount = 1745;
    final int testPromotionNewPrice = 245;
    final int testProductPrice = 500;
    final int testProduct1Quantity = 1;
    final int testProduct2Quantity = 3;
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
    final ObjectId testProduct1Id = new ObjectId("123456789012345678901231");
    final ObjectId testProduct2Id = new ObjectId("123456789012345678902331");
    ObjectMapper objectMapper = new ObjectMapper();
    setToken();

    ProductInfo productInfo1 = new ProductInfo();
    productInfo1.setProductId(testProduct1Id);
    productInfo1.setHasPromotion(true);
    ProductInfo productInfo2 = new ProductInfo();
    productInfo2.setProductId(testProduct2Id);
    productInfo2.setHasPromotion(false);
    Map<ProductInfo, Integer> productsInfo = new HashMap<>();
    productsInfo.put(productInfo1, testProduct1Quantity);
    productsInfo.put(productInfo2, testProduct2Quantity);
    Promotion promotion = new Promotion();
    promotion.setNewPrice(testPromotionNewPrice);
    Product product = new Product();
    product.setPrice(testProductPrice);
    List<Map<String, Object>> products = new ArrayList<>();
    Map<String, Object> product1 = new HashMap<>();
    product1.put(NAME_PARAM, objectMapper.writeValueAsString(productInfo1));
    product1.put(UNIT_PRICE_PARAM, testPromotionNewPrice);
    product1.put(QUANTITY_PARAM, testProduct1Quantity);
    Map<String, Object> product2 = new HashMap<>();
    product2.put(NAME_PARAM, objectMapper.writeValueAsString(productInfo2));
    product2.put(UNIT_PRICE_PARAM, testProductPrice);
    product2.put(QUANTITY_PARAM, testProduct2Quantity);
    products.add(product2);
    products.add(product1);
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
    HttpEntity<Map<String, Object>> requestEntityOrder = new HttpEntity<>(dataOrder, headersOrder);
    ResponseEntity<String> expectedResponseForOrder =
        new ResponseEntity<>(bodyOrder, HttpStatus.OK);
    Pair<Boolean, String> expectedPair = Pair.of(true, testPaymentUrl);

    when(promotionRepository.findNewPriceByProductId(testProduct1Id)).thenReturn(promotion);
    when(productRepository.findPriceById(testProduct2Id)).thenReturn(product);
    when(restTemplate.exchange(
            "https://secure.snd.payu.com/api/v2_1/orders",
            HttpMethod.POST,
            requestEntityOrder,
            String.class))
        .thenReturn(expectedResponseForOrder);

    assertEquals(
        expectedPair,
        payUService.productsOrder(productsInfo, TEST_CARD_ID, TEST_IP, TEST_ORDER_ID));
  }

  @Test
  public void shouldReturnPairOfFalseAndErrorAtProductsOrderWhenGetException()
      throws JsonProcessingException {
    final int maxTimeForOrderInSeconds = 900;
    final int expectedTotalAmount = 1745;
    final int testPromotionNewPrice = 245;
    final int testProductPrice = 500;
    final int testProduct1Quantity = 1;
    final int testProduct2Quantity = 3;
    final ObjectId testProduct1Id = new ObjectId("123456789012345678901231");
    final ObjectId testProduct2Id = new ObjectId("123456789012345678902331");
    setToken();

    ObjectMapper objectMapper = new ObjectMapper();
    ProductInfo productInfo1 = new ProductInfo();
    productInfo1.setProductId(testProduct1Id);
    productInfo1.setHasPromotion(true);
    ProductInfo productInfo2 = new ProductInfo();
    productInfo2.setProductId(testProduct2Id);
    productInfo2.setHasPromotion(false);
    Map<ProductInfo, Integer> productsInfo = new HashMap<>();
    productsInfo.put(productInfo1, testProduct1Quantity);
    productsInfo.put(productInfo2, testProduct2Quantity);
    Promotion promotion = new Promotion();
    promotion.setNewPrice(testPromotionNewPrice);
    Product product = new Product();
    product.setPrice(testProductPrice);
    List<Map<String, Object>> products = new ArrayList<>();
    Map<String, Object> product1 = new HashMap<>();
    product1.put(NAME_PARAM, objectMapper.writeValueAsString(productInfo1));
    product1.put(UNIT_PRICE_PARAM, testPromotionNewPrice);
    product1.put(QUANTITY_PARAM, testProduct1Quantity);
    Map<String, Object> product2 = new HashMap<>();
    product2.put(NAME_PARAM, objectMapper.writeValueAsString(productInfo2));
    product2.put(UNIT_PRICE_PARAM, testProductPrice);
    product2.put(QUANTITY_PARAM, testProduct2Quantity);
    products.add(product2);
    products.add(product1);
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
    HttpEntity<Map<String, Object>> requestEntityOrder = new HttpEntity<>(dataOrder, headersOrder);
    Pair<Boolean, String> expectedPair = Pair.of(false, "error");

    when(promotionRepository.findNewPriceByProductId(testProduct1Id)).thenReturn(promotion);
    when(productRepository.findPriceById(testProduct2Id)).thenReturn(product);
    when(restTemplate.exchange(
            "https://secure.snd.payu.com/api/v2_1/orders",
            HttpMethod.POST,
            requestEntityOrder,
            String.class))
        .thenThrow(RuntimeException.class);

    assertEquals(
        expectedPair,
        payUService.productsOrder(productsInfo, TEST_CARD_ID, TEST_IP, TEST_ORDER_ID));
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
