package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Country;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CountryRepository extends MongoRepository<Country, String> {}
