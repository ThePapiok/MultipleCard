package com.thepapiok.multiplecard.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.misc.BearerToken;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.misc.ProductPayU;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class PayUService {
  private static BearerToken bearerToken;
  private final RestTemplate restTemplate;
  private final ProductRepository productRepository;
  private final PromotionRepository promotionRepository;
  private final MessageSource messageSource;

  @Value("${PAYU_CLIENT_SECRET}")
  private String clientSecret;

  @Value("${PAYU_CLIENT_ID}")
  private String clientId;

  @Value("${PAYU_KEY_MD5}")
  private String keyMD5;

  @Value("${app.url}")
  private String appUrl;

  @Autowired
  public PayUService(
      RestTemplate restTemplate,
      ProductRepository productRepository,
      PromotionRepository promotionRepository,
      MessageSource messageSource) {
    this.restTemplate = restTemplate;
    this.productRepository = productRepository;
    this.promotionRepository = promotionRepository;
    this.messageSource = messageSource;
  }

  public Pair<Boolean, String> productsOrder(
      Map<ProductInfo, Integer> productsId,
      String cardId,
      String ip,
      String orderId,
      Locale locale) {
    try {
      final int maxTimeForOrderInSeconds = 900;
      ObjectId productId;
      int totalAmount = 0;
      int quantity;
      int price;
      ObjectMapper objectMapper = new ObjectMapper();
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(getToken());
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map<String, Object> data = new HashMap<>();
      data.put("extOrderId", orderId);
      data.put("notifyUrl", appUrl + "buy_products");
      data.put("continueUrl", appUrl);
      data.put("customerIp", ip);
      data.put("validityTime", maxTimeForOrderInSeconds);
      data.put("merchantPosId", clientId);
      data.put("description", cardId);
      data.put("additionalDescription", locale);
      data.put("currencyCode", "PLN");
      List<ProductPayU> products = new ArrayList<>();
      for (Map.Entry<ProductInfo, Integer> entry : productsId.entrySet()) {
        productId = entry.getKey().getProductId();
        quantity = entry.getValue();
        if (entry.getKey().isHasPromotion()) {
          price = promotionRepository.findNewPriceByProductId(productId).getNewPrice();
        } else {
          price = productRepository.findPriceById(productId).getPrice();
        }
        Map<String, Object> productName = new HashMap<>(2);
        productName.put("productId", productId.toHexString());
        productName.put("hasPromotion", entry.getKey().isHasPromotion());
        ProductPayU product = new ProductPayU();
        product.setName(objectMapper.writeValueAsString(productName));
        product.setUnitPrice(price);
        product.setQuantity(quantity);
        products.add(product);
        totalAmount += (price * quantity);
      }
      data.put("totalAmount", totalAmount);
      data.put("products", products);
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(data, headers);
      ResponseEntity<String> response =
          restTemplate.exchange(
              "https://secure.snd.payu.com/api/v2_1/orders",
              HttpMethod.POST,
              requestEntity,
              String.class);
      if (!response.getStatusCode().equals(HttpStatus.FOUND)) {
        return Pair.of(false, "error");
      }
      return Pair.of(true, objectMapper.readTree(response.getBody()).get("redirectUri").asText());
    } catch (Exception e) {
      return Pair.of(false, "error");
    }
  }

  public boolean makeRefund(String orderId, Locale locale) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(getToken());
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map<String, Object> data = new HashMap<>(1);
      Map<String, String> description = new HashMap<>(1);
      description.put(
          "description", messageSource.getMessage("buyProducts.refund.message", null, locale));
      data.put("refund", description);
      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(data, headers);
      ResponseEntity<String> response =
          restTemplate.exchange(
              "https://secure.snd.payu.com/api/v2_1/orders/" + orderId + "/refunds",
              HttpMethod.POST,
              requestEntity,
              String.class);
      return response.getStatusCode().equals(HttpStatus.OK);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean checkNotification(String body, String header) {
    String signature =
        Arrays.stream(
                Arrays.stream(header.split(";"))
                    .filter(e -> e.startsWith("signature"))
                    .toList()
                    .get(0)
                    .split("="))
            .toList()
            .get(1);
    return signature.equals(DigestUtils.md5DigestAsHex((body + keyMD5).getBytes()));
  }

  private String getToken() throws JsonProcessingException {
    if (bearerToken == null || LocalDateTime.now().isAfter(bearerToken.getExpiresIn())) {
      final float procentOfAll = 0.9F;
      int expiresIn;
      ObjectMapper objectMapper = new ObjectMapper();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
      data.add("grant_type", "client_credentials");
      data.add("client_id", clientId);
      data.add("client_secret", clientSecret);
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(data, headers);
      ResponseEntity<String> response =
          restTemplate.exchange(
              "https://secure.snd.payu.com/pl/standard/user/oauth/authorize",
              HttpMethod.POST,
              requestEntity,
              String.class);
      JsonNode jsonNode = objectMapper.readTree(response.getBody());
      expiresIn = Integer.parseInt(jsonNode.get("expires_in").asText());
      bearerToken =
          new BearerToken(
              jsonNode.get("access_token").asText(),
              LocalDateTime.now().plusSeconds((long) (expiresIn * procentOfAll)));
    }
    return bearerToken.getToken();
  }

  @Profile("test")
  public String testBearerTokenExpired() throws JsonProcessingException {
    bearerToken = new BearerToken("testToken", LocalDateTime.now().minusMinutes(1));
    return getToken();
  }

  @Profile("test")
  public String testBearerTokenNotExpired() throws JsonProcessingException {
    bearerToken = new BearerToken("testToken", LocalDateTime.now().plusMinutes(1));
    return getToken();
  }
}
