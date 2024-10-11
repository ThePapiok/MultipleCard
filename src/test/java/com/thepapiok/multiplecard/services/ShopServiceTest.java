package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.misc.AddressConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

public class ShopServiceTest {

  private static final String TEST_ACCOUNT_NUMBER = "12312312312312312312312";
  private static final String TEST_SHOP_NAME = "test";
  private static final String TEST_CONTENT_TYPE = "image/png";
  private static final String TEST_FILE_NAME = "file";
  private static final String TEST_FILE1_NAME = "file1";
  private static final String TEST_FILE2_NAME = "file2";
  private static final String TEST_FILE3_NAME = "file3";
  private static final String TEST_FILE4_NAME = "file4";
  private static final String TEST_FORMAT_NAME = "png";
  private static final String TEST_OTHER_CONTENT_TYPE = "application/pdf";
  private static final String IBAN_API_URL =
      "https://api.ibanapi.com/v1/validate-basic/PL12312312312312312312312?api_key=null";
  @Mock private AddressConverter addressConverter;
  @Mock private RestTemplate restTemplate;
  @Mock private AccountRepository accountRepository;
  private ShopService shopService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    shopService = new ShopService(addressConverter, accountRepository, restTemplate);
  }

  @Test
  public void shouldSuccessAtCheckImage() throws IOException {
    final int goodWidth = 460;
    final int goodHeight = 460;
    MultipartFile multipartFile = setFile(TEST_CONTENT_TYPE, goodWidth, goodHeight);

    assertTrue(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldFailAtCheckImageWhenEmptyFile() {
    assertFalse(shopService.checkImage(new MockMultipartFile(TEST_FILE_NAME, new byte[0])));
  }

  @Test
  public void shouldFailAtCheckImageWhenToLowHeight() throws IOException {
    final int goodWidth = 460;
    final int badHeight = 410;
    MultipartFile multipartFile = setFile(TEST_CONTENT_TYPE, goodWidth, badHeight);

    assertFalse(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldFailAtCheckImageWhenToLowWidth() throws IOException {
    final int badWidth = 410;
    final int goodHeight = 460;
    MultipartFile multipartFile = setFile(TEST_CONTENT_TYPE, badWidth, goodHeight);

    assertFalse(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldFailAtCheckImageWhenBadType() throws IOException {
    final int goodWidth = 460;
    final int goodHeight = 460;
    MultipartFile multipartFile = setFile("pdf", goodWidth, goodHeight);

    assertFalse(shopService.checkImage(multipartFile));
  }

  private MultipartFile setFile(String contentType, int width, int height) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, TEST_FORMAT_NAME, byteArrayOutputStream);
    return new MockMultipartFile(
        TEST_FILE_NAME, TEST_FILE_NAME, contentType, byteArrayOutputStream.toByteArray());
  }

  @Test
  public void shouldFailAtCheckImageWhenTooMuchSize() throws IOException {
    final int width = 1000;
    final int height = 1000;
    final int maxRGB = 256;
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    Random random = new Random();
    graphics2D.setColor(Color.RED);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        graphics2D.setColor(
            new Color(
                random.nextInt(0, maxRGB), random.nextInt(0, maxRGB), random.nextInt(0, maxRGB)));
        graphics2D.drawRect(i, j, 1, 1);
      }
    }
    graphics2D.dispose();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, TEST_FORMAT_NAME, byteArrayOutputStream);
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            TEST_FILE_NAME, TEST_FILE_NAME, TEST_CONTENT_TYPE, byteArrayOutputStream.toByteArray());

    assertFalse(shopService.checkImage(multipartFile));
  }

  @Test
  public void shouldSuccessAtCheckAccountNumberExistsWhenNullPhone() {
    when(accountRepository.existsByAccountNumberOtherThanPhone(TEST_ACCOUNT_NUMBER, null))
        .thenReturn(true);

    assertTrue(shopService.checkAccountNumberExists(TEST_ACCOUNT_NUMBER, null));
  }

  @Test
  public void shouldFailAtCheckAccountNumberExistsWhenNotFoundAndNullPhone() {
    when(accountRepository.existsByAccountNumberOtherThanPhone(TEST_ACCOUNT_NUMBER, null))
        .thenReturn(false);

    assertFalse(shopService.checkAccountNumberExists(TEST_ACCOUNT_NUMBER, null));
  }

  @Test
  public void shouldSuccessAtCheckShopNameExistsWhenNullPhone() {
    when(accountRepository.existsByNameOtherThanPhone(TEST_SHOP_NAME, null)).thenReturn(true);

    assertTrue(shopService.checkShopNameExists(TEST_SHOP_NAME, null));
  }

  @Test
  public void shouldFailAtCheckShopNameExistsWhenNotFoundAndNullPhone() {
    when(accountRepository.existsByNameOtherThanPhone(TEST_SHOP_NAME, null)).thenReturn(false);

    assertFalse(shopService.checkShopNameExists(TEST_SHOP_NAME, null));
  }

  @Test
  public void shouldSuccessAtCheckAccountNumber() {
    final int statusOk = 200;
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatusCode.valueOf(statusOk));

    when(restTemplate.exchange(IBAN_API_URL, HttpMethod.GET, null, String.class))
        .thenReturn(response);

    assertTrue(shopService.checkAccountNumber(TEST_ACCOUNT_NUMBER));
  }

  @Test
  public void shouldFailAtCheckAccountNumberWhenInvalidAccountNumber() {
    when(restTemplate.exchange(IBAN_API_URL, HttpMethod.GET, null, String.class))
        .thenThrow(HttpClientErrorException.class);

    assertFalse(shopService.checkAccountNumber(TEST_ACCOUNT_NUMBER));
  }

  @Test
  public void shouldSuccessAtCheckPointExistsWhenNullPhone() {
    AddressDTO addressDTO1 = new AddressDTO();
    AddressDTO addressDTO2 = new AddressDTO();
    List<AddressDTO> addressDTOList = List.of(addressDTO1, addressDTO2);
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = List.of(address1, address2);

    when(addressConverter.getEntities(addressDTOList)).thenReturn(addresses);
    when(accountRepository.existsByPointsOtherThanPhone(address1, null)).thenReturn(true);

    assertTrue(shopService.checkPointsExists(addressDTOList, null));
  }

  @Test
  public void shouldFailAtCheckPointExistsWhenNoTheSamePlacesWhenNullPhone() {
    AddressDTO addressDTO1 = new AddressDTO();
    AddressDTO addressDTO2 = new AddressDTO();
    List<AddressDTO> addressDTOList = List.of(addressDTO1, addressDTO2);
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = List.of(address1, address2);

    when(addressConverter.getEntities(addressDTOList)).thenReturn(addresses);

    assertFalse(shopService.checkPointsExists(addressDTOList, null));
  }

  @Test
  public void shouldSuccessAtSaveTempFile() throws IOException {
    MockMultipartFile multipartFile = new MockMultipartFile(TEST_FILE_NAME, new byte[0]);

    String filePath = shopService.saveTempFile(multipartFile);
    System.out.println(filePath);
    assertTrue(filePath.contains("upload_"));
    assertTrue(filePath.contains(".tmp"));

    Path path = Path.of(filePath);
    Files.deleteIfExists(path);
  }

  @Test
  public void shouldSuccessAtCheckFiles() {
    MockMultipartFile multipartFile1 =
        new MockMultipartFile(
            TEST_FILE1_NAME, TEST_FILE1_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile2 =
        new MockMultipartFile(
            TEST_FILE2_NAME, TEST_FILE2_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile3 =
        new MockMultipartFile(
            TEST_FILE3_NAME, TEST_FILE3_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile4 = new MockMultipartFile(TEST_FILE4_NAME, new byte[0]);
    List<MultipartFile> list =
        List.of(multipartFile1, multipartFile2, multipartFile3, multipartFile4);

    assertTrue(shopService.checkFiles(list));
  }

  @Test
  public void shouldFailAtCheckFilesWhenNoPdf() {
    MockMultipartFile multipartFile1 =
        new MockMultipartFile(
            TEST_FILE1_NAME, TEST_FILE1_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile2 =
        new MockMultipartFile(TEST_FILE2_NAME, TEST_FILE2_NAME, "application/cos", new byte[1]);
    MockMultipartFile multipartFile3 =
        new MockMultipartFile(
            TEST_FILE3_NAME, TEST_FILE3_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile4 = new MockMultipartFile(TEST_FILE4_NAME, new byte[0]);
    List<MultipartFile> list =
        List.of(multipartFile1, multipartFile2, multipartFile3, multipartFile4);

    assertFalse(shopService.checkFiles(list));
  }

  @Test
  public void shouldFailAtCheckFilesWhenTooManyEmptyFiles() {
    MockMultipartFile multipartFile1 =
        new MockMultipartFile(
            TEST_FILE1_NAME, TEST_FILE1_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile2 =
        new MockMultipartFile(
            TEST_FILE2_NAME, TEST_FILE2_NAME, TEST_OTHER_CONTENT_TYPE, new byte[1]);
    MockMultipartFile multipartFile3 = new MockMultipartFile(TEST_FILE3_NAME, new byte[0]);
    MockMultipartFile multipartFile4 = new MockMultipartFile(TEST_FILE4_NAME, new byte[0]);
    List<MultipartFile> list =
        List.of(multipartFile1, multipartFile2, multipartFile3, multipartFile4);

    assertFalse(shopService.checkFiles(list));
  }
}
