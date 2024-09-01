package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Like;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeRepository extends MongoRepository<Like, String> {
  Optional<Like> findByReviewUserId(ObjectId id);

  void deleteAllByReviewUserId(ObjectId id);
}
