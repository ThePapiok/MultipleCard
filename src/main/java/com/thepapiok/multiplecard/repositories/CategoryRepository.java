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
                    $project: {
                        "_id": 0,
                        "count": { $add: ["$count", ?1]}
                    }
                }
            """,
        """
                {
                    $project: {
                        "find": {
                            $cond: {
                                if: { $gte: ["$count", 20] },
                                then: true,
                                else: false
                            }
                        }
                    }
                }
            """
      })
  Boolean countByOwnerIsEqual20(ObjectId ownerId, int count);

  @Aggregation(
      pipeline = {
        """
                {
                    $project: {
                        "isSubset": {
                            $setIsSubset: [["$name"], ?0]
                        }

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
