package com.hsbc.user.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 用来生成加密token
 */
public class TokenGenerator {

    private TokenGenerator() {

    }

    public static SecretKey generateSecretKey(String secretKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom(secretKey.getBytes(StandardCharsets.UTF_8));
        keyGenerator.init(128, secureRandom);
        return keyGenerator.generateKey();
    }

    public static String encryptToken(String tokenData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(tokenData.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptToken(String encryptedToken, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
