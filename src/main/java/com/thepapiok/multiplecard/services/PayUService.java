package com.thepapiok.multiplecard.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.misc.BearerToken;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class PayUService {
  private static BearerToken bearerToken;
  private final RestTemplate restTemplate;
  private final ProductRepository productRepository;
  private final PromotionRepository promotionRepository;

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
      PromotionRepository promotionRepository) {
    this.restTemplate = restTemplate;
    this.productRepository = productRepository;
    this.promotionRepository = promotionRepository;
  }

  public Pair<Boolean, String> productsOrder(
      Map<ProductInfo, Integer> productsId, String cardId, String ip, String orderId) {
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
      data.put("currencyCode", "PLN");
      List<Map<String, Object>> products = new ArrayList<>();
      for (Map.Entry<ProductInfo, Integer> entry : productsId.entrySet()) {
        productId = entry.getKey().getProductId();
        quantity = entry.getValue();
        Map<String, Object> product = new HashMap<>();
        if (entry.getKey().isHasPromotion()) {
          price = promotionRepository.findNewPriceByProductId(productId).getNewPrice();
        } else {
          price = productRepository.findPriceById(productId).getPrice();
        }
        product.put("name", objectMapper.writeValueAsString(entry.getKey()));
        product.put("unitPrice", price);
        product.put("quantity", quantity);
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
      return Pair.of(true, objectMapper.readTree(response.getBody()).get("redirectUri").asText());
    } catch (Exception e) {
      return Pair.of(false, "error");
    }
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
