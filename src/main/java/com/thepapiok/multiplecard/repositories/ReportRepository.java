package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.Report;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, ObjectId> {
  boolean existsByUserIdAndReportedId(ObjectId userId, ObjectId reportedId);

  void deleteAllByReportedId(ObjectId reportedId);
}
