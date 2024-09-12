package com.thepapiok.multiplecard.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class CloudinaryService {

  @Value("${CLOUDINARY_CLOUD_NAME}")
  private String cloudinaryCloudName;

  @Value("${CLOUDINARY_API_KEY}")
  private String cloudinaryApiKey;

  @Value("${CLOUDINARY_API_SECRET}")
  private String cloudinaryApiSecret;

  private Cloudinary cloudinary;

  @PostConstruct
  public void init() {
    cloudinary =
        new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudinaryCloudName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinaryApiSecret,
                "secure", true));
  }

  public String addImage(byte[] file, String name) {
    try {
      return (String)
          cloudinary
              .uploader()
              .upload(
                  file,
                  ObjectUtils.asMap(
                      "public_id", name,
                      "unique_filename", false,
                      "overwrite", true))
              .get("url");
    } catch (Exception e) {
      return null;
    }
  }

  @Profile("test")
  public void setCloudinary(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }
}
