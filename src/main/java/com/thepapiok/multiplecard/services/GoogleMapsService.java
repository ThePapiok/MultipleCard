package com.thepapiok.multiplecard.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleMapsService {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final ShopRepository shopRepository;
  private final RestTemplate restTemplate;

  @Value("${GOOGLE_API_KEY}")
  private String googleApiKey;

  @Autowired
  public GoogleMapsService(
      UserRepository userRepository,
      AccountRepository accountRepository,
      ShopRepository shopRepository,
      RestTemplate restTemplate) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.shopRepository = shopRepository;
    this.restTemplate = restTemplate;
  }

  public Map<String, Double> getCoordsOfOrigins(String phone) {
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isEmpty()) {
      return Map.of();
    }
    Address address = optionalUser.get().getAddress();
    return getCoordsByAddress(address);
  }

  public List<Map<String, Double>> getCoordsOfDestinations(String shopName) {
    Shop shop = shopRepository.getShopByName(shopName);
    if (shop == null) {
      return List.of();
    }
    List<Map<String, Double>> coordsPoints = new ArrayList<>();
    for (Address address : shop.getPoints()) {
      Map<String, Double> coords = getCoordsByAddress(address);
      if (coords.size() == 0) {
        return List.of();
      }
      coordsPoints.add(coords);
    }
    return coordsPoints;
  }

  private Map<String, Double> getCoordsByAddress(Address address) {
    try {
      final String nextItem = ",+";
      ObjectMapper objectMapper = new ObjectMapper();
      StringBuilder urlBuilder =
          new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json?address=");
      String url;
      String country = address.getCountry();
      int index = country.indexOf("(") - 1;
      country = country.substring(0, index);
      urlBuilder.append(address.getStreet()).append('+').append(address.getHouseNumber());
      urlBuilder
          .append(nextItem)
          .append(address.getPostalCode())
          .append('+')
          .append(address.getCity())
          .append(nextItem)
          .append(address.getProvince())
          .append(nextItem)
          .append(country);
      urlBuilder.append("&key=").append(googleApiKey);
      url = urlBuilder.toString().replace(" ", "+");
      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.POST, null, String.class);
      String coords =
          String.valueOf(
              objectMapper
                  .readTree(response.getBody())
                  .get("results")
                  .get(0)
                  .get("geometry")
                  .get("location"));
      return objectMapper.readValue(coords, new TypeReference<Map<String, Double>>() {});
    } catch (Exception e) {
      return Map.of();
    }
  }

  public Map<String, Double> getTheNearestPlace(
      Map<String, Double> origins, List<Map<String, Double>> destinations) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      boolean isFirst = true;
      long minDistance;
      long distance;
      int index;
      StringBuilder urlBuilder =
          new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
      urlBuilder.append(origins.get("lat")).append(",").append(origins.get("lng"));
      urlBuilder.append("&destinations=");
      for (Map<String, Double> coords : destinations) {
        if (isFirst) {
          isFirst = false;
        } else {
          urlBuilder.append("|");
        }
        urlBuilder.append(coords.get("lat")).append(",").append(coords.get("lng"));
      }
      urlBuilder.append("&mode=driving&key=").append(googleApiKey);
      ResponseEntity<String> response =
          restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, null, String.class);
      JsonNode elements =
          objectMapper.readTree(response.getBody()).get("rows").get(0).get("elements");
      minDistance = elements.get(0).get("distance").get("value").asLong();
      index = 0;
      for (int i = 1; i < elements.size(); i++) {
        distance = elements.get(i).get("distance").get("value").asLong();
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
      return destinations.get(index);
    } catch (Exception e) {
      return Map.of();
    }
  }
}
