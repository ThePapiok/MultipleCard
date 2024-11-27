package com.thepapiok.multiplecard.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.misc.ProductPayU;
import com.thepapiok.multiplecard.services.BlockedIpService;
import com.thepapiok.multiplecard.services.EmailService;
import com.thepapiok.multiplecard.services.OrderService;
import com.thepapiok.multiplecard.services.PayUService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.RefundService;
import com.thepapiok.multiplecard.services.ReservedProductService;
import com.thepapiok.multiplecard.services.ShopService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopController {
  private final ShopService shopService;
  private final ProductService productService;
  private final ReservedProductService reservedProductService;
  private final BlockedIpService blockedIpService;
  private final PayUService payUService;
  private final OrderService orderService;
  private final MessageSource messageSource;
  private final EmailService emailService;
  private final RefundService refundService;

  @Autowired
  public ShopController(
      ShopService shopService,
      ProductService productService,
      ReservedProductService reservedProductService,
      BlockedIpService blockedIpService,
      PayUService payUService,
      OrderService orderService,
      MessageSource messageSource,
      EmailService emailService,
      RefundService refundService) {
    this.shopService = shopService;
    this.productService = productService;
    this.reservedProductService = reservedProductService;
    this.blockedIpService = blockedIpService;
    this.payUService = payUService;
    this.orderService = orderService;
    this.messageSource = messageSource;
    this.emailService = emailService;
    this.refundService = refundService;
  }

  @PostMapping("/get_shop_names")
  public ResponseEntity<List<String>> getShopNames(@RequestParam String prefix) {
    return new ResponseEntity<>(shopService.getShopNamesByPrefix(prefix), HttpStatus.OK);
  }

  @PostMapping("/buy_products")
  @ResponseBody
  public ResponseEntity<String> buyProducts(
      @RequestBody String requestBody, @RequestHeader("OpenPayu-Signature") String header)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    if (!payUService.checkNotification(requestBody, header)) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    JsonNode jsonNode = objectMapper.readTree(requestBody);
    boolean isOrder = jsonNode.has("order");
    if (isOrder) {
      jsonNode = jsonNode.get("order");
      String status = jsonNode.get("status").asText();
      if (!"PENDING".equals(status)) {
        String orderId = jsonNode.get("extOrderId").asText();
        String payuOrderId = jsonNode.get("orderId").asText();
        if ("CANCELED".equals(status)) {
          reservedProductService.deleteAndUpdateBlockedIps(
              orderId, jsonNode.get("customerIp").asText());
        } else if (!orderService.checkExistsAlreadyOrder(orderId)
            && !refundService.checkExistsAlreadyRefund(payuOrderId)) {
          String cardId = jsonNode.get("description").asText();
          List<ProductPayU> products =
              objectMapper.readValue(
                  jsonNode.get("products").toString(), new TypeReference<List<ProductPayU>>() {});
          if (!productService.buyProducts(products, cardId, orderId)) {
            refundService.createRefund(
                payuOrderId,
                jsonNode.get("additionalDescription").asText(),
                jsonNode.get("buyer").get("email").asText());
            reservedProductService.deleteAllByOrderId(orderId);
            if (!payUService.makeRefund(payuOrderId)) {
              emailService.sendEmail(
                  requestBody, "multiplecard@gmail.com", "Błąd zwrotu - " + payuOrderId);
            }
          }
        }
      }
    } else {
      String payuOrderId = jsonNode.get("orderId").asText();
      String status = jsonNode.get("refund").get("status").asText();
      if ("FINALIZED".equals(status)) {
        refundService.updateRefund(payuOrderId);
      }
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/make_order")
  public ResponseEntity<String> makeOrder(
      @RequestBody Map<String, Integer> productsId,
      @RequestParam String cardId,
      Locale locale,
      HttpServletRequest httpServletRequest,
      HttpSession httpSession) {
    final ObjectId orderId = new ObjectId();
    final String ip = httpServletRequest.getRemoteAddr();
    Map<ProductInfo, Integer> productsInfo = productService.getProductsInfo(productsId);
    if (productsInfo.size() == 0) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.bad_products", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!blockedIpService.checkIpIsNotBlocked(ip)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.ip_blocked", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!productService.checkProductsQuantity(productsInfo)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.bad_products", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!reservedProductService.checkReservedProductsIsLessThan100ByCardId(cardId)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.reserved_products_too_many", null, locale),
          HttpStatus.BAD_REQUEST);
    } else if (!reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(ip)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.reserved_products_too_many", null, locale),
          HttpStatus.BAD_REQUEST);
    }
    Pair<Boolean, String> response =
        payUService.productsOrder(productsInfo, cardId, ip, orderId.toHexString(), locale);
    if (!response.getFirst()) {
      return new ResponseEntity<>(
          messageSource.getMessage("error.unexpected", null, locale), HttpStatus.BAD_REQUEST);
    } else if (!reservedProductService.reservedProducts(productsInfo, ip, orderId, cardId)) {
      return new ResponseEntity<>(
          messageSource.getMessage("makeOrder.error.reserved_products_already", null, locale),
          HttpStatus.BAD_REQUEST);
    }
    httpSession.setAttribute(
        "successMessage", messageSource.getMessage("makeOrder.success.buy_products", null, locale));
    return new ResponseEntity<>(response.getSecond(), HttpStatus.OK);
  }
}
