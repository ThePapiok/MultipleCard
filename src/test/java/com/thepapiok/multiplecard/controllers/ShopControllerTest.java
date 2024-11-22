package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.services.PayUService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ReservedProductService;
import com.thepapiok.multiplecard.services.ShopService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ShopControllerTest {
  private static final String TEST_CARD_ID = "123456789012345678901234";
  private static final String TEST_PRODUCT_ID = "123456789012345678901231";
  private static final String MAKE_ORDER_URL = "/make_order";
  private static final String CARD_ID_PARAM = "cardId";
  private static final int STATUS_BAD_REQUEST = 400;
  private Map<ProductInfo, Integer> productsInfo;

  @Autowired private MockMvc mockMvc;
  @MockBean private ShopService shopService;
  @MockBean private ProductService productService;
  @MockBean private ReservedProductService reservedProductService;
  @MockBean private PayUService payUService;

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
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenBadSizeOfProducts()
      throws Exception {
    final String productInfoJSON =
        """
                {
                  "productId": \""""
            + TEST_PRODUCT_ID
            + "\","
            + """
                        "hasPromotion": true
                      }
                """;
    final ProductInfo productInfo = new ProductInfo(TEST_PRODUCT_ID, true);
    final Map<String, Integer> productsJSON = Map.of(productInfoJSON, 11);
    final Map<ProductInfo, Integer> products = Map.of(productInfo, 11);
    ObjectMapper objectMapper = new ObjectMapper();

    when(productService.getProductsInfo(productsJSON)).thenReturn(products);
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
    setProductsInfoForMakeOrder();

    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(false);

    performPostAtMakeOrder("Zbyt dużo zarezerwowanych produktów", STATUS_BAD_REQUEST);
  }

  @Test
  public void
      shouldReturnResponseWithErrorMessageAtMakeOrderWhenTooManyReservedProductsByIpAddress()
          throws Exception {
    setProductsInfoForMakeOrder();

    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(false);

    performPostAtMakeOrder("Zbyt dużo zarezerwowanych produktów", STATUS_BAD_REQUEST);
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenErrorAtReservedProducts()
      throws Exception {
    setProductsInfoForMakeOrder();

    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(true);
    when(reservedProductService.reservedProducts(eq(productsInfo), anyString(), eq(TEST_CARD_ID)))
        .thenReturn(false);

    performPostAtMakeOrder("Błąd podczas rezerwacji produktów", STATUS_BAD_REQUEST);
  }

  @Test
  public void shouldReturnResponseWithErrorMessageAtMakeOrderWhenErrorMakeOrderOfProducts()
      throws Exception {
    setProductsInfoForMakeOrder();

    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(true);
    when(reservedProductService.reservedProducts(eq(productsInfo), anyString(), eq(TEST_CARD_ID)))
        .thenReturn(true);
    when(payUService.productsOrder(eq(productsInfo), eq(TEST_CARD_ID), anyString()))
        .thenReturn(Pair.of(false, "error"));

    performPostAtMakeOrder("Nieoczekiwany błąd", STATUS_BAD_REQUEST);
  }

  @Test
  public void shouldReturnResponseOkWithPaymentUrlAtMakeOrderWhenEverythingOk() throws Exception {
    final int statusOk = 200;
    setProductsInfoForMakeOrder();

    when(productService.checkProductsQuantity(productsInfo)).thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByCardId(TEST_CARD_ID))
        .thenReturn(true);
    when(reservedProductService.checkReservedProductsIsLessThan100ByEncryptedIp(anyString()))
        .thenReturn(true);
    when(reservedProductService.reservedProducts(eq(productsInfo), anyString(), eq(TEST_CARD_ID)))
        .thenReturn(true);
    when(payUService.productsOrder(eq(productsInfo), eq(TEST_CARD_ID), anyString()))
        .thenReturn(Pair.of(true, "payu.com"));

    performPostAtMakeOrder("payu.com", statusOk);
  }

  private void setProductsInfoForMakeOrder() {
    final ProductInfo productInfo = new ProductInfo(TEST_PRODUCT_ID, true);
    productsInfo = Map.of(productInfo, 1);
  }

  private void performPostAtMakeOrder(String message, int status) throws Exception {
    final String productInfoJSON =
        """
                {
                  "productId": \""""
            + TEST_PRODUCT_ID
            + "\","
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
                    .content(objectMapper.writeValueAsString(productsJSON)))
            .andExpect(status().is(status))
            .andReturn();
    assertEquals(message, result.getResponse().getContentAsString());
  }
}
