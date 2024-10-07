package com.thepapiok.multiplecard.collections;

import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shops")
@Data
public class Shop {
  @Id private ObjectId id;
  private String firstName;
  private String lastName;
  private String name;
  private Long totalAmount;
  private String imageUrl;
  private String accountNumber;
  private List<Address> points;
}
