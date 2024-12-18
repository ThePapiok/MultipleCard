package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class CloudinaryServiceTest {
  private static final String TEST_FILE_NAME = "name";
  @Mock private Cloudinary cloudinary;
  @Mock private Uploader uploader;
  private final CloudinaryService cloudinaryService = new CloudinaryService();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    cloudinaryService.setCloudinary(cloudinary);
  }

  @Test
  public void shouldReturnUrlAtAddImageWhenEverythingOk() throws IOException {
    final String url = "www.testoweurl";
    byte[] file = new byte[0];
    Map<String, String> upload = Map.of("url", url);

    when(cloudinary.uploader()).thenReturn(uploader);
    when(uploader.upload(
            file,
            ObjectUtils.asMap(
                "public_id", TEST_FILE_NAME,
                "unique_filename", false,
                "overwrite", true)))
        .thenReturn(upload);

    assertEquals(url, cloudinaryService.addImage(file, TEST_FILE_NAME));
  }

  @Test
  public void shouldReturnNullAtAddImageWhenGetException() throws IOException {
    byte[] file = new byte[0];

    when(cloudinary.uploader()).thenReturn(uploader);
    when(uploader.upload(
            file,
            ObjectUtils.asMap(
                "public_id", TEST_FILE_NAME,
                "unique_filename", false,
                "overwrite", true)))
        .thenThrow(RuntimeException.class);

    assertNull(cloudinaryService.addImage(file, TEST_FILE_NAME));
  }
}
