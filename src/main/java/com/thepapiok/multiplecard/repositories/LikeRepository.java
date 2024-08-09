package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeRepository extends MongoRepository<Like, String> {}
