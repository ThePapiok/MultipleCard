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
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.io.IOException;
import java.util.List;
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
      CloudinaryService cloudinaryService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.profileConverter = profileConverter;
    this.cardRepository = cardRepository;
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.cloudinaryService = cloudinaryService;
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
    final int centsPerZloty = 100;
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
                  List<Order> orders = orderRepository.findAllByProductId(product.getId());
                  for (Order order : orders) {
                    mongoTemplate.updateFirst(
                        query(where(cardIdParam).is(order.getCardId())),
                        new Update().inc("points", (order.getAmount() / centsPerZloty)),
                        User.class);
                    mongoTemplate.remove(order);
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
}
