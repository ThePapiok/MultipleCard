package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Order;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, ObjectId> {
  List<Order> findAllByProductId(ObjectId id);
}
