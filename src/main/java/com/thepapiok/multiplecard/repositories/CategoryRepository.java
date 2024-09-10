package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {}
