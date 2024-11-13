package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Category;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
  @Aggregation(
      pipeline = {
        """
                            {
                                $match: {
                                    "ownerId": ?0,
                                        }
                            }
                            """,
        """
                                {
                                    $group: {
                                        "_id": "_id",
                                        "count": {
                                            $count: {}
                                        }
                                    }
                                }
                            """,
        """
                            {
                                $addFields: {
                                    "allCount": { $add: ["$count", ?1]}
                                }
                            }
                            """,
        """
                                {
                                    $project: {
                                        "_id": 0,
                                        "allCount": 1
                                    }
                                }
                            """,
        """
                                {
                                    $addFields: {
                                        "find": {
                                            $cond: {
                                                if: { $gte: ["$allCount", 20] },
                                                then: true,
                                                else: false
                                            }
                                        }
                                    }
                                }
                            """,
        """
                            {
                                $project: {
                                    "find": 1,
                                }
                            }
                            """
      })
  Boolean countByOwnerIsGTE20(ObjectId ownerId, int count);

  @Aggregation(
      pipeline = {
        """
                                {
                                    $addFields: {
                                        "isSubset": {
                                            $setIsSubset: [["$name"], ?0]
                                        }

                                    }
                                }
                            """,
        """
                            {
                                $project: {
                                    "_id": 0,
                                    "isSubset": 1
                                }
                            }
                            """,
        """
                                {
                                    $match: {
                                        "isSubset": true
                                    }
                                }
                            """,
        """
                                {
                                    $group: {
                                        "_id": "_id",
                                        "count": {
                                            $count: {}
                                        }
                                    }
                                }
                            """,
        """
                                {
                                    $project: {
                                        "_id": 0,
                                        "count": 1
                                    }
                                }
                            """
      })
  Integer countExistingCategories(List<String> categories);

  @Query(value = "{'name': ?0}", fields = "{'_id': 1}")
  Category findIdByName(String name);
}
