package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Report;
import com.thepapiok.multiplecard.dto.ReportsDTO;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, ObjectId> {
  boolean existsByUserIdAndReportedId(ObjectId userId, ObjectId reportedId);

  void deleteAllByReportedId(ObjectId reportedId);

  @Aggregation(
      pipeline = {
        """
        {
          $match: {
            "reportedId": {
              $nin: ?0
              }
          }
        }
        """,
        """
                    {
                      $group: {
                        "_id": {"_id": "$reportedId", "isProduct": "$isProduct"},
                        "maxCreatedAt": {
                          $max: "$createdAt"
                        }
                      }
                    }
                    """,
        """
                      {
                        $sort: {
                          "maxCreatedAt": -1
                        }
                      }
                    """,
        """
                {
                    $project: {
                        "maxCreatedAt": 0
                    }
                }
            """,
        """
                    {
                      $limit: 1
                    }
                    """,
        """
                    {
                      $lookup: {
                        "from": "reports",
                        "localField": "_id._id",
                        "foreignField": "reportedId",
                        "as": "reports",
                        pipeline: [
                          {
                            $lookup: {
                              "from": "users",
                              "localField": "userId",
                              "foreignField": "_id",
                              "as": "user",
                              pipeline: [
                                {
                                  $project: {
                                    "firstName": 1,
                                    "lastName": 1,
                                    "_id": 0
                                  }
                                }
                              ]
                            }
                          },
                          {
                              $unwind: {
                                "path": "$user",
                                "preserveNullAndEmptyArrays": true
                              }
                          },
                          {
                            $addFields: {
                              "firstName": "$user.firstName",
                              "lastName": "$user.lastName",
                            }
                          },
                          {
                            $project: {
                              "firstName": 1,
                              "lastName": 1,
                              "description": 1,
                              "userId": 1,
                              "createdAt": 1
                            }
                          }
                        ]
                      }
                    }
                    """,
        """
                    {
                      $addFields: {
                        "_id": "$_id._id",
                        "isProduct": "$_id.isProduct"
                      }
                    }
                    """
      })
  ReportsDTO getFirstReport(List<ObjectId> ids);
}
