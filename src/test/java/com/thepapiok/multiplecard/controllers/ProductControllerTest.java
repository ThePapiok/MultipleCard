package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductGetDTO;
import com.thepapiok.multiplecard.dto.PromotionGetDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ResultService;
import com.thepapiok.multiplecard.services.ShopService;
import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductControllerTest {
  private static final String TEST_PHONE = "+4324234234234234";
  private static final String TEST_BAD_CATEGORY1_NAME = "category1";
  private static final String TEST_BAD_CATEGORY2_NAME = "category2";
  private static final String TEST_BAD_CATEGORY3_NAME = "category3";
  private static final String TEST_FILE_NAME = "file";
  private static final String TEST_PRODUCT_NAME = "Addd";
  private static final String TEST_BARCODE = "1234567890123";
  private static final String TEST_AMOUNT = "123.12zł";
  private static final String TEST_AMOUNT_WITHOUT_CURRENCY = "123.12";
  private static final String TEST_DESCRIPTION = "asdfds";
  private static final String TEST_CATEGORY1_NAME = "Kategoria";
  private static final String TEST_CATEGORY2_NAME = "Kateegoria";
  private static final String TEST_CATEGORY3_NAME = "Kateeegoria";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ERROR_PARAM = "error";
  private static final String ID_PARAM = "id";
  private static final String ERROR_MESSAGE = "error!";
  private static final String SUCCESS_PARAM = "success";
  private static final String ADD_PRODUCT_PARAM = "addProduct";
  private static final String CATEGORIES_PARAM = "categories";
  private static final String NAME_PARAM = "name";
  private static final String DESCRIPTION_PARAM = "description";
  private static final String BARCODE_PARAM = "barcode";
  private static final String AMOUNT_PARAM = "amount";
  private static final String CATEGORY0_PARAM = "category[0]";
  private static final String CATEGORY1_PARAM = "category[1]";
  private static final String CATEGORY2_PARAM = "category[2]";
  private static final String PRODUCTS_URL = "/products";
  private static final String ADD_PRODUCT_URL = "/add_product";
  private static final String ADD_PRODUCT_ERROR_URL = "/add_product?error";
  private static final String PRODUCTS_PAGE = "productsPage";
  private static final String ADD_PRODUCT_PAGE = "addProductPage";
  private static final String ERROR_VALIDATION_MESSAGE = "Podane dane są niepoprawne";
  private static final String FIELD_PARAM = "field";
  private static final String IS_DESCENDING_PARAM = "isDescending";
  private static final String PAGES_PARAM = "pages";
  private static final String PAGE_SELECTED_PARAM = "pageSelected";
  private static final String PRODUCTS_PARAM = "products";
  private static final String PROMOTIONS_PARAM = "promotions";
  private static final String PRODUCTS_SIZE_PARAM = "productsSize";
  private static final String MAX_PAGE_PARAM = "maxPage";
  private static final String COUNT_FIELD = "count";
  private static final Integer TEST_PRODUCT_SIZE = 2;
  private static final String ERROR_UNEXPECTED_MESSAGE = "Nieoczekiwany błąd";

  private Product testProduct1;
  private Product testProduct2;
  private List<Integer> testPages;
  private PromotionGetDTO testPromotion;
  private List<ProductGetDTO> testProducts;

  @Autowired private MockMvc mockMvc;
  @MockBean private CategoryService categoryService;
  @MockBean private AccountRepository accountRepository;
  @MockBean private ShopService shopService;
  @MockBean private ProductService productService;
  @MockBean private ResultService resultService;

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWhenEverythingOk() throws Exception {
    setDataForProductsPage();

    mockMvc
        .perform(get(PRODUCTS_URL))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(
            model()
                .attribute(
                    PRODUCTS_PARAM,
                    List.of(
                        new ProductDTO(true, testProduct1), new ProductDTO(false, testProduct2))))
        .andExpect(model().attribute(PROMOTIONS_PARAM, List.of(testPromotion)))
        .andExpect(model().attribute(PRODUCTS_SIZE_PARAM, TEST_PRODUCT_SIZE))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(view().name(PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWithErrorParamWithoutMessage()
      throws Exception {
    setDataForProductsPage();

    when(productService.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, true, ""))
        .thenReturn(testProducts);
    when(productService.getMaxPage("", TEST_PHONE)).thenReturn(1);
    when(resultService.getPages(1, 1)).thenReturn(testPages);

    mockMvc
        .perform(get(PRODUCTS_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(
            model()
                .attribute(
                    PRODUCTS_PARAM,
                    List.of(
                        new ProductDTO(true, testProduct1), new ProductDTO(false, testProduct2))))
        .andExpect(model().attribute(PROMOTIONS_PARAM, List.of(testPromotion)))
        .andExpect(model().attribute(PRODUCTS_SIZE_PARAM, TEST_PRODUCT_SIZE))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(view().name(PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWithErrorParam() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    setDataForProductsPage();

    when(productService.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, true, ""))
        .thenReturn(testProducts);
    when(productService.getMaxPage("", TEST_PHONE)).thenReturn(1);
    when(resultService.getPages(1, 1)).thenReturn(testPages);

    mockMvc
        .perform(get(PRODUCTS_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(
            model()
                .attribute(
                    PRODUCTS_PARAM,
                    List.of(
                        new ProductDTO(true, testProduct1), new ProductDTO(false, testProduct2))))
        .andExpect(model().attribute(PROMOTIONS_PARAM, List.of(testPromotion)))
        .andExpect(model().attribute(PRODUCTS_SIZE_PARAM, TEST_PRODUCT_SIZE))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(PRODUCTS_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWithSuccessParamWithoutMessage()
      throws Exception {
    setDataForProductsPage();

    when(productService.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, true, ""))
        .thenReturn(testProducts);
    when(productService.getMaxPage("", TEST_PHONE)).thenReturn(1);
    when(resultService.getPages(1, 1)).thenReturn(testPages);

    mockMvc
        .perform(get(PRODUCTS_URL).param(SUCCESS_PARAM, ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(
            model()
                .attribute(
                    PRODUCTS_PARAM,
                    List.of(
                        new ProductDTO(true, testProduct1), new ProductDTO(false, testProduct2))))
        .andExpect(model().attribute(PROMOTIONS_PARAM, List.of(testPromotion)))
        .andExpect(model().attribute(PRODUCTS_SIZE_PARAM, TEST_PRODUCT_SIZE))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(view().name(PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWithSuccessParam() throws Exception {
    final String successMessage = "success!";
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(SUCCESS_MESSAGE_PARAM, successMessage);
    setDataForProductsPage();

    when(productService.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, true, ""))
        .thenReturn(testProducts);
    when(productService.getMaxPage("", TEST_PHONE)).thenReturn(1);
    when(resultService.getPages(1, 1)).thenReturn(testPages);

    mockMvc
        .perform(get(PRODUCTS_URL).param(SUCCESS_PARAM, "").session(httpSession))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(
            model()
                .attribute(
                    PRODUCTS_PARAM,
                    List.of(
                        new ProductDTO(true, testProduct1), new ProductDTO(false, testProduct2))))
        .andExpect(model().attribute(PROMOTIONS_PARAM, List.of(testPromotion)))
        .andExpect(model().attribute(PRODUCTS_SIZE_PARAM, TEST_PRODUCT_SIZE))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(SUCCESS_MESSAGE_PARAM, successMessage))
        .andExpect(view().name(PRODUCTS_PAGE));
    assertNull(httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  private void setDataForProductsPage() {
    final ObjectId productId1 = new ObjectId("123456789012345678904312");
    final ObjectId productId2 = new ObjectId("023456589012345178904387");
    final int testYearStartAt = 2024;
    final int testMonthStartAt = 1;
    final int testDayStartAt = 1;
    final int testYearExpiredAt = 2015;
    final int testMonthExpiredAt = 2;
    final int testDayExpiredAt = 3;
    final int testAmount = 10;
    final int testOtherAmount = 222;
    final int testPromotionAmount = 5;
    List<ObjectId> categories = List.of(new ObjectId("153456489019345178004311"));
    LocalDate startAt = LocalDate.of(testYearStartAt, testMonthStartAt, testDayStartAt);
    LocalDate expiredAt = LocalDate.of(testYearExpiredAt, testMonthExpiredAt, testDayExpiredAt);
    testProduct1 = new Product();
    testProduct1.setImageUrl("url1");
    testProduct1.setBarcode("barcode1");
    testProduct1.setDescription("description1");
    testProduct1.setName("name1");
    testProduct1.setShopId(TEST_ID);
    testProduct1.setId(productId1);
    testProduct1.setCategories(categories);
    testProduct1.setAmount(testAmount);
    testProduct2 = new Product();
    testProduct2.setAmount(testOtherAmount);
    testProduct2.setDescription("description2");
    testProduct2.setBarcode("barcode2");
    testProduct2.setShopId(TEST_ID);
    testProduct2.setImageUrl("url2");
    testProduct2.setName("name2");
    testProduct2.setCategories(categories);
    testProduct2.setId(productId2);
    Promotion promotion1 = new Promotion();
    promotion1.setId(new ObjectId("923426389512345172904181"));
    promotion1.setAmount(testPromotionAmount);
    promotion1.setProductId(productId1);
    promotion1.setStartAt(startAt);
    promotion1.setExpiredAt(expiredAt);
    testPromotion =
        new PromotionGetDTO(productId1.toString(), startAt, expiredAt, testPromotionAmount, 0);
    ProductGetDTO productGetDTO1 = new ProductGetDTO();
    productGetDTO1.setProduct(testProduct1);
    productGetDTO1.setPromotion(promotion1);
    productGetDTO1.setBlocked(null);
    ProductGetDTO productGetDTO2 = new ProductGetDTO();
    productGetDTO2.setProduct(testProduct2);
    productGetDTO2.setPromotion(null);
    productGetDTO2.setBlocked(new Blocked());
    testProducts = List.of(productGetDTO1, productGetDTO2);
    testPages = List.of(1);

    when(productService.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, true, ""))
        .thenReturn(testProducts);
    when(productService.getMaxPage("", TEST_PHONE)).thenReturn(1);
    when(resultService.getPages(1, 1)).thenReturn(testPages);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddProductPageAtAddProductPageWhenEverythingOk() throws Exception {
    List<String> categories = List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME);

    when(categoryService.getAllNames()).thenReturn(categories);

    mockMvc
        .perform(get(ADD_PRODUCT_URL))
        .andExpect(model().attribute(CATEGORIES_PARAM, categories))
        .andExpect(model().attribute(ADD_PRODUCT_PARAM, new AddProductDTO()))
        .andExpect(view().name(ADD_PRODUCT_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddProductPageAtAddProductPageWithErrorParamWithoutMessage()
      throws Exception {
    List<String> categories = List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME);

    when(categoryService.getAllNames()).thenReturn(categories);

    mockMvc
        .perform(get(ADD_PRODUCT_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(CATEGORIES_PARAM, categories))
        .andExpect(model().attribute(ADD_PRODUCT_PARAM, new AddProductDTO()))
        .andExpect(view().name(ADD_PRODUCT_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnAddProductPageAtAddProductPageWithErrorParam() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    List<String> categories = List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME);

    when(categoryService.getAllNames()).thenReturn(categories);

    mockMvc
        .perform(get(ADD_PRODUCT_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(CATEGORIES_PARAM, categories))
        .andExpect(model().attribute(ADD_PRODUCT_PARAM, new AddProductDTO()))
        .andExpect(model().attribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE))
        .andExpect(view().name(ADD_PRODUCT_PAGE));
    assertNull(httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenErrorAtValidation() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("gg123213123123123");
    addProductDTO.setBarcode("aa");
    addProductDTO.setAmount("123");
    addProductDTO.setDescription("asdf");
    addProductDTO.setCategory(
        List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME, TEST_BAD_CATEGORY3_NAME));

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenErrorAtValidationCategory()
      throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setBarcode(TEST_BARCODE);
    addProductDTO.setAmount(TEST_AMOUNT);
    addProductDTO.setDescription(TEST_DESCRIPTION);
    addProductDTO.setCategory(
        List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME, TEST_BAD_CATEGORY3_NAME));

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenErrorBadSizeOfCategories()
      throws Exception {
    final int index3 = 3;
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setBarcode(TEST_BARCODE);
    addProductDTO.setAmount(TEST_AMOUNT);
    addProductDTO.setDescription(TEST_DESCRIPTION);
    addProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME, "Kateeeegoria"));
    List<String> categories = addProductDTO.getCategory();
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    mockMvc
        .perform(
            multipart(ADD_PRODUCT_URL)
                .file(new MockMultipartFile(TEST_FILE_NAME, new byte[0]))
                .param(NAME_PARAM, addProductDTO.getName())
                .param(DESCRIPTION_PARAM, addProductDTO.getDescription())
                .param(BARCODE_PARAM, addProductDTO.getBarcode())
                .param(CATEGORY0_PARAM, categories.get(0))
                .param(CATEGORY1_PARAM, categories.get(1))
                .param(CATEGORY2_PARAM, categories.get(2))
                .param("category[3]", categories.get(index3))
                .param(AMOUNT_PARAM, addProductDTO.getAmount())
                .session(httpSession))
        .andExpect(redirectedUrl(ADD_PRODUCT_ERROR_URL));
    assertEquals("Zła ilość kategorii", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenErrorCategoriesNotUnique()
      throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setBarcode(TEST_BARCODE);
    addProductDTO.setAmount(TEST_AMOUNT);
    addProductDTO.setDescription(TEST_DESCRIPTION);
    addProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME));

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals("Kategorie muszą być unikalne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenErrorBadImage() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = setUpAddProductDTO(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(false);

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals("Niepoprawny plik", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenOwnerHasTooManyCategoriesCreated()
      throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = setUpAddProductDTO(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_ID, addProductDTO.getCategory()))
        .thenReturn(true);

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals(
        "Za dużo stworzyłeś nowych kategorii", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenOwnerHasTheSameProductName()
      throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = setUpAddProductDTO(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_ID, addProductDTO.getName()))
        .thenReturn(true);

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals(
        "Posiadasz już produkt o takiej nazwie", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToAddProductErrorAtAddProductWhenOwnerHasTheSameBarcode()
      throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = setUpAddProductDTO(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_ID, addProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_ID, addProductDTO.getBarcode()))
        .thenReturn(true);

    performPostAddProduct(addProductDTO, httpSession, ADD_PRODUCT_ERROR_URL, multipartFile);
    assertEquals(
        "Posiadasz już produkt o takim kodzie kreskowym",
        httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsErrorAtAddProductWhenErrorAtAddProduct() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = setUpAddProductDTO(multipartFile);
    AddProductDTO expectedAddProductDTO = new AddProductDTO();
    expectedAddProductDTO.setName(TEST_PRODUCT_NAME);
    expectedAddProductDTO.setBarcode(TEST_BARCODE);
    expectedAddProductDTO.setAmount(TEST_AMOUNT_WITHOUT_CURRENCY);
    expectedAddProductDTO.setDescription(TEST_DESCRIPTION);
    expectedAddProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME));
    expectedAddProductDTO.setFile(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_ID, addProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_ID, addProductDTO.getBarcode()))
        .thenReturn(false);
    when(productService.addProduct(expectedAddProductDTO, TEST_ID, addProductDTO.getCategory()))
        .thenReturn(false);

    performPostAddProduct(addProductDTO, httpSession, "/products?error", multipartFile);
    assertEquals(ERROR_UNEXPECTED_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsSuccessAtAddProductWhenEverythingOk() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);
    MockHttpSession httpSession = new MockHttpSession();
    AddProductDTO addProductDTO = setUpAddProductDTO(multipartFile);
    AddProductDTO expectedAddProductDTO = new AddProductDTO();
    expectedAddProductDTO.setName(TEST_PRODUCT_NAME);
    expectedAddProductDTO.setBarcode(TEST_BARCODE);
    expectedAddProductDTO.setAmount(TEST_AMOUNT_WITHOUT_CURRENCY);
    expectedAddProductDTO.setDescription(TEST_DESCRIPTION);
    expectedAddProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME));
    expectedAddProductDTO.setFile(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_ID, addProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_ID, addProductDTO.getBarcode()))
        .thenReturn(false);
    when(productService.addProduct(expectedAddProductDTO, TEST_ID, addProductDTO.getCategory()))
        .thenReturn(true);

    performPostAddProduct(addProductDTO, httpSession, "/products?success", multipartFile);
    assertEquals("Pomyślnie dodany nowy produkt", httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  private AddProductDTO setUpAddProductDTO(MockMultipartFile multipartFile) {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName(TEST_PRODUCT_NAME);
    addProductDTO.setBarcode(TEST_BARCODE);
    addProductDTO.setAmount(TEST_AMOUNT);
    addProductDTO.setDescription(TEST_DESCRIPTION);
    addProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME));
    addProductDTO.setFile(multipartFile);
    return addProductDTO;
  }

  private void performPostAddProduct(
      AddProductDTO addProductDTO,
      MockHttpSession httpSession,
      String redirectUrl,
      MockMultipartFile multipartFile)
      throws Exception {
    List<String> categories = addProductDTO.getCategory();
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);

    mockMvc
        .perform(
            multipart(ADD_PRODUCT_URL)
                .file(multipartFile)
                .param(NAME_PARAM, addProductDTO.getName())
                .param(DESCRIPTION_PARAM, addProductDTO.getDescription())
                .param(BARCODE_PARAM, addProductDTO.getBarcode())
                .param(CATEGORY0_PARAM, categories.get(0))
                .param(CATEGORY1_PARAM, categories.get(1))
                .param(CATEGORY2_PARAM, categories.get(2))
                .param(AMOUNT_PARAM, addProductDTO.getAmount())
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtDeleteProductWhenIsNotOwner() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID.toString())).thenReturn(false);

    mockMvc
        .perform(delete(PRODUCTS_URL).param(ID_PARAM, TEST_ID.toHexString()))
        .andExpect(content().string("Nie posiadasz tego produktu"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtDeleteProductWhenErrorAtDeleteProduct() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID.toString())).thenReturn(true);
    when(productService.deleteProduct(TEST_ID.toString())).thenReturn(false);

    mockMvc
        .perform(delete(PRODUCTS_URL).param(ID_PARAM, TEST_ID.toHexString()))
        .andExpect(content().string(ERROR_UNEXPECTED_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnOkAtDeleteProductWhenEverythingOk() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID.toString())).thenReturn(true);
    when(productService.deleteProduct(TEST_ID.toString())).thenReturn(true);

    mockMvc
        .perform(delete(PRODUCTS_URL).param(ID_PARAM, TEST_ID.toHexString()))
        .andExpect(content().string("ok"));
  }
}
