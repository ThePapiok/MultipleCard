package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Account;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountRepository extends MongoRepository<Account, String> {

  @Query(value = "{}", fields = "{'_id': 0, 'phone': 1}")
  List<Account> findAllPhones();

  @Query(value = "{}", fields = "{'_id': 0, 'email': 1}")
  List<Account> findAllEmails();

  Account findByPhone(String phone);
}
