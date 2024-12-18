package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShopControllerTest {

  private static final String TEST_CARD_ID = "123456789012345678901234";
  private static final String TEST_ORDER_ID = "123454289012345678901231";
  private static final String TEST_PAYU_ORDER_ID = "1ewrqerqwerqwer12131";
  private static final String TEST_PRODUCT_ID = "123456789012345678901231";
  private static final String TEST_EMAIL = "multiplecard@gmail.com";
  private static final String MAKE_ORDER_URL = "/make_order";
  private static final String CARD_ID_PARAM = "cardId";
  private static final int STATUS_BAD_REQUEST = 400;
  private static final int STATUS_OK = 200;
  private static final String STATUS_PAYU_COMPLETED = "COMPLETED";
  private static final String TEST_SIGNATURE = "signature=sadfsda12312";
  private static final String QUOTATION_MARK_WITH_COMMA = "\",";
  private static final String TEST_PHONE = "12341234123412341";

  private Map<ProductInfo, Integer> productsInfo;

  @Autowired private MockMvc mockMvc;
  @MockBean private ShopService shopService;
  @MockBean private ProductService productService;
  @MockBean private ReservedProductService reservedProductService;
  @MockBean private PayUService payUService;
  @MockBean private BlockedIpService blockedIpService;
  @MockBean private RefundService refundService;
  @MockBean private OrderService orderService;
  @MockBean private EmailService emailService;

  @Test
  public void shouldReturnListOfShopNamesAtGetShopNamesWhenEverythingOk() throws Exception {
    final String prefix = "shop";
    List<String> expectedShopNames = List.of("shop1", "shop2");

    when(shopService.getShopNamesByPrefix(prefix)).thenReturn(expectedShopNames);

    MvcResult mvcResult =
        mockMvc
            .perform(post("/get_shop_names").param("prefix", prefix))
            .andExpect(status().isOk())
            .andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> shop =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(), new TypeReference<List<String>>() {});
    assertEquals(expectedShopNames, shop);
  }

  @Test
  public void shouldReturnStatusUnauthorizedAtBuyProductsWhenNoPayuSender() throws Exception {
    final int unauthorizedStatus = 401;

    when(payUService.checkNotification("{bad}", "header")).thenReturn(false);

    performPostBuyProducts(unauthorizedStatus, "{bad}", "header");
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsRefund() throws Exception {
    final String body =
        """
            {
              "orderId": \""""
            + TEST_PAYU_ORDER_ID
            + QUOTATION_MARK_WITH_COMMA
            + """
              "refund": {
                "status": "FINALIZED"
              }
            }

            """;
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
    verify(refundService).updateRefund(TEST_PAYU_ORDER_ID);
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusPending() throws Exception {
    final String body = setBodyForBuyProducts("PENDING");

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCanceled() throws Exception {
    final String body = setBodyForBuyProducts("CANCELED");

    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
    verify(reservedProductService).deleteAndUpdateBlockedIps(TEST_ORDER_ID, "127.0.0.1");
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedButOrderAlreadyExists()
      throws Exception {
    final String body = setBodyForBuyProducts(STATUS_PAYU_COMPLETED);

    when(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID)).thenReturn(true);
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
  }

  @Test
  public void shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedButRefundAlreadyExists()
      throws Exception {
    final String body = setBodyForBuyProducts(STATUS_PAYU_COMPLETED);

    when(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID)).thenReturn(false);
    when(refundService.checkExistsAlreadyRefund(TEST_PAYU_ORDER_ID)).thenReturn(true);
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
  }

  @Test
  public void
      shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedAndSuccessWithBuyProducts()
          throws Exception {
    final String body = setBodyForBuyProducts(STATUS_PAYU_COMPLETED);
    List<ProductPayU> products = setProductsPayuAtBuyProducts();

    when(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID)).thenReturn(false);
    when(refundService.checkExistsAlreadyRefund(TEST_PAYU_ORDER_ID)).thenReturn(false);
    when(productService.buyProducts(products, TEST_CARD_ID, TEST_ORDER_ID)).thenReturn(true);
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
    verify(productService).buyProducts(products, TEST_CARD_ID, TEST_ORDER_ID);
  }

  @Test
  public void
      shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedAndErrorWithBuyProductsAndSuccessWithRefund()
          throws Exception {
    final String body = setBodyForBuyProducts(STATUS_PAYU_COMPLETED);
    List<ProductPayU> products = setProductsPayuAtBuyProducts();

    when(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID)).thenReturn(false);
    when(refundService.checkExistsAlreadyRefund(TEST_PAYU_ORDER_ID)).thenReturn(false);
    when(productService.buyProducts(products, TEST_CARD_ID, TEST_ORDER_ID)).thenReturn(false);
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);
    when(payUService.makeRefund(TEST_PAYU_ORDER_ID)).thenReturn(true);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
    verify(productService).buyProducts(products, TEST_CARD_ID, TEST_ORDER_ID);
    verify(reservedProductService).deleteAllByOrderId(TEST_ORDER_ID);
    verify(refundService).createRefund(TEST_PAYU_ORDER_ID, "pl", TEST_EMAIL);
  }

  @Test
  public void
      shouldReturnStatusOkAtBuyProductsWhenIsOrderAndStatusCompletedAndErrorWithBuyProductsAndErrorWithRefund()
          throws Exception {
    final String body = setBodyForBuyProducts(STATUS_PAYU_COMPLETED);
    List<ProductPayU> products = setProductsPayuAtBuyProducts();

    when(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID)).thenReturn(false);
    when(refundService.checkExistsAlreadyRefund(TEST_PAYU_ORDER_ID)).thenReturn(false);
    when(productService.buyProducts(products, TEST_CARD_ID, TEST_ORDER_ID)).thenReturn(false);
    when(payUService.checkNotification(body, TEST_SIGNATURE)).thenReturn(true);
    when(payUService.makeRefund(TEST_PAYU_ORDER_ID)).thenReturn(false);

    performPostBuyProducts(STATUS_OK, body, TEST_SIGNATURE);
    verify(productService).buyProducts(products, TEST_CARD_ID, TEST_ORDER_ID);
    verify(reservedProductService).deleteAllByOrderId(TEST_ORDER_ID);
    verify(refundService).createRefund(TEST_PAYU_ORDER_ID, "pl", TEST_EMAIL);
    verify(emailService).sendEmail(body, TEST_EMAIL, "Błąd zwrotu - " + TEST_PAYU_ORDER_ID);
  }

  private String setBodyForBuyProducts(String status) {
    return new StringBuilder(
            """
                {
                  "order": {
                    "status": \""""
                + status
                + QUOTATION_MARK_WITH_COMMA
                + """
                    "orderId": \"""")
        .append(TEST_PAYU_ORDER_ID)
        .append(QUOTATION_MARK_WITH_COMMA)
        .append("\"extOrderId\": \"")
        .append(TEST_ORDER_ID)
        .append(QUOTATION_MARK_WITH_COMMA)
        .append(
            """
                            "description": \"""")
        .append(TEST_CARD_ID)
        .append(QUOTATION_MARK_WITH_COMMA)
        .append(
            """
                            "additionalDescription": "pl",
                            "customerIp": "127.0.0.1",
                            "buyer": {
                              "email": "multiplecard@gmail.com"
                            },
                            "products": [{
                              "name": \"""")
        .append("{\\\"productId\\\": \\\"")
        .append(TEST_PRODUCT_ID)
        .append("\\\", \\\"hasPromotion\\\": true}\",")
        .append(
            """
                                "unitPrice" : "500",
                                "quantity": 2
                                }]
                          }
                        }
                        """)
        .toString();
  }

  private List<ProductPayU> setProductsPayuAtBuyProducts() {
    final int testProductPrice = 500;
    final int testProductQuantity = 2;
    List<ProductPayU> products = new ArrayList<>();
    ProductPayU productPayU = new ProductPayU();
    productPayU.setName("{\"productId\": \"" + TEST_PRODUCT_ID + "\", \"hasPromotion\": true}");
    productPayU.setUnitPrice(testProductPrice);
    productPayU.setQuantity(testProductQuantity);
    products.add(productPayU);
    return products;
  }

  private void performPostBuyProducts(int status, String body, String valuesHeader)
      throws Exception {
    mockMvc
        .perform(
            post("/buy_products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("OpenPayu-Signature", valuesHeader))
        .andExpect(status().is(status));
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenBadSizeOfMapOfProductsInfo()
      throws Exception {
    when(productService.getProductsInfo(Map.of())).thenReturn(Map.of());

    MvcResult result =
        mockMvc
            .perform(
                post(MAKE_ORDER_URL)
                    .param(CARD_ID_PARAM, TEST_CARD_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertEquals("Nieprawidłowe produkty", result.getResponse().getContentAsString());
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenBlockedIp() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setProductsInfoForMakeOrder();

    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(false);

    performPostAtMakeOrder(
        "Zbyt dużo anulowanych transakcji, spróbuj jutro", STATUS_BAD_REQUEST, httpSession);
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenBadSizeOfProducts()
      throws Exception {
    final String productInfoJSON =
        """
                {
                  "productId": \""""
            + TEST_PRODUCT_ID
            + QUOTATION_MARK_WITH_COMMA
            + """
                        "hasPromotion": true
                      }
                """;
    final ProductInfo productInfo = new ProductInfo(TEST_PRODUCT_ID, true);
    final Map<String, Integer> productsJSON = Map.of(productInfoJSON, 11);
    final Map<ProductInfo, Integer> products = Map.of(productInfo, 11);
    ObjectMapper objectMapper = new ObjectMapper();

    when(productService.getProductsInfo(productsJSON)).thenReturn(products);
    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(true);
    when(productService.checkProductsQuantity(products)).thenReturn(false);

    MvcResult result =
        mockMvc
            .perform(
                post(MAKE_ORDER_URL)
                    .param(CARD_ID_PARAM, TEST_CARD_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productsJSON)))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertEquals("Nieprawidłowe produkty", result.getResponse().getContentAsString());
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenTooManyReservedProductsByCard()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setProductsInfoForMakeOrder();

    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(true);
    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(false);

    performPostAtMakeOrder("Zbyt dużo zarezerwowanych produktów", STATUS_BAD_REQUEST, httpSession);
  }

  @Test
  public void
      shouldReturnResponseWithErrorMessageAtMakeOrderWhenTooManyReservedProductsByIpAddress()
          throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setProductsInfoForMakeOrder();

    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(true);
    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(false);

    performPostAtMakeOrder("Zbyt dużo zarezerwowanych produktów", STATUS_BAD_REQUEST, httpSession);
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenErrorMakeOrderOfProducts()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setProductsInfoForMakeOrder();

    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(true);
    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(true);
    when(payUService.productsOrder(
            eq(productsInfo), eq(TEST_CARD_ID), anyString(), anyString(), any(Locale.class)))
        .thenReturn(Pair.of(false, "error"));

    performPostAtMakeOrder("Nieoczekiwany błąd", STATUS_BAD_REQUEST, httpSession);
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenErrorAtReservedProducts()
      throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setProductsInfoForMakeOrder();

    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(true);
    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(true);
    when(payUService.productsOrder(
            eq(productsInfo), eq(TEST_CARD_ID), anyString(), anyString(), any(Locale.class)))
        .thenReturn(Pair.of(true, "pay.com"));
    when(reservedProductService.reservedProducts(
            eq(productsInfo), anyString(), any(ObjectId.class), eq(TEST_CARD_ID)))
        .thenReturn(false);

    performPostAtMakeOrder("Błąd podczas rezerwacji produktów", STATUS_BAD_REQUEST, httpSession);
  }

  @Test
  public void shouldReturnResponseOkWithPaymentUrlAtMakeOrderWhenEverythingOk() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    setProductsInfoForMakeOrder();

    when(blockedIpService.checkIpIsNotBlocked(anyString())).thenReturn(true);
    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(true);
    when(payUService.productsOrder(
            eq(productsInfo), eq(TEST_CARD_ID), anyString(), anyString(), any(Locale.class)))
        .thenReturn(Pair.of(true, "payu.com"));
    when(reservedProductService.reservedProducts(
            eq(productsInfo), anyString(), any(ObjectId.class), eq(TEST_CARD_ID)))
        .thenReturn(true);

    performPostAtMakeOrder("payu.com", STATUS_OK, httpSession);
    assertEquals("Pomyślnie zakupiono produkty", httpSession.getAttribute("successMessage"));
  }

  private void setProductsInfoForMakeOrder() {
    final ProductInfo productInfo = new ProductInfo(TEST_PRODUCT_ID, true);
    productsInfo = Map.of(productInfo, 1);
  }

  private void performPostAtMakeOrder(String message, int status, MockHttpSession httpSession)
      throws Exception {
    final String productInfoJSON =
        """
                {
                  "productId": \""""
            + TEST_PRODUCT_ID
            + QUOTATION_MARK_WITH_COMMA
            + """
                        "hasPromotion": true
                      }
                """;
    final Map<String, Integer> productsJSON = Map.of(productInfoJSON, 1);
    ObjectMapper objectMapper = new ObjectMapper();

    when(productService.getProductsInfo(productsJSON)).thenReturn(productsInfo);

    MvcResult result =
        mockMvc
            .perform(
                post(MAKE_ORDER_URL)
                    .param(CARD_ID_PARAM, TEST_CARD_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productsJSON))
                    .session(httpSession))
            .andExpect(status().is(status))
            .andReturn();
    assertEquals(message, result.getResponse().getContentAsString());
  }
}
