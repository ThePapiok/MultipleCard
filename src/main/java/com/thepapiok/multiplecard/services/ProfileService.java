package com.thepapiok.multiplecard.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.misc.ProductPayU;
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileService {
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final ProfileConverter profileConverter;
  private final CardRepository cardRepository;
  private final ProductRepository productRepository;
  private final MongoTemplate mongoTemplate;
  private final MongoTransactionManager mongoTransactionManager;
  private final CloudinaryService cloudinaryService;
  private final ShopRepository shopRepository;
  private final EmailService emailService;
  private final ProductService productService;

  @Autowired
  public ProfileService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      ProfileConverter profileConverter,
      CardRepository cardRepository,
      ProductRepository productRepository,
      MongoTemplate mongoTemplate,
      MongoTransactionManager mongoTransactionManager,
      CloudinaryService cloudinaryService,
      ShopRepository shopRepository,
      EmailService emailService,
      ProductService productService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.profileConverter = profileConverter;
    this.cardRepository = cardRepository;
    this.productRepository = productRepository;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.cloudinaryService = cloudinaryService;
    this.shopRepository = shopRepository;
    this.emailService = emailService;
    this.productService = productService;
  }

  public ProfileDTO getProfile(String phone) {
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isPresent()) {
      return profileConverter.getDTO(optionalUser.get());
    } else {
      return null;
    }
  }

  public boolean editProfile(ProfileDTO profileDTO, String phone) {
    try {
      User user = profileConverter.getEntity(profileDTO, phone);
      userRepository.save(user);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean deleteAccount(String phone) {
    final String idParam = "_id";
    final String cardIdParam = "cardId";
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              Account account = accountRepository.findByPhone(phone);
              ObjectId id = account.getId();
              Role role = account.getRole();
              mongoTemplate.remove(account);
              if (role.equals(Role.ROLE_SHOP)) {
                try {
                  cloudinaryService.deleteImage(id.toString());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                mongoTemplate.remove(query(where(idParam).is(id)), Shop.class);
                List<Product> products = productRepository.getAllByShopId(id);
                for (Product product : products) {
                  if (!productService.deleteProduct(product.getId().toString())) {
                    throw new RuntimeException();
                  }
                }
              } else {
                if (!role.equals(Role.ROLE_ADMIN)) {
                  Card card = cardRepository.findCardByUserId(id);
                  mongoTemplate.remove(query(where("reviewUserId").is(id)), Like.class);
                  mongoTemplate.remove(query(where("userId").is(id)), Like.class);
                  if (card != null) {
                    try {
                      cloudinaryService.deleteImage(card.getId().toString());
                    } catch (IOException e) {
                      throw new RuntimeException(e);
                    }
                    mongoTemplate.remove(query(where(cardIdParam).is(card.getId())), Order.class);
                    mongoTemplate.remove(card);
                  }
                }
                mongoTemplate.remove(query(where(idParam).is(id)), User.class);
              }
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean checkRole(String phone, Role role) {
    Boolean isFound = accountRepository.hasRole(phone, role);
    return isFound != null && isFound;
  }

  public ProfileShopDTO getShop(String phone) {
    Optional<Shop> optionalShop =
        shopRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalShop.isPresent()) {
      return profileConverter.getDTO(optionalShop.get());
    } else {
      return null;
    }
  }

  public boolean editProfileShop(
      ProfileShopDTO profileShopDTO,
      String filePath,
      Locale locale,
      List<MultipartFile> fileList,
      String phone) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              Shop shop = profileConverter.getEntity(profileShopDTO, phone);
              if (filePath != null) {
                try {
                  Path path = Path.of(filePath);
                  shop.setImageUrl(
                      cloudinaryService.addImage(
                          Files.readAllBytes(path), shop.getId().toHexString()));
                  Files.deleteIfExists(path);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
              mongoTemplate.save(shop);
              Account account = accountRepository.findByPhone(phone);
              account.setActive(false);
              mongoTemplate.save(account);
              try {
                emailService.sendVerification(shop, account, locale, fileList);
              } catch (MessagingException e) {
                throw new RuntimeException(e);
              }
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public int getPoints(String phone) {
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isEmpty()) {
      return 0;
    }
    return optionalUser.get().getPoints();
  }

  public int calculatePoints(List<ProductPayU> productPayUS) {
    final float centsPerZl = 100.0F;
    int price = 0;
    for (ProductPayU productPayU : productPayUS) {
      price += (productPayU.getUnitPrice() * productPayU.getQuantity());
    }
    return (int) Math.ceil(price / centsPerZl);
  }
}
