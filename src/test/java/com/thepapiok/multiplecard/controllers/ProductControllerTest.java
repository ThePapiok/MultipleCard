package com.thepapiok.multiplecard.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.AddProductDTO;
import com.thepapiok.multiplecard.dto.EditProductDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.services.CategoryService;
import com.thepapiok.multiplecard.services.ProductService;
import com.thepapiok.multiplecard.services.ResultService;
import com.thepapiok.multiplecard.services.ShopService;
import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductControllerTest {
  private static final String TEST_PHONE = "+4324234234234234";
  private static final String TEST_BAD_CATEGORY1_NAME = "category1";
  private static final String TEST_BAD_CATEGORY2_NAME = "category2";
  private static final String TEST_FILE_NAME = "file";
  private static final String TEST_PRODUCT_NAME = "Addd";
  private static final String TEST_BARCODE = "1234567890123";
  private static final String TEST_AMOUNT = "123.12zł";
  private static final String TEST_DESCRIPTION = "asdfds";
  private static final String TEST_CATEGORY1_NAME = "Kategoria";
  private static final String TEST_CATEGORY2_NAME = "Kateegoria";
  private static final String TEST_CATEGORY3_NAME = "Kateeegoria";
  private static final String TEST_ID = "123456789012345678901234";
  private static final ObjectId TEST_OBJECT_ID = new ObjectId(TEST_ID);
  private static final String ERROR_MESSAGE_PARAM = "errorMessage";
  private static final String SUCCESS_MESSAGE_PARAM = "successMessage";
  private static final String ERROR_PARAM = "error";
  private static final String ID_PARAM = "id";
  private static final String ERROR_MESSAGE = "error!";
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
  private static final String PRODUCTS_SUCCESS_URL = "/products?success";
  private static final String PRODUCTS_ID_URL = "/products?id=";
  private static final String ERROR_URL_PARAM = "&error";
  private static final String ADD_PRODUCT_URL = "/add_product";
  private static final String ADD_PRODUCT_ERROR_URL = "/add_product?error";
  private static final String BLOCK_PRODUCT_URL = "/block_product";
  private static final String UNBLOCK_PRODUCT_URL = "/unblock_product";
  private static final String PRODUCTS_PAGE = "productsPage";
  private static final String ADD_PRODUCT_PAGE = "addProductPage";
  private static final String ERROR_VALIDATION_MESSAGE = "Podane dane są niepoprawne";
  private static final String FIELD_PARAM = "field";
  private static final String IS_DESCENDING_PARAM = "isDescending";
  private static final String PAGES_PARAM = "pages";
  private static final String PAGE_SELECTED_PARAM = "pageSelected";
  private static final String PRODUCTS_PARAM = "products";
  private static final String PRODUCTS_EMPTY_PARAM = "productsEmpty";
  private static final String MAX_PAGE_PARAM = "maxPage";
  private static final String COUNT_FIELD = "count";
  private static final Integer TEST_PRODUCT_SIZE = 2;
  private static final String ERROR_UNEXPECTED_MESSAGE = "Nieoczekiwany błąd";
  private static final String ERROR_NOT_OWNER_MESSAGE = "Nie posiadasz tego produktu";
  private static final String SUCCESS_OK_MESSAGE = "ok";
  private static final String SUCCESS_EDIT_PRODUCT_MESSAGE = "Pomyślnie edytowano produkt";
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012345678904312");
  private Product testProduct1;
  private Product testProduct2;
  private List<Integer> testPages;
  private List<ProductDTO> testProducts;
  @Autowired private MockMvc mockMvc;
  @MockBean private CategoryService categoryService;
  @MockBean private AccountRepository accountRepository;
  @MockBean private ShopService shopService;
  @MockBean private ProductService productService;
  @MockBean private ResultService resultService;

  @BeforeEach
  public void setUp() {
    final ObjectId productId2 = new ObjectId("023456589012345178904387");
    final int testAmount = 10;
    final int testOtherAmount = 222;
    List<ObjectId> categories = List.of(new ObjectId("153456489019345178004311"));
    testProduct1 = new Product();
    testProduct1.setImageUrl("url1");
    testProduct1.setBarcode("1254561890124");
    testProduct1.setDescription("description1");
    testProduct1.setName("Name");
    testProduct1.setShopId(TEST_OBJECT_ID);
    testProduct1.setId(TEST_PRODUCT_ID);
    testProduct1.setCategories(categories);
    testProduct1.setAmount(testAmount);
    testProduct2 = new Product();
    testProduct2.setAmount(testOtherAmount);
    testProduct2.setDescription("description2");
    testProduct2.setBarcode("barcode2");
    testProduct2.setShopId(TEST_OBJECT_ID);
    testProduct2.setImageUrl("url2");
    testProduct2.setName("name2");
    testProduct2.setCategories(categories);
    testProduct2.setId(productId2);
  }

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
        .andExpect(model().attribute(PRODUCTS_PARAM, testProducts))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, TEST_PRODUCT_SIZE == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(view().name(PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWithErrorParamWithoutMessage()
      throws Exception {
    setDataForProductsPage();

    mockMvc
        .perform(get(PRODUCTS_URL).param(ERROR_PARAM, ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, testProducts))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, TEST_PRODUCT_SIZE == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(view().name(PRODUCTS_PAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductsPageAtProductsPageWithErrorParam() throws Exception {
    MockHttpSession httpSession = new MockHttpSession();
    httpSession.setAttribute(ERROR_MESSAGE_PARAM, ERROR_MESSAGE);
    setDataForProductsPage();

    mockMvc
        .perform(get(PRODUCTS_URL).param(ERROR_PARAM, "").session(httpSession))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, testProducts))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, TEST_PRODUCT_SIZE == 0))
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

    mockMvc
        .perform(get(PRODUCTS_URL).param("success", ""))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, testProducts))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, TEST_PRODUCT_SIZE == 0))
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

    mockMvc
        .perform(get(PRODUCTS_URL).param("success", "").session(httpSession))
        .andExpect(model().attribute(FIELD_PARAM, COUNT_FIELD))
        .andExpect(model().attribute(IS_DESCENDING_PARAM, true))
        .andExpect(model().attribute(PAGES_PARAM, testPages))
        .andExpect(model().attribute(PAGE_SELECTED_PARAM, 1))
        .andExpect(model().attribute(PRODUCTS_PARAM, testProducts))
        .andExpect(model().attribute(PRODUCTS_EMPTY_PARAM, TEST_PRODUCT_SIZE == 0))
        .andExpect(model().attribute(MAX_PAGE_PARAM, 1))
        .andExpect(model().attribute(SUCCESS_MESSAGE_PARAM, successMessage))
        .andExpect(view().name(PRODUCTS_PAGE));
    assertNull(httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsAtProductsPageWithIdParamWhenNotProductOwner()
      throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(false);

    mockMvc
        .perform(get(PRODUCTS_URL).param(ID_PARAM, TEST_ID))
        .andExpect(redirectedUrl(PRODUCTS_URL));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnProductPageAtProductsPageWithIdParamWhenEverythingOk() throws Exception {
    final String productCategory = "food4";
    List<String> allCategories = List.of("food1", "food2", "food3", productCategory);
    Product product = new Product();
    EditProductDTO editProductDTO = new EditProductDTO();

    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.getProductById(TEST_ID)).thenReturn(product);
    when(productService.getEditProductDTO(product)).thenReturn(editProductDTO);
    when(categoryService.getAllNames()).thenReturn(allCategories);
    when(productService.getCategoriesNames(product)).thenReturn(List.of(productCategory));

    mockMvc
        .perform(get(PRODUCTS_URL).param(ID_PARAM, TEST_ID))
        .andExpect(model().attribute(CATEGORIES_PARAM, allCategories))
        .andExpect(model().attribute("product", editProductDTO))
        .andExpect(model().attribute(ID_PARAM, TEST_ID))
        .andExpect(model().attribute("productCategories", List.of(productCategory)))
        .andExpect(view().name("productPage"));
  }

  private void setDataForProductsPage() {
    final int testYearStartAt = 2024;
    final int testMonthStartAt = 1;
    final int testDayStartAt = 1;
    final int testYearExpiredAt = 2015;
    final int testMonthExpiredAt = 2;
    final int testDayExpiredAt = 3;
    final int testPromotionAmount = 5;
    LocalDate startAt = LocalDate.of(testYearStartAt, testMonthStartAt, testDayStartAt);
    LocalDate expiredAt = LocalDate.of(testYearExpiredAt, testMonthExpiredAt, testDayExpiredAt);
    Promotion promotion1 = new Promotion();
    promotion1.setId(new ObjectId("923426389512345172904181"));
    promotion1.setAmount(testPromotionAmount);
    promotion1.setProductId(TEST_PRODUCT_ID);
    promotion1.setStartAt(startAt);
    promotion1.setExpiredAt(expiredAt);
    ProductDTO productDTO1 = new ProductDTO();
    productDTO1.setProductId(testProduct1.getId().toString());
    productDTO1.setProductName(testProduct1.getName());
    productDTO1.setActive(true);
    productDTO1.setAmount(testProduct1.getAmount());
    productDTO1.setDescription(testProduct1.getDescription());
    productDTO1.setShopId(testProduct1.getShopId());
    productDTO1.setCountPromotion(promotion1.getCount());
    productDTO1.setAmountPromotion(promotion1.getAmount());
    productDTO1.setProductImageUrl(testProduct1.getImageUrl());
    productDTO1.setStartAtPromotion(promotion1.getStartAt());
    productDTO1.setExpiredAtPromotion(promotion1.getExpiredAt());
    ProductDTO productDTO2 = new ProductDTO();
    productDTO2.setProductName(testProduct2.getName());
    productDTO2.setProductId(testProduct2.getId().toString());
    productDTO2.setActive(false);
    productDTO2.setAmount(testProduct2.getAmount());
    productDTO2.setDescription(testProduct2.getDescription());
    productDTO2.setShopId(testProduct2.getShopId());
    productDTO2.setCountPromotion(0);
    productDTO2.setAmountPromotion(0);
    productDTO2.setProductImageUrl(testProduct2.getImageUrl());
    productDTO2.setStartAtPromotion(null);
    productDTO2.setExpiredAtPromotion(null);
    testProducts = List.of(productDTO1, productDTO2);
    testPages = List.of(1);

    when(productService.getProducts(TEST_PHONE, 0, COUNT_FIELD, true, "", "", ""))
        .thenReturn(testProducts);
    when(productService.getMaxPage("", TEST_PHONE, "", "")).thenReturn(1);
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
        List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME, "category3"));

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
        List.of(TEST_BAD_CATEGORY1_NAME, TEST_BAD_CATEGORY2_NAME, "category3"));

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
    account.setId(TEST_OBJECT_ID);

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
    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, addProductDTO.getCategory()))
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
    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, addProductDTO.getName()))
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
    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, addProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, addProductDTO.getBarcode()))
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
    expectedAddProductDTO.setAmount("123.12");
    expectedAddProductDTO.setDescription(TEST_DESCRIPTION);
    expectedAddProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME));
    expectedAddProductDTO.setFile(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, addProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, addProductDTO.getBarcode()))
        .thenReturn(false);
    when(productService.addProduct(
            expectedAddProductDTO, TEST_OBJECT_ID, addProductDTO.getCategory()))
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
    expectedAddProductDTO.setAmount("123.12");
    expectedAddProductDTO.setDescription(TEST_DESCRIPTION);
    expectedAddProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME));
    expectedAddProductDTO.setFile(multipartFile);

    when(shopService.checkImage(multipartFile)).thenReturn(true);
    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, addProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, addProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, addProductDTO.getBarcode()))
        .thenReturn(false);
    when(productService.addProduct(
            expectedAddProductDTO, TEST_OBJECT_ID, addProductDTO.getCategory()))
        .thenReturn(true);

    performPostAddProduct(addProductDTO, httpSession, PRODUCTS_SUCCESS_URL, multipartFile);
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
    account.setId(TEST_OBJECT_ID);

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
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(false);

    mockMvc
        .perform(delete(PRODUCTS_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(ERROR_NOT_OWNER_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtDeleteProductWhenErrorAtDeleteProduct() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.deleteProduct(TEST_ID)).thenReturn(false);

    mockMvc
        .perform(delete(PRODUCTS_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(ERROR_UNEXPECTED_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnOkAtDeleteProductWhenEverythingOk() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.deleteProduct(TEST_ID)).thenReturn(true);

    mockMvc
        .perform(delete(PRODUCTS_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(SUCCESS_OK_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtBlockProductWhenIsNotOwner() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(false);

    mockMvc
        .perform(post(BLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(ERROR_NOT_OWNER_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtBlockProductWhenHasBlockAlready() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.hasBlock(TEST_ID)).thenReturn(true);

    mockMvc
        .perform(post(BLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string("Ten produkt już jest zablokowany"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtBlockProductWhenErrorAtBlockProduct() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.hasBlock(TEST_ID)).thenReturn(false);
    when(productService.blockProduct(TEST_ID)).thenReturn(false);

    mockMvc
        .perform(post(BLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(ERROR_UNEXPECTED_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnOkAtBlockProductWhenEverythingOk() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.hasBlock(TEST_ID)).thenReturn(false);
    when(productService.blockProduct(TEST_ID)).thenReturn(true);

    mockMvc
        .perform(post(BLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(SUCCESS_OK_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtUnblockProductWhenIsNotOwner() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(false);

    mockMvc
        .perform(post(UNBLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(ERROR_NOT_OWNER_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtUnblockProductWhenHasNotBlock() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.hasBlock(TEST_ID)).thenReturn(false);

    mockMvc
        .perform(post(UNBLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string("Ten produkt nie jest zablokowany"));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnErrorMessageAtBlockProductWhenErrorAtUnblockProduct() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.hasBlock(TEST_ID)).thenReturn(true);
    when(productService.unblockProduct(TEST_ID)).thenReturn(false);

    mockMvc
        .perform(post(UNBLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(ERROR_UNEXPECTED_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldReturnOkAtBlockProductWhenErrorAtUnblockProduct() throws Exception {
    when(productService.isProductOwner(TEST_PHONE, TEST_ID)).thenReturn(true);
    when(productService.hasBlock(TEST_ID)).thenReturn(true);
    when(productService.unblockProduct(TEST_ID)).thenReturn(true);

    mockMvc
        .perform(post(UNBLOCK_PRODUCT_URL).param(ID_PARAM, TEST_ID))
        .andExpect(content().string(SUCCESS_OK_MESSAGE));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenErrorAtValidation() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();
    editProductDTO.setName(editProductDTO.getName() + "!");
    performPostForEditProduct(
        editProductDTO,
        PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM,
        ERROR_MESSAGE_PARAM,
        ERROR_VALIDATION_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenBadSizeOfCategories() throws Exception {
    final int categoryIndex3 = 3;
    EditProductDTO editProductDTO = setDataForEditProduct();
    editProductDTO.setCategory(
        List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY2_NAME, TEST_CATEGORY3_NAME, "Kategoria4"));
    List<String> categories = editProductDTO.getCategory();
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setPhone(TEST_PHONE);
    MockHttpSession httpSession = new MockHttpSession();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productService.getProductById(TEST_PRODUCT_ID.toString())).thenReturn(testProduct1);

    mockMvc
        .perform(
            multipart(PRODUCTS_URL)
                .file((MockMultipartFile) editProductDTO.getFile())
                .param(ID_PARAM, editProductDTO.getId())
                .param(NAME_PARAM, editProductDTO.getName())
                .param(DESCRIPTION_PARAM, editProductDTO.getDescription())
                .param(BARCODE_PARAM, editProductDTO.getBarcode())
                .param(AMOUNT_PARAM, editProductDTO.getAmount())
                .param(CATEGORY0_PARAM, categories.get(0))
                .param(CATEGORY1_PARAM, categories.get(1))
                .param(CATEGORY2_PARAM, categories.get(2))
                .param("category[3]", categories.get(categoryIndex3))
                .session(httpSession))
        .andExpect(redirectedUrl(PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM));
    assertEquals(ERROR_VALIDATION_MESSAGE, httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenCategoriesNotUnique() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();
    editProductDTO.setCategory(List.of(TEST_CATEGORY1_NAME, TEST_CATEGORY1_NAME));
    List<String> categories = editProductDTO.getCategory();
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setPhone(TEST_PHONE);
    MockHttpSession httpSession = new MockHttpSession();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productService.getProductById(TEST_PRODUCT_ID.toString())).thenReturn(testProduct1);

    mockMvc
        .perform(
            multipart(PRODUCTS_URL)
                .file((MockMultipartFile) editProductDTO.getFile())
                .param(ID_PARAM, editProductDTO.getId())
                .param(NAME_PARAM, editProductDTO.getName())
                .param(DESCRIPTION_PARAM, editProductDTO.getDescription())
                .param(BARCODE_PARAM, editProductDTO.getBarcode())
                .param(AMOUNT_PARAM, editProductDTO.getAmount())
                .param(CATEGORY0_PARAM, categories.get(0))
                .param(CATEGORY1_PARAM, categories.get(1))
                .session(httpSession))
        .andExpect(redirectedUrl(PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM));
    assertEquals("Kategorie muszą być unikalne", httpSession.getAttribute(ERROR_MESSAGE_PARAM));
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenUserHasMoreThan20Categories()
      throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(true);

    performPostForEditProduct(
        editProductDTO,
        PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM,
        ERROR_MESSAGE_PARAM,
        "Za dużo stworzyłeś nowych kategorii");
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenUserHasTheSameProductName()
      throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(true);

    performPostForEditProduct(
        editProductDTO,
        PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM,
        ERROR_MESSAGE_PARAM,
        "Posiadasz już produkt o takiej nazwie");
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenUserHasTheSameBarcode()
      throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, editProductDTO.getBarcode()))
        .thenReturn(true);

    performPostForEditProduct(
        editProductDTO,
        PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM,
        ERROR_MESSAGE_PARAM,
        "Posiadasz już produkt o takim kodzie kreskowym");
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductWithIdAtEditProductWhenBadFile() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, editProductDTO.getBarcode()))
        .thenReturn(false);
    when(shopService.checkImage(editProductDTO.getFile())).thenReturn(false);

    performPostForEditProduct(
        editProductDTO,
        PRODUCTS_ID_URL + TEST_ID + ERROR_URL_PARAM,
        ERROR_MESSAGE_PARAM,
        "Niepoprawny plik");
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsErrorAtEditProductWhenErrorAtEditProduct() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, editProductDTO.getBarcode()))
        .thenReturn(false);
    when(shopService.checkImage(editProductDTO.getFile())).thenReturn(true);
    when(productService.editProduct(any(), eq(TEST_OBJECT_ID), eq(editProductDTO.getCategory())))
        .thenReturn(false);

    performPostForEditProduct(
        editProductDTO, "/products?error", ERROR_MESSAGE_PARAM, ERROR_UNEXPECTED_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsSuccessAtEditProductWhenEverythingOk() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, editProductDTO.getBarcode()))
        .thenReturn(false);
    when(shopService.checkImage(editProductDTO.getFile())).thenReturn(true);
    when(productService.editProduct(any(), eq(TEST_OBJECT_ID), eq(editProductDTO.getCategory())))
        .thenReturn(true);

    performPostForEditProduct(
        editProductDTO, PRODUCTS_SUCCESS_URL, SUCCESS_MESSAGE_PARAM, SUCCESS_EDIT_PRODUCT_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsSuccessAtEditProductWhenNameNoChange() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();
    editProductDTO.setName(testProduct1.getName());

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, editProductDTO.getBarcode()))
        .thenReturn(false);
    when(shopService.checkImage(editProductDTO.getFile())).thenReturn(true);
    when(productService.editProduct(any(), eq(TEST_OBJECT_ID), eq(editProductDTO.getCategory())))
        .thenReturn(true);

    performPostForEditProduct(
        editProductDTO, PRODUCTS_SUCCESS_URL, SUCCESS_MESSAGE_PARAM, SUCCESS_EDIT_PRODUCT_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsSuccessAtEditProductWhenBarcodeNoChange() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();
    editProductDTO.setBarcode(testProduct1.getBarcode());

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(false);
    when(shopService.checkImage(editProductDTO.getFile())).thenReturn(true);
    when(productService.editProduct(any(), eq(TEST_OBJECT_ID), eq(editProductDTO.getCategory())))
        .thenReturn(true);

    performPostForEditProduct(
        editProductDTO, PRODUCTS_SUCCESS_URL, SUCCESS_MESSAGE_PARAM, SUCCESS_EDIT_PRODUCT_MESSAGE);
  }

  @Test
  @WithMockUser(username = TEST_PHONE, roles = "SHOP")
  public void shouldRedirectToProductsSuccessAtEditProductWhenImageNotChange() throws Exception {
    EditProductDTO editProductDTO = setDataForEditProduct();
    editProductDTO.setFile(null);
    List<String> categories = editProductDTO.getCategory();
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setPhone(TEST_PHONE);
    MockHttpSession httpSession = new MockHttpSession();

    when(categoryService.checkOwnerHas20Categories(TEST_OBJECT_ID, editProductDTO.getCategory()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameNameProduct(TEST_OBJECT_ID, editProductDTO.getName()))
        .thenReturn(false);
    when(productService.checkOwnerHasTheSameBarcode(TEST_OBJECT_ID, editProductDTO.getBarcode()))
        .thenReturn(false);
    when(productService.editProduct(any(), eq(TEST_OBJECT_ID), eq(editProductDTO.getCategory())))
        .thenReturn(true);
    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productService.getProductById(editProductDTO.getId())).thenReturn(testProduct1);

    mockMvc
        .perform(
            post(PRODUCTS_URL)
                .param(ID_PARAM, editProductDTO.getId())
                .param(NAME_PARAM, editProductDTO.getName())
                .param(DESCRIPTION_PARAM, editProductDTO.getDescription())
                .param(BARCODE_PARAM, editProductDTO.getBarcode())
                .param(AMOUNT_PARAM, editProductDTO.getAmount())
                .param(CATEGORY0_PARAM, categories.get(0))
                .session(httpSession))
        .andExpect(redirectedUrl(PRODUCTS_SUCCESS_URL));
    assertEquals(SUCCESS_EDIT_PRODUCT_MESSAGE, httpSession.getAttribute(SUCCESS_MESSAGE_PARAM));
  }

  private EditProductDTO setDataForEditProduct() {
    EditProductDTO editProductDTO = new EditProductDTO();
    editProductDTO.setId(TEST_ID);
    editProductDTO.setName(TEST_PRODUCT_NAME);
    editProductDTO.setDescription(TEST_DESCRIPTION);
    editProductDTO.setBarcode(TEST_BARCODE);
    editProductDTO.setAmount(TEST_AMOUNT);
    editProductDTO.setCategory(List.of(TEST_CATEGORY1_NAME));
    editProductDTO.setFile(new MockMultipartFile(TEST_FILE_NAME, new byte[0]));
    return editProductDTO;
  }

  private void performPostForEditProduct(
      EditProductDTO editProductDTO, String redirectUrl, String param, String message)
      throws Exception {
    List<String> categories = editProductDTO.getCategory();
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setPhone(TEST_PHONE);
    MockHttpSession httpSession = new MockHttpSession();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(productService.getProductById(editProductDTO.getId())).thenReturn(testProduct1);

    mockMvc
        .perform(
            multipart(PRODUCTS_URL)
                .file((MockMultipartFile) editProductDTO.getFile())
                .param(ID_PARAM, editProductDTO.getId())
                .param(NAME_PARAM, editProductDTO.getName())
                .param(DESCRIPTION_PARAM, editProductDTO.getDescription())
                .param(BARCODE_PARAM, editProductDTO.getBarcode())
                .param(AMOUNT_PARAM, editProductDTO.getAmount())
                .param(CATEGORY0_PARAM, categories.get(0))
                .session(httpSession))
        .andExpect(redirectedUrl(redirectUrl));
    assertEquals(message, httpSession.getAttribute(param));
  }

  @Test
  public void shouldReturnResponseOfListProductWithShopDTOAtGetProductsWhenEverythingOk()
      throws Exception {
    final int amount = 500;
    final int amountOther = 5200;
    final String testProductId = "523456789012345678101254";
    ProductWithShopDTO product1 = new ProductWithShopDTO();
    product1.setProductId(TEST_ID);
    product1.setProductName(TEST_PRODUCT_NAME);
    product1.setActive(true);
    product1.setDescription(TEST_DESCRIPTION);
    product1.setShopId(TEST_OBJECT_ID);
    product1.setAmount(amount);
    ProductWithShopDTO product2 = new ProductWithShopDTO();
    product1.setProductId(testProductId.toString());
    product1.setProductName(TEST_PRODUCT_NAME + "A");
    product1.setActive(true);
    product1.setDescription(TEST_DESCRIPTION + "C");
    product1.setShopId(TEST_OBJECT_ID);
    product1.setAmount(amountOther);
    List<ProductWithShopDTO> products = List.of(product1, product2);
    StringBuilder productsInfo1 = new StringBuilder();
    StringBuilder productsInfo2 = new StringBuilder();
    productsInfo1
        .append("{\"productId\": \"")
        .append(TEST_ID)
        .append("\", \"hasPromotion\": false}");
    productsInfo2
        .append("{\"productId\": \"")
        .append(testProductId)
        .append("\", \"hasPromotion\": true}");
    ObjectMapper objectMapper = new ObjectMapper();

    when(productService.getProductsByIds(
            List.of(productsInfo1.toString(), productsInfo2.toString())))
        .thenReturn(products);

    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/get_products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            List.of(productsInfo1.toString(), productsInfo2.toString()))))
            .andExpect(status().isOk())
            .andReturn();
    assertNotNull(mvcResult.getResponse().getContentAsString());
  }
}
