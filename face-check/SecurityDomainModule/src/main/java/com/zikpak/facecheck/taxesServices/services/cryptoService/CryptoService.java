package com.zikpak.facecheck.taxesServices.services.cryptoService;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CryptoService {

    private static final String AES_TRANSFORM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_LEN = 12;
    private static final int KEY_VERSION = 1;

    private final SecretKeySpec aesKey;
    private final SecretKeySpec hmacKey;
    private final SecureRandom rnd = new SecureRandom();

    public CryptoService(
            @Value("${app.crypto.aesKeyBase64}") String aesKeyBase64,
            @Value("${app.crypto.hmacKeyBase64}") String hmacKeyBase64
    ) {
        this.aesKey = new SecretKeySpec(Base64.getDecoder().decode(aesKeyBase64), "AES");
        this.hmacKey = new SecretKeySpec(Base64.getDecoder().decode(hmacKeyBase64), "HmacSHA256");
    }

    /**
     * Зашифровать строку
     */
    public Sealed seal(String plain) {
        if (plain == null || plain.isBlank()) return null;

        try {
            byte[] iv = new byte[IV_LEN];
            rnd.nextBytes(iv);

            Cipher c = Cipher.getInstance(AES_TRANSFORM);
            c.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ct = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            byte[] h = mac.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            String last4 = extractLast4Digits(plain);
            return new Sealed(ct, iv, KEY_VERSION, h, last4);

        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    /**
     * Расшифровать данные
     */
    public String unseal(byte[] ciphertext, byte[] iv, Integer keyVersion) {
        if (ciphertext == null || iv == null || keyVersion == null) return null;

        // Проверяем версию ключа
        if (keyVersion != KEY_VERSION) {
            throw new IllegalStateException("Unsupported key version: " + keyVersion);
        }

        try {
            Cipher c = Cipher.getInstance(AES_TRANSFORM);
            c.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] plainBytes = c.doFinal(ciphertext);
            return new String(plainBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    /**
     * Проверить, что значение соответствует HMAC
     * Используется для поиска по зашифрованным полям
     */
    public boolean verifyHmac(String plainValue, byte[] storedHmac) {
        if (plainValue == null || storedHmac == null) return false;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            byte[] calculatedHmac = mac.doFinal(plainValue.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(calculatedHmac, storedHmac);

        } catch (Exception e) {
            throw new IllegalStateException("HMAC verification failed", e);
        }
    }

    /**
     * Вычислить HMAC для значения
     * Используется для поиска в БД по зашифрованным полям
     */
    public byte[] computeHmac(String value) {
        if (value == null || value.isBlank()) return null;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(hmacKey);
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new IllegalStateException("HMAC computation failed", e);
        }
    }

    private String extractLast4Digits(String s) {
        String digits = s.replaceAll("\\D", "");
        if (digits.length() < 4) return digits;
        return digits.substring(digits.length() - 4);
    }

    @Data
    @AllArgsConstructor
    public static class Sealed {
        private byte[] ciphertext;
        private byte[] iv;
        private int keyVersion;
        private byte[] hmac;
        private String last4;
    }
}