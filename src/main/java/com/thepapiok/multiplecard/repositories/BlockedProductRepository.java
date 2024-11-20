package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.BlockedProduct;
import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedProductRepository extends MongoRepository<BlockedProduct, ObjectId> {
  boolean existsByProductId(ObjectId productId);

  BlockedProduct findByProductId(ObjectId productId);

  void deleteByProductId(ObjectId productId);

  List<BlockedProduct> findAllByExpiredAtIsBefore(LocalDate date);
}
