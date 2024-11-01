package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Blocked;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedRepository extends MongoRepository<Blocked, ObjectId> {}
