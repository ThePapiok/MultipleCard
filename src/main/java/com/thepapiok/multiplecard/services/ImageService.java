package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.misc.CustomMultipartFile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

  public List<CustomMultipartFile> generateImage(byte[] qrCode, String cardName, String cardId) {
    try {
      final String frontFileName = "front_" + cardId;
      final String backFileName = "back_" + cardId;
      final String testFormatName = "png";
      final int rotateLeft = -90;
      final int sizeFont = 48;
      final int offsetX = 200;
      final int offsetY = 820;
      Graphics2D graphics2D;
      BufferedImage background1 =
          ImageIO.read(
              Objects.requireNonNull(getClass().getResourceAsStream("/static/images/card.png")));
      BufferedImage qr = ImageIO.read(new ByteArrayInputStream(qrCode));
      BufferedImage result1 =
          new BufferedImage(
              background1.getWidth(), background1.getHeight(), BufferedImage.TYPE_INT_ARGB);
      graphics2D = (Graphics2D) result1.getGraphics();
      graphics2D.drawImage(background1, 0, 0, null);
      graphics2D.drawImage(
          qr,
          background1.getWidth() / 2 - qr.getWidth() / 2,
          background1.getHeight() / 2 - qr.getHeight() / 2,
          null);
      graphics2D.dispose();
      ByteArrayOutputStream byteArrayOutputStreamResult1 = new ByteArrayOutputStream();
      ImageIO.write(result1, testFormatName, byteArrayOutputStreamResult1);
      BufferedImage background2 =
          ImageIO.read(
              Objects.requireNonNull(
                  getClass().getResourceAsStream("/static/images/card_name.png")));
      BufferedImage result2 =
          new BufferedImage(
              background2.getWidth(), background2.getHeight(), BufferedImage.TYPE_INT_ARGB);
      graphics2D = (Graphics2D) result2.getGraphics();
      graphics2D.drawImage(background2, 0, 0, null);
      graphics2D.rotate(Math.toRadians(rotateLeft), offsetX, offsetY);
      graphics2D.setColor(Color.BLACK);
      graphics2D.setFont(new Font("Arial", Font.PLAIN, sizeFont));
      graphics2D.drawString(cardName, offsetX, offsetY);
      graphics2D.dispose();
      ByteArrayOutputStream byteArrayOutputStreamResult2 = new ByteArrayOutputStream();
      ImageIO.write(result2, testFormatName, byteArrayOutputStreamResult2);
      CustomMultipartFile customMultipartFile1 =
          new CustomMultipartFile(
              frontFileName,
              frontFileName + ".png",
              testFormatName,
              byteArrayOutputStreamResult1.toByteArray());
      CustomMultipartFile customMultipartFile2 =
          new CustomMultipartFile(
              backFileName,
              backFileName + ".png",
              testFormatName,
              byteArrayOutputStreamResult2.toByteArray());
      return List.of(customMultipartFile1, customMultipartFile2);
    } catch (Exception e) {
      return List.of();
    }
  }
}
