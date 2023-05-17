package com.liqiang.utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * AES加密解密工具类
 */
@Slf4j
public class AESUtil {
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private static final String defaultCharset = "UTF-8";
    private static final String KEY_AES = "AES";
    private static final String KEY = "0102030405060708090a0b0c0d0e0f10";

    private static final String IV = "0102030405060708090a0b0c0d0e0f10";


    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static final String DECRIPT_TRANSFORMATION = "AES/CBC/NoPadding";

    private static final String PADDING = "Zeros";

    public static byte[] encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(ByteUtil.hexToByteArray(KEY), ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ByteUtil.hexToByteArray(IV)); // 初始化向量

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

//        return Base64.getEncoder().encodeToString(encryptedBytes);
        return encryptedBytes;
    }

    public static String decrypt(byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance(DECRIPT_TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(ByteUtil.hexToByteArray(KEY), ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ByteUtil.hexToByteArray(IV)); // 初始化向量

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
//        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return
     */
//    public static String encrypt(String data, String key) {
//        return doAES(data, key, Cipher.ENCRYPT_MODE);
//    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
//    public static String encrypt(String data) {
//        return encrypt(data, AESUtil.KEY);
//    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return
     */
//    public static String decrypt(String data, String key) {
//        return doAES(data, key, Cipher.DECRYPT_MODE);
//    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
//    public static String decrypt(String data) {
//        return decrypt(data, AESUtil.KEY);
//    }

    /**
     * 加解密
     *
     * @param data 待处理数据
     * @param key  密钥
     * @param mode 加解密mode
     * @return
     */
    private static String doAES(String data, String key, int mode) {
        try {
            if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                return null;
            }
            //判断是加密还是解密
            boolean encrypt = mode == Cipher.ENCRYPT_MODE;
            byte[] content;
            //true 加密内容 false 解密内容
            if (encrypt) {
                content = data.getBytes(defaultCharset);
            } else {
                content = parseHexStr2Byte(data);
            }
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            kgen.init(128, new SecureRandom(key.getBytes()));
            //3.产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance(KEY_AES);// 创建密码器
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(mode, keySpec);// 初始化
            byte[] result = cipher.doFinal(content);
            if (encrypt) {
                //将二进制转换成16进制
                return parseByte2HexStr(result);
            } else {
                return new String(result, defaultCharset);
            }
        } catch (Exception e) {
            logger.error("AES 密文处理异常", e);
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
//        String content = "FC6288AA1F54EC3E0055F80123528C7E5A";
//        System.out.println("加密前：" + content);
//        System.out.println("加密密钥和解密密钥：" + KEY);
//        String encrypt = encrypt(content, KEY);
//        System.out.println("加密后：" + encrypt);
//        String decrypt = decrypt(encrypt, KEY);
//        System.out.println("解密后：" + decrypt);

        try {
            String plaintext = "Hello, World!";
            String key = "0123456789abcdef"; // 128-bit 密钥
//            key = "Kp9zhJzW!9y$$IV@";

//            String encryptedText = encrypt(plaintext);
//            System.out.println("Encrypted: " + encryptedText);
//            encryptedText = new String(encryptedText.getBytes(), "UTF-8");
////            encryptedText = "cOsHOooMdwJQl2s1UG5D9MgyLcNDcXT2Vl+Q6kcvOY3XL2QewEIceJYmer+yYpirbY5FwGKHFohdwZpVRDnwa42tMcXsA2FAfDlru9T9mgGDLcnqi6BdUtm7B8CeOZFkQoIpfKNPj9exXEYXJO+SfoVqdk5B7KCZHfIwlgFCOMkB2DxcnvKSdlLScim2EXO8TKwaciyhv5yRlKMvXGT1Dj8rypKlyK9/gHDOMyR8m0rnL97eezel2pDZ2s";
//            String decryptedText = decrypt(encryptedText);
//            System.out.println("Decrypted: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}