package com.barberApp.userService.infra.security;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    public static PublicKey loadPublicKey(String filename) throws Exception {
            try {
                Resource res = new ClassPathResource(filename);
                try (InputStream is = res.getInputStream()) {
                    String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    String content = pem.replaceAll("-----BEGIN (.*)-----", "")
                            .replaceAll("-----END (.*)-----", "")
                            .replaceAll("\\s", "");
                    byte[] keyBytes = Base64.getDecoder().decode(content);
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                    return KeyFactory.getInstance("RSA").generatePublic(spec);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load public key from classpath: " + filename, e);
            }
    }

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filename)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
