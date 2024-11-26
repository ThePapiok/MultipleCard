package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Promotion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PromotionRepository extends MongoRepository<Promotion, ObjectId> {
  Promotion findByProductId(ObjectId productId);

  void deleteByProductId(ObjectId productId);

  boolean existsByProductIdAndQuantityIsNotNull(ObjectId productId);

  @Query(value = "{'_id': ?0}", fields = "{'_id': 0, 'quantity': 1}")
  Promotion findQuantityById(ObjectId promotionId);

  @Query(value = "{'productId': ?0}", fields = "{'_id': 0, 'newPrice': 1}")
  Promotion findNewPriceByProductId(ObjectId productId);

  @Query(value = "{'productId': ?0}", fields = "{'_id': 1}")
  Promotion findIdByProductId(ObjectId productId);
}
