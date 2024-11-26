package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.repositories.OrderRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final OrderRepository orderRepository;

  @Autowired
  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public boolean checkExistsAlreadyOrder(String orderId) {
    return orderRepository.existsByOrderId(new ObjectId(orderId));
  }
}
