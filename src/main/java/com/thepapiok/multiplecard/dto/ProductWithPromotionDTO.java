package com.thepapiok.multiplecard.dto;

import com.thepapiok.multiplecard.collections.Promotion;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ProductWithPromotionDTO {
  private String id;
  private String name;
  private String description;
  private String imageUrl;
  private String barcode;
  private List<ObjectId> categories;
  private int amount;
  private ObjectId shopId;
  private LocalDateTime updatedAt;
  private Promotion promotion;
}
