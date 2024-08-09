package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopRepository extends MongoRepository<Shop, String> {}
