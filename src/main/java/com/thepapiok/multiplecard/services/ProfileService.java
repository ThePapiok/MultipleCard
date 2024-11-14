package com.thepapiok.multiplecard.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
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
import org.springframework.data.mongodb.core.query.Update;
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
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final MongoTemplate mongoTemplate;
  private final MongoTransactionManager mongoTransactionManager;
  private final CloudinaryService cloudinaryService;
  private final ShopRepository shopRepository;
  private final EmailService emailService;

  @Autowired
  public ProfileService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      ProfileConverter profileConverter,
      CardRepository cardRepository,
      OrderRepository orderRepository,
      ProductRepository productRepository,
      MongoTemplate mongoTemplate,
      MongoTransactionManager mongoTransactionManager,
      CloudinaryService cloudinaryService,
      ShopRepository shopRepository,
      EmailService emailService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.profileConverter = profileConverter;
    this.cardRepository = cardRepository;
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.cloudinaryService = cloudinaryService;
    this.shopRepository = shopRepository;
    this.emailService = emailService;
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
    final String productIdParam = "productId";
    final float centsPerZloty = 100;
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              ObjectId productId;
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
                  productId = product.getId();
                  mongoTemplate.remove(query(where(productIdParam).is(productId)), Promotion.class);
                  mongoTemplate.remove(query(where(productIdParam).is(productId)), Blocked.class);
                  List<Order> orders =
                      orderRepository.findAllByProductIdAndIsUsed(productId, false);
                  for (Order order : orders) {
                    mongoTemplate.updateFirst(
                        query(where(cardIdParam).is(order.getCardId())),
                        new Update().inc("points", (Math.round(order.getAmount() / centsPerZloty))),
                        User.class);
                    order.setUsed(true);
                    mongoTemplate.save(order);
                  }
                  try {
                    cloudinaryService.deleteImage(product.getId().toString());
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                  mongoTemplate.remove(product);
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
                emailService.sendEmailWithAttachment(shop, account, locale, fileList);
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
}
