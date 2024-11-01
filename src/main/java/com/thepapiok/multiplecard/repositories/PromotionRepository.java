package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Promotion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromotionRepository extends MongoRepository<Promotion, ObjectId> {

  Promotion findByProductId(ObjectId productId);

  void deleteByProductId(ObjectId productId);
}
