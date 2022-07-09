package com.wkclz.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

/**
 * 生成二维码帮助类
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
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 400, 400);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", Base64.getEncoder().wrap(os));
            return "data:image/jpg;base64," + os.toString();
        } catch (WriterException | IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 生成base64 条码
     *
     * @param url
     * @return
     */
    public static String createBase64BarCode(String url) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.CODE_39, 600, 280);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", Base64.getEncoder().wrap(os));
            return "data:image/jpg;base64," + os.toString();
        } catch (WriterException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {

        }
        return null;
    }

    /**
     * 小程序二维码base64
     *
     * @param urls
     * @return
     * @throws IOException
     */
    public static String createBase64QrCodeWxapp(String urls) {

        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(urls);
            is = url.openStream();
            // Or whatever size you want to read in at a time.
            byte[] byteChunk = new byte[4096];
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}


