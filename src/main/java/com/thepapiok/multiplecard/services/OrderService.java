package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;
  private final AccountRepository accountRepository;

  @Autowired
  public OrderService(
      OrderRepository orderRepository,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate,
      AccountRepository accountRepository) {
    this.orderRepository = orderRepository;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
    this.accountRepository = accountRepository;
  }

  public boolean checkExistsAlreadyOrder(String orderId) {
    return orderRepository.existsByOrderId(new ObjectId(orderId));
  }

  public boolean makeOrdersUsed(List<String> ids, ObjectId cardId, String phone) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            final ObjectId shopId = accountRepository.findIdByPhone(phone).getId();

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              for (ObjectId id : ids.stream().map(ObjectId::new).toList()) {
                Order order = orderRepository.findByIdAndCardIdAndShopId(id, cardId, shopId);
                if (order == null) {
                  throw new RuntimeException();
                }
                order.setUsed(true);
                mongoTemplate.save(order);
              }
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
