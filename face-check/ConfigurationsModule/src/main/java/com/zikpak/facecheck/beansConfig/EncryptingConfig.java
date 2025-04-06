package com.zikpak.facecheck.beansConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class EncryptingConfig {


    @Value("${application.security.encryption.aes-key}")
    private String secretKey;


    @Bean
    public SecretKey secretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, "AES");
    }


}
