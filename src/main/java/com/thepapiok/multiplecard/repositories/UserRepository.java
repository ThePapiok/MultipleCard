package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {}
