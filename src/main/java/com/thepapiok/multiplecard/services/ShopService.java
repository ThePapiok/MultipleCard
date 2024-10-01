package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.misc.AddressConverter;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShopService {

  private final ShopRepository shopRepository;
  private final AddressConverter addressConverter;
  private final RestTemplate restTemplate;

  @Value("${IBANAPI_API_KEY}")
  private String apiKey;

  @Autowired
  public ShopService(
      ShopRepository shopRepository, AddressConverter addressConverter, RestTemplate restTemplate) {
    this.shopRepository = shopRepository;
    this.addressConverter = addressConverter;
    this.restTemplate = restTemplate;
  }

  public boolean checkFile(MultipartFile file) {
    final int maxSize = 2000000;
    final int minWidth = 450;
    final int minHeight = 450;
    BufferedImage image;
    if (file.isEmpty() || !file.getContentType().startsWith("image") || file.getSize() >= maxSize) {
      return false;
    }
    try {
      image = ImageIO.read(file.getInputStream());
    } catch (IOException e) {
      return false;
    }
    return image.getWidth() > minWidth && image.getHeight() > minHeight;
  }

  public boolean checkAccountNumberExists(String accountNumber) {
    return shopRepository.existsByAccountNumber(accountNumber);
  }

  public boolean checkShopNameExists(String name) {
    return shopRepository.existsByName(name);
  }

  public boolean checkAccountNumber(String accountNumber) {
    String url =
        "https://api.ibanapi.com/v1/validate-basic/PL" + accountNumber + "?api_key=" + apiKey;
    try {
      restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    } catch (HttpClientErrorException e) {
      return false;
    }
    return true;
  }

  public boolean checkPointsExists(List<AddressDTO> points) {
    boolean found = false;
    for (Address address : addressConverter.getEntities(points)) {
      if (shopRepository.existsByPoint(address)) {
        found = true;
      }
    }
    return found;
  }
}
