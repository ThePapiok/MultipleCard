package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {}
