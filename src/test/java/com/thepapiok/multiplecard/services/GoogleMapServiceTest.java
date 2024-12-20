package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GoogleMapServiceTest {
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private static final String TEST_PHONE = "+2431324123412";
  private static final String TEST_SHOP_NAME = "shopName";
  private static final String LAT_PARAM = "lat";
  private static final String LNG_PARAM = "lng";
  private static final String PREFIX_GOOGLE_URL =
      "https://maps.googleapis.com/maps/api/geocode/json?address=";
  private GoogleMapsService googleMapsService;
  @Mock private UserRepository userRepository;
  @Mock private AccountRepository accountRepository;
  @Mock private ShopRepository shopRepository;
  @Mock private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    googleMapsService =
        new GoogleMapsService(userRepository, accountRepository, shopRepository, restTemplate);
  }

  @Test
  public void shouldReturnEmptyMapAtGetCoordsOfOriginsWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertEquals(Map.of(), googleMapsService.getCoordsOfOrigins(TEST_PHONE));
  }

  @Test
  public void shouldReturnEmptyMapAtGetCoordsOfOriginsWhenGetException() {
    Account account = new Account();
    account.setId(TEST_ID);
    Address address = new Address();
    address.setCity("testCity");
    address.setCountry("testCountry (PL)");
    address.setStreet("testStreet");
    address.setProvince("testProvince");
    address.setPostalCode("testPostalCode");
    address.setHouseNumber("testHouseNumber");
    User user = new User();
    user.setId(TEST_ID);
    user.setAddress(address);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));

    assertEquals(Map.of(), googleMapsService.getCoordsOfOrigins(TEST_PHONE));
  }

  @Test
  public void shouldReturnMapOfCoordsAtGetCoordsOfOriginsWhenEverythingOk() {
    final double testLat = 24321.214;
    final double testLng = 542.234;
    Account account = new Account();
    account.setId(TEST_ID);
    Address address = new Address();
    address.setCity("testCity");
    address.setCountry("testCountry (PL)");
    address.setStreet("testStreet");
    address.setProvince("testProvince");
    address.setPostalCode("testPostalCode");
    address.setHouseNumber("testHouseNumber");
    User user = new User();
    user.setId(TEST_ID);
    user.setAddress(address);
    String body = getBodyAtGetCoords(testLat, testLng);
    ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.OK);
    Map<String, Double> coords = new HashMap<>(2);
    coords.put(LAT_PARAM, testLat);
    coords.put(LNG_PARAM, testLng);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(restTemplate.exchange(
            PREFIX_GOOGLE_URL
                + "testStreet+testHouseNumber,+testPostalCode+testCity,+testProvince,+testCountry&key=null",
            HttpMethod.POST,
            null,
            String.class))
        .thenReturn(response);

    assertEquals(coords, googleMapsService.getCoordsOfOrigins(TEST_PHONE));
  }

  @Test
  public void shouldReturnEmptyListAtGetCoordsOfDestinationsWhenShopNotFound() {
    when(shopRepository.getShopByName(TEST_SHOP_NAME)).thenReturn(null);

    assertEquals(List.of(), googleMapsService.getCoordsOfDestinations(TEST_SHOP_NAME));
  }

  @Test
  public void shouldReturnEmptyListAtGetCoordsOfDestinationsWhenCoordsNotFound() {
    Address address1 = new Address();
    address1.setCity("testCity1");
    address1.setCountry("testCountry1 (PL)");
    address1.setStreet("testStreet1");
    address1.setProvince("testProvince1");
    address1.setPostalCode("testPostalCode1");
    address1.setHouseNumber("testHouseNumber1");
    Address address2 = new Address();
    address2.setCity("testCity2");
    address2.setCountry("testCountry2 (PL)");
    address2.setStreet("testStreet2");
    address2.setProvince("testProvince2");
    address2.setPostalCode("testPostalCode2");
    address2.setHouseNumber("testHouseNumber2");
    Shop shop = new Shop();
    shop.setName(TEST_SHOP_NAME);
    shop.setPoints(List.of(address1, address2));

    when(shopRepository.getShopByName(TEST_SHOP_NAME)).thenReturn(shop);

    assertEquals(List.of(), googleMapsService.getCoordsOfDestinations(TEST_SHOP_NAME));
  }

  @Test
  public void shouldReturnListOfCoordsAtGetCoordsOfDestinationsWhenEverythingOk() {
    final double testLat1 = 24321.214;
    final double testLng1 = 542.234;
    final double testLat2 = 12.56;
    final double testLng2 = 67.1234;
    Address address1 = new Address();
    address1.setCity("testCity1");
    address1.setCountry("testCountry1 (PL)");
    address1.setStreet("testStreet1");
    address1.setProvince("testProvince1");
    address1.setPostalCode("testPostalCode1");
    address1.setHouseNumber("testHouseNumber1");
    Address address2 = new Address();
    address2.setCity("testCity2");
    address2.setCountry("testCountry2 (PL)");
    address2.setStreet("testStreet2");
    address2.setProvince("testProvince2");
    address2.setPostalCode("testPostalCode2");
    address2.setHouseNumber("testHouseNumber2");
    Shop shop = new Shop();
    shop.setName(TEST_SHOP_NAME);
    shop.setPoints(List.of(address1, address2));
    String body1 = getBodyAtGetCoords(testLat1, testLng1);
    ResponseEntity<String> response1 = new ResponseEntity<>(body1, HttpStatus.OK);
    String body2 = getBodyAtGetCoords(testLat2, testLng2);
    ResponseEntity<String> response2 = new ResponseEntity<>(body2, HttpStatus.OK);
    List<Map<String, Double>> expectedPoints = new ArrayList<>();
    Map<String, Double> coords1 = new LinkedHashMap<>(2);
    coords1.put(LAT_PARAM, testLat1);
    coords1.put(LNG_PARAM, testLng1);
    Map<String, Double> coords2 = new LinkedHashMap<>(2);
    coords2.put(LAT_PARAM, testLat2);
    coords2.put(LNG_PARAM, testLng2);
    expectedPoints.add(coords1);
    expectedPoints.add(coords2);

    when(shopRepository.getShopByName(TEST_SHOP_NAME)).thenReturn(shop);
    when(restTemplate.exchange(
            PREFIX_GOOGLE_URL
                + "testStreet1+testHouseNumber1,+testPostalCode1+testCity1,+testProvince1,+testCountry1&key=null",
            HttpMethod.POST,
            null,
            String.class))
        .thenReturn(response1);
    when(restTemplate.exchange(
            PREFIX_GOOGLE_URL
                + "testStreet2+testHouseNumber2,+testPostalCode2+testCity2,+testProvince2,+testCountry2&key=null",
            HttpMethod.POST,
            null,
            String.class))
        .thenReturn(response2);

    assertEquals(expectedPoints, googleMapsService.getCoordsOfDestinations(TEST_SHOP_NAME));
  }

  private String getBodyAtGetCoords(Double testLat, Double testLng) {
    return """
                          {
                              "results": [
                                  {
                                      "geometry": {
                                          "location": {
                                              "lat": """
        + testLat
        + ","
        + """
                                        "lng": """
        + testLng
        + """
                                    }
                                }
                            }
                        ]
                    }
                """;
  }

  @Test
  public void shouldReturnEmptyMapAtGetNearestPlaceWhenGetException() {
    final double testLat1 = 24321.214;
    final double testLng1 = 542.234;
    final double testLat2 = 12.56;
    final double testLng2 = 67.1234;
    final double testLat3 = 1234.56;
    final double testLng3 = 85.123;
    Map<String, Double> origins = new HashMap<>(2);
    origins.put(LAT_PARAM, testLat1);
    origins.put(LNG_PARAM, testLng1);
    List<Map<String, Double>> destinations = new ArrayList<>();
    Map<String, Double> coords1 = new HashMap<>();
    coords1.put(LAT_PARAM, testLat2);
    coords1.put(LNG_PARAM, testLng2);
    Map<String, Double> coords2 = new HashMap<>();
    coords2.put(LAT_PARAM, testLat3);
    coords2.put(LNG_PARAM, testLng3);
    destinations.add(coords1);
    destinations.add(coords2);

    assertEquals(Map.of(), googleMapsService.getTheNearestPlace(origins, destinations));
  }

  @Test
  public void shouldReturnMapOfCoordsAtGetNearestPlaceWhenEverythingOk() {
    final double testLat1 = 24321.214;
    final double testLng1 = 542.234;
    final double testLat2 = 12.56;
    final double testLng2 = 67.1234;
    final double testLat3 = 1234.56;
    final double testLng3 = 85.123;
    Map<String, Double> origins = new HashMap<>(2);
    origins.put(LAT_PARAM, testLat1);
    origins.put(LNG_PARAM, testLng1);
    List<Map<String, Double>> destinations = new ArrayList<>();
    Map<String, Double> coords1 = new HashMap<>();
    coords1.put(LAT_PARAM, testLat2);
    coords1.put(LNG_PARAM, testLng2);
    Map<String, Double> coords2 = new HashMap<>();
    coords2.put(LAT_PARAM, testLat3);
    coords2.put(LNG_PARAM, testLng3);
    destinations.add(coords1);
    destinations.add(coords2);
    String body =
        """
                    {
                        "rows": [
                            {
                                "elements": [
                                    {
                                        "distance": {
                                            "value": 123
                                        }
                                    },
                                    {
                                        "distance": {
                                            "value": 2431
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                """;
    ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.OK);
    Map<String, Double> coords = new HashMap<>(2);
    coords.put(LAT_PARAM, testLat2);
    coords.put(LNG_PARAM, testLng2);

    when(restTemplate.exchange(
            "https://maps.googleapis.com/maps/api/distancematrix/json?"
                + "origins=24321.214,542.234&destinations=12.56,67.1234|1234.56,85.123&mode=driving&key=null",
            HttpMethod.POST,
            null,
            String.class))
        .thenReturn(response);

    assertEquals(coords, googleMapsService.getTheNearestPlace(origins, destinations));
  }
}
