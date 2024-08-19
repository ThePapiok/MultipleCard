package com.thepapiok.multiplecard.collections;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shops")
@Data
public class Shop {
  @Id private String id;
  private String name;
  private Long totalAmount;
  private String imageUrl;
  private List<Address> points;
}
