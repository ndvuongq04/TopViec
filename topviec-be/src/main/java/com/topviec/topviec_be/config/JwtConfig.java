package com.topviec.topviec_be.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.topviec.topviec_be.security.JwtProperties;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.util.Base64;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    // private SecretKey getSecretKey() {
    // return new SecretKeySpec(
    // jwtProperties.getSecret().getBytes(),
    // "HmacSHA256");
    // }
    private SecretKey getSecretKey() {
        /*
         * chuyển đổi jwtKey từ String sang mảng bit ( security ko làm việc với String)
         * hàm này sinh ra vì jwtDecoder và jwtEncoder cần jwtKey dưới dạng mảng bit
         */
        byte[] keyBytes = Base64.from(jwtProperties.getSecret()).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    // Dùng để TẠO token
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    // Dùng để VALIDATE và PARSE token
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
    }
}