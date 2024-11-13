package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ShopRepository extends MongoRepository<Shop, ObjectId> {
  @Query(value = "{'_id': ?0}", fields = "{'_id': 0, 'imageUrl': 1, 'name': 1}")
  Shop findImageUrlAndNameById(ObjectId shopId);
}
