package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Blocked;
import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedRepository extends MongoRepository<Blocked, ObjectId> {
  boolean existsByProductId(ObjectId productId);

  Blocked findByProductId(ObjectId productId);

  void deleteByProductId(ObjectId productId);

  List<Blocked> findAllByExpiredAtIsBefore(LocalDate date);
}
