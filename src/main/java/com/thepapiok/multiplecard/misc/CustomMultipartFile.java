package com.thepapiok.multiplecard.misc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
  private final String name;
  private final String originalFileName;
  private final String contentType;
  private final byte[] content;

  public CustomMultipartFile(
      String name, String originalFileName, String contentType, byte[] content) {
    this.name = name;
    this.originalFileName = originalFileName;
    this.contentType = contentType;
    this.content = content;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFileName;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return content;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {}
}
