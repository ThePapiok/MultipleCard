package com.thepapiok.multiplecard.services;

import static org.springframework.data.mongodb.core.query.Query.query;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.misc.AddressConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShopService {
  private final AddressConverter addressConverter;
  private final AccountRepository accountRepository;
  private final ShopRepository shopRepository;
  private final RestTemplate restTemplate;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;

  @Value("${IBANAPI_API_KEY}")
  private String apiKey;

  @Autowired
  public ShopService(
      AddressConverter addressConverter,
      AccountRepository accountRepository,
      ShopRepository shopRepository,
      RestTemplate restTemplate,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate) {
    this.addressConverter = addressConverter;
    this.accountRepository = accountRepository;
    this.shopRepository = shopRepository;
    this.restTemplate = restTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
  }

  public boolean checkImage(MultipartFile file) {
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

  public boolean checkAccountNumberExists(String accountNumber, String phone) {
    return accountRepository.existsByAccountNumberOtherThanPhone(accountNumber, phone);
  }

  public boolean checkShopNameExists(String name, String phone) {
    return accountRepository.existsByNameOtherThanPhone(name, phone);
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

  public boolean checkPointsExists(List<AddressDTO> points, String phone) {
    boolean found = false;
    for (Address address : addressConverter.getEntities(points)) {
      if (accountRepository.existsByPointsOtherThanPhone(address, phone)) {
        found = true;
      }
    }
    return found;
  }

  public String saveTempFile(MultipartFile multipartFile) {
    Path path;
    try {
      path = Files.createTempFile("upload_", ".tmp");
      Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
      return path.toString();
    } catch (IOException e) {
      return null;
    }
  }

  public boolean checkFiles(List<MultipartFile> files) {
    final int maxSize = 5000000;
    int emptyFiles = 0;
    for (MultipartFile multipartFile : files) {
      if (multipartFile.isEmpty()) {
        emptyFiles++;
      } else if (!"application/pdf".equals(multipartFile.getContentType())
          || multipartFile.getSize() >= maxSize) {
        return false;
      }
    }
    return emptyFiles == 1;
  }

  public List<String> getShopNamesByPrefix(String prefix) {
    if ("".equals(prefix)) {
      return List.of();
    }
    return shopRepository.getShopNamesByPrefix("^" + prefix);
  }

  public Boolean buyProducts(Map<String, Integer> productsId, String cardId) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      final LocalDateTime date = LocalDateTime.now();
      final ObjectId objectCardId = new ObjectId(cardId);
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              productsId.forEach(
                  (productId, amount) -> {
                    int promotionCount;
                    ObjectId objectProductId = new ObjectId(productId);
                    Product product =
                        mongoTemplate.findOne(
                            query(Criteria.where("id").is(objectProductId)), Product.class);
                    if (product == null) {
                      throw new RuntimeException();
                    }
                    for (int i = 1; i <= amount; i++) {
                      Order order = new Order();
                      order.setCreatedAt(date);
                      order.setUsed(false);
                      order.setCardId(objectCardId);
                      order.setProductId(objectProductId);
                      order.setShopId(product.getShopId());
                      Promotion promotion =
                          mongoTemplate.findOne(
                              query(Criteria.where("productId").is(objectProductId)),
                              Promotion.class);
                      if (promotion != null) {
                        order.setAmount(promotion.getAmount());
                        promotionCount = promotion.getCount();
                        if (promotionCount != 0) {
                          if (promotionCount - 1 == 0) {
                            mongoTemplate.remove(promotion);
                          } else {
                            promotion.setCount(promotionCount - 1);
                            mongoTemplate.save(promotion);
                          }
                        }
                      } else {
                        order.setAmount(product.getAmount());
                      }
                      mongoTemplate.save(order);
                    }
                  });
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
