package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Country;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CountryRepository extends MongoRepository<Country, String> {
    @Query(value = "{'name':  ?0}", fields = "{'_id': 1}")
    Country findByName(String name);
}
