package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String> {}
