package com.wkclz.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wkclz.common.exception.SysException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.EnumMap;

/**
 * 生成二维码帮助类
 * @author shrimp
 */
public class QrCodeUtil {
    private static final Logger logger = LoggerFactory.getLogger(QrCodeUtil.class);

    /**
     * 生成base64位二维码
     *
     * @param url
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static String createBase64QrCode(String url) {
        BufferedImage bufferedImage = createQrCode(url, BarcodeFormat.QR_CODE, 400, 400);
        return bufferedImage2Base64(bufferedImage);
    }

    /**
     * 生成base64 条码
     *
     * @param url
     * @return
     */
    public static String createBase64BarCode(String url) {
        BufferedImage bufferedImage = createQrCode(url, BarcodeFormat.CODE_39, 600, 200);
        return bufferedImage2Base64(bufferedImage);
    }

    /**
     * 小程序二维码base64
     *
     * @param urls
     * @return
     * @throws IOException
     */
    public static String createBase64QrCodeWxapp(String urls) {

        try {
            URL url = new URI(urls).toURL();
            // Or whatever size you want to read in at a time.
            try (
                InputStream is = url.openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ) {
                byte[] byteChunk = new byte[4096];
                int i;
                while ((i = is.read(byteChunk)) > 0) {
                    baos.write(byteChunk, 0, i);
                }
                return Base64.getEncoder().encodeToString(baos.toByteArray());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    // 文本转 二维码
    public static BufferedImage createQrCode(String txt, BarcodeFormat format, int width, int height) {
        if (StringUtils.isBlank(txt)) {
            throw SysException.error("no txt to create QR code");
        }
        try {
            EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            // 设置白边为1，最小可设为0
            hints.put(EncodeHintType.MARGIN, 1);
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(txt, format, width, height, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    // 图片转 Base64
    public static String bufferedImage2Base64(BufferedImage image) {
        if (image == null) {
            return null;
        }
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", Base64.getEncoder().wrap(os));
            return "data:image/png;base64," + os;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File bufferedImage2File(BufferedImage image, String file) {
        return bufferedImage2File(image, new File(file));
    }
    public static File bufferedImage2File(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "png", file);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}


