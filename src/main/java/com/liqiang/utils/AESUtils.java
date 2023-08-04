package com.liqiang.utils;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES加解密
 *
 */
@Slf4j
public class AESUtils {
    public static final String CHAOSUKEY = "lijiong011652123";

    /**
     * AES加密+Base64转码
     *
     * @param data 明文（16进制）
     * @param key  密钥
     * @return
     */
    public static String encrypt(String data, String key) {
        byte[] keyb = null;
        try {
            keyb = key.getBytes("utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } // 明文
        SecretKeySpec sKeySpec = new SecretKeySpec(keyb, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] bjiamihou = null;
        String miwen = "";
        try {
            bjiamihou = cipher.doFinal(data.getBytes("utf-8"));
            // byte加密后
            miwen = Base64.encodeBase64String(bjiamihou);// 密文用base64加密
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return miwen;
    }

    /**
     * Base64解码 + AES解码
     *
     * @param data 密文 （16进制）
     * @param key  密钥
     * @return
     */
    public static String decrypt(String data, String key){
        byte[] keyb = null;
        try {
            keyb = key.getBytes("utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        byte[] miwen = Base64.decodeBase64(data);
        miwen = new byte[]{-101, 57, 57, -128, -61, 107, -109, -65, 50, 88, 114, 127, -10, 103, 92, -46, 9, -3, -45, -74, -125, 2, -70, -6, -87, -57, 88, -6, 88, -46, 125, -37, -35, -2, 13, -59, -94, -11, 71, -40, 58, -6, 79, 59, -20, 79, -91, -28, -27, -84, -119, -99, 44, 116, -50, 47, -55, 106, 116, 43, 25, 113, -118, -126, 61, 97, 48, 24, -115, 82, -82, 39, 58, -1, -101, 22, 41, -120, -33, 106, -125, -107, -5, -63, -26, 26, 20, -117, 14, 32, 8, 5, -8, 13, -98, -35, -102, -5, -98, 5, 94, -28, 62, 119, 110, 42, -126, -62, 97, 76, -99, -81, -35, -85, 22, -112, -94, 125, 74, 56, 40, -23, 24, -81, 22, -46, -12, -98, -126, 37, 70, 86, -123, 122, -6, -106, -32, 50, 39, -22, -112, -112, -69, 123, -3, -119, 70, 3, 82, -38, -21, 78, 80, 7, -61, 40, 114, 123, -12, -78, -53, -49, -74, 67, 74, -36, -93, 93, 80, -23, 92, 28, 3, 79, -22, 84, 95, -43, -54, -96, -82, 46, -107, -36, -24, 105, -13, -32, 57, 16, -63, 106, -16, 0, 80, -117, 56, -76, -79, 63, -26, 48, 33, -59, 57, -47, -43, 60, -70, -15, 86, -20, 41, 30, -81, 69, 97, -70, -54, -40, 86, -8, -98, -17, 9, -122, 91, -124, 81, 58, 59, -72, -9, -16, -68, 72, 3, 23, -39, -85, -76, -23, 72, 96, 98, 82, 113, 119, 62, -63, -121, -94, -24, -82, -119, -65, 7, 61, 42, -10, 73, -24, 0, 23, -67, -9, -94, -78, 60, -98, -1, 76, 12, 114, 27, -87, -46, -17, -121, -122, -74, -24, -114, -79, -35, 67, -12, 18, -33, 73, 118, -119, 6, 93, 50, 9, -65, 54, 26, 58, 78, 2, -36, 112, 70, 97, -82, 42, 59, 41, 21, 85, 104, -2, 111, 77, 74, -38, 16, -88, 59, 66, 122, -72, -62, 34, -71, -109, 37, -72, 40, 114, 116, 90, 100, 21, -96, 34, 111, 32, -8, -84, -6, -82, 75, -78, -24, -115, 48, 29, -127, -89, 13, -89, -107, -72, 32, 115, 107, -36, 71, 113, -48, 89, 73, -22, -8, 28, -63, -102, -73, 23, 35, 107, -23, -20, -99, -63, -54, 31, 42, 44, 53, 81, -10, -17, -124, 112, -71, -63, -38, -45, 100, 9, -105, 23, -42, -93, -59, -20, 60, 27, 23, 82, -5, -31, -122, 72, 38, 22, 15, 14, -122, 55, -22, -106, 35, -80, 39, -96, -79, -68, 107, -24, 19, 93, 87, -45, 108, 8, -33, 5, 62, -34, 113, 117, -47, 113, 72, -85, -16, -36, -60, 96, 0, -119, 95, 52, 39, -73, -111, -70, 77, 47, -30, 105, 60, -98, -73, 38, -36, -56, 30, -99, -94, 93, -95, -115, -128, 68, 17, -91, -111, -106, 91, -108, 32, -79, -89, 33, 88, 4, 96, -28, 110, 119, 72, 74, 72, -24, 107, 64, 28, -103, 7, -7, -128, -38, -10, -7, -24, 73, 5, 33, -10, -120, 106, 37, 100, 123, -39, -115, 8, -74, -123, -89, 66, 37, 64, -31, -3, 85, 77, 112, -122, -49, -40, -51, -93, -22, -100, -74, 0, -109, -54, -51, 1, 54, 59, -59, -74, -34, 74, -46, -111, -17, 111, -59, 4, -112, -99, -54, -59, 64, 51, -26, -117, 42, 123, 68, 119, 106, 21, 101, 46, -37, 29, -85, 94, -126, -16, -114, 0, -80, -25, -8, 70, 32, 22, -39, 69, 27, 76, 28, -67, 8, -48, 101, 72, -57, 64, 43, 0, 100, -34, -107, -54, -121, -16, 98, 122, -15, 83, -56, -4, -70, -41, -103, 17, -29, 26, -101, 10, -11, 20, 82, 26, 36, 5, 107, -44, -3, 20, 23, -6, -66, -97, -41, -18, 30, -110, 56, -128, 50, -20, 23, -32, -72, 41, -78, -99, 7, -38, 76, -119, 94, -13, 124, -17, 78, 117, -30, 63, -68, -127, -69, 71, 120, -48, 70, -17, 39, -89, -38, 5, 9, 14, -113, -17, 66, -11, -17, -25, -126, 27, -97, -24, -4, 69, -115, -105, 44, 33, -121, -110, -56, 31, 79, -22, -108, -3, -62, 8, -83, -68, 22, -87, -107, -87, 87, -12, -58, -99, 42, 86, 94, -127, 19, -112, -85, 104, 29, 78, 19, -48, -49, -32, 17, 49, 18, 21, 29, 77, 114, 43, 91, 52, -64, 71, 34, 57, 127, -85, -108, -92, 107, 98, 34, 38, -15, 123, 35, -33, 35, 25, -6, 78, 65, 59, -49, 89, -9, -70, -9, -64, 13, 5, -14, 107, 90, -22, -55, 31, 55, 55, -41, -20, -47, 120, 27, 61, 126, -67, 126, -81, 50, -76, 19, 85, -93, 95, 121, -123, -99, -106, -4, 100, 37, 96, 75, -97, -123, 84, -121, -35, -23, 56, -102, -43, -22, -120, -15, -72, -24, 123, 27, -30, -33, 61, -53, 70, -103, -109, -88, 107, 95, 29, -106, -4, -59, 101, 97, 113, 9, -26, -115, -51, 71, 30, -120, 28, -105, 123, 24, 19, -43, -125, 106, -28, 99, 27, 10, 105, -9, 85, -83, -6, 44, 89, 48, 15, 29, 18, -18, 80, -122, -47, 21, -39, -29, -75, -32, 97, -126, -34, 127, 59, -73, 35, -18, 96, -127, -28, -70, 34, 13, 125, 7, -84, 46, 7, -119, 107, 67, -9, -88, -105, 11, 5, 83, 107, -112, -55, -71, -65, -121, 24, -92, -16, 31, -7, -98, 81, -2, 75, 20, 126, 75, -89, 91, -97, -90, -8, -30, 84, 122, 40, -114, 13, -125, 79, 47, -31, 6, 34, 55, 1, -81, 110, 124, -81, -68, 93, 111, 54, -125, -112, 86, 18, -63, -57, -3, -4, -82, 63, -88, -76, -98, -40, 17, 42, 126, 25, 51, 117, -66, 22, 90, 10, 120};
        SecretKeySpec sKeySpec = new SecretKeySpec(keyb, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ByteUtil.hexToByteArray("0102030405060708090a0b0c0d0e0f10")); // 初始化向量
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, ivParameterSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        byte[] bjiemihou = null;
        String mingwen = "";
        try {
            bjiemihou = cipher.doFinal(miwen);
            // byte加密后
            mingwen = new String(bjiemihou,"utf-8");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mingwen;
    }

    public static void main(String[] args) throws Exception {
        // 测试加密工具类
        String key = "0102030405060708090a0b0c0d0e0f10";//自定义16位字符串即可
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<root>\n" +
                "  <common>\n" +
                "    <building_id>估计</building_id>\n" +
                "    <gateway_id>01</gateway_id>\n" +
                "    <type>md5</type>\n" +
                "  </common>";//明文
        String dataq = new String(data.getBytes(), "GB2312");

//        String miwen = AESUtil.encrypt(data);// 加密
        AESUtil.decrypt(AESUtil.encrypt(data));
        System.out.println(AESUtil.decrypt(AESUtil.encrypt(dataq)));// 解密

    }
}
