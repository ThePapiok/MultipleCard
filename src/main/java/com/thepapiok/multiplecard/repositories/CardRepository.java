package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Card;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardRepository extends MongoRepository<Card, ObjectId> {
  Card findCardByUserId(ObjectId id);

  boolean existsCardById(ObjectId id);

  boolean existsCardByIdAndName(ObjectId id, String name);
}
