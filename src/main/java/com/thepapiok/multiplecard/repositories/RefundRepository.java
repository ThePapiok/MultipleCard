package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Refund;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefundRepository extends MongoRepository<Refund, ObjectId> {}
