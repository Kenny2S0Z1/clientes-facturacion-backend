package com.bolsadeideas.springboot.backend.apirest.auth;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    // Genera KeyPair RSA para firmar JWT
    public KeyPair keyPair() throws Exception {
        // Leer private key desde classpath
        ClassPathResource privateResource = new ClassPathResource("keys/private_key.pem");
        byte[] privateKeyBytes = privateResource.getInputStream().readAllBytes();
        String privateKeyContent = new String(privateKeyBytes)
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        // Leer public key desde classpath
        ClassPathResource publicResource = new ClassPathResource("keys/public_key.pem");
        byte[] publicKeyBytes = publicResource.getInputStream().readAllBytes();
        String publicKeyContent = new String(publicKeyBytes)
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(pubKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    // JWKSource usado por Authorization Server
    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID("clave_a_poner") //cualquier clave constante
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    // JwtDecoder compatible con RSA
    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    // JwtEncoder usando JWKSource RSA
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}

