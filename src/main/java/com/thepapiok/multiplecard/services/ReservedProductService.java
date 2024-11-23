package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.ReservedProduct;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.ReservedProductsRepository;
import java.util.Map;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ReservedProductService {
  private final PasswordEncoder passwordEncoder;
  private final ReservedProductsRepository reservedProductsRepository;
  private final AggregationRepository aggregationRepository;

  @Autowired
  public ReservedProductService(
      PasswordEncoder passwordEncoder,
      ReservedProductsRepository reservedProductsRepository,
      AggregationRepository aggregationRepository) {
    this.passwordEncoder = passwordEncoder;
    this.reservedProductsRepository = reservedProductsRepository;
    this.aggregationRepository = aggregationRepository;
  }

  public boolean reservedProducts(
      Map<ProductInfo, Integer> products, String ip, ObjectId orderId, String cardId) {
    final Map<ObjectId, Integer> reducedProducts =
        products.entrySet().stream()
            .filter(e -> e.getKey().isHasPromotion())
            .collect(Collectors.toMap(e -> e.getKey().getProductId(), Map.Entry::getValue));
    return aggregationRepository.reservedProducts(
        reducedProducts, passwordEncoder.encode(ip), orderId, new ObjectId(cardId));
  }

  public boolean checkReservedProductsIsLessThan100ByCardId(String cardId) {
    final int maxReservedProducts = 100;
    return reservedProductsRepository.countByCardId(new ObjectId(cardId)) <= maxReservedProducts;
  }

  public boolean checkReservedProductsIsLessThan100ByEncryptedIp(String ip) {
    final int maxReservedProducts = 100;
    int count = 0;
    for (ReservedProduct reservedProduct : reservedProductsRepository.findAll()) {
      if (passwordEncoder.matches(ip, reservedProduct.getEncryptedIp())) {
        count++;
      }
    }
    return count <= maxReservedProducts;
  }
}
