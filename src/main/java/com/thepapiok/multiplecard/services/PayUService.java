package com.thepapiok.multiplecard.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.misc.BearerToken;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  private final RestTemplate restTemplate;
  private BearerToken bearerToken;

  @Value("${PAYU_CLIENT_SECRET}")
  private String clientSecret;

  @Value("${PAYU_CLIENT_ID}")
  private String clientId;

  @Value("${PAYU_KEY_MD5}")
  private String keyMD5;

  @Autowired
  public PayUService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private BearerToken getToken() throws JsonProcessingException {
    if (bearerToken == null || LocalDateTime.now().isAfter(bearerToken.getExpiresIn())) {
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
      bearerToken =
          new BearerToken(
              jsonNode.get("access_token").asText(),
              LocalDateTime.now()
                  .plusSeconds(Integer.parseInt(jsonNode.get("expires_in").asText()))
                  .minusHours(2));
    }
    return bearerToken;
  }
}
