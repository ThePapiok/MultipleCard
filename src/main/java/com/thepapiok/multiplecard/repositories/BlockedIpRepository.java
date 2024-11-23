package com.thepapiok.multiplecard.repositories;

import com.thepapiok.multiplecard.collections.BlockedIp;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedIpRepository extends MongoRepository<BlockedIp, ObjectId> {
  List<BlockedIp> getAllByAmountGreaterThanOrAttemptsEquals(int maxAmount, int maxAttempts);
}
