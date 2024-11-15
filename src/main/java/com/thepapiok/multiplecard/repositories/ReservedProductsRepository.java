package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.ReservedProduct;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservedProductsRepository extends MongoRepository<ReservedProduct, ObjectId> {}
