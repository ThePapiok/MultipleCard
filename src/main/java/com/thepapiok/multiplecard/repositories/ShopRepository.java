package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Shop;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopRepository extends MongoRepository<Shop, ObjectId> {}
