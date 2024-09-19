package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Product;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, ObjectId> {
  List<Product> getAllByShopId(ObjectId objectId);
}
