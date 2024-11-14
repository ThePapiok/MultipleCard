package com.thepapiok.multiplecard.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class QrCodeService {
  public byte[] generateQrCode(String link) throws WriterException, IOException {
    final int width = 420;
    final int height = 420;
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, width, height);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
}
