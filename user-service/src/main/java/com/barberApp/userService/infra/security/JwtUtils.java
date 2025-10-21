package com.barberApp.userService.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

@Service
public class JwtUtils {

    private final PublicKey publicKey;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public JwtUtils() throws Exception {
        this.publicKey = KeyLoader.loadPublicKey("public_key.pem");
    }

    /**
     * Validate a JWT token
     * @param token JWT token to validate
     * @return email (subject) from token
     */
    public String validate(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("API BarberApp")
                    .build();
            DecodedJWT jwt = verifier.verify(token);

            String subject = jwt.getSubject();
            if (subject == null) {
                throw new InvalidParameterException("Subject is missing in the token.");
            }

            return subject;

        } catch (Exception e) {
            logger.error("Failed to validate JWT", e);
            throw new InvalidParameterException("JWT validation failed: " + e.getMessage());
        }
    }

    public Boolean boolValidate(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("API BarberApp")
                    .build();
            DecodedJWT jwt = verifier.verify(token);

            if (jwt != null) {
                return true;
            }

        } catch (Exception e) {
            logger.error("Failed to validate JWT", e);
            throw new InvalidParameterException("JWT validation failed: " + e.getMessage());
        }
        return false;
    }

    public String retrieveToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) return null;
        return authorizationHeader.replace("Bearer ", "");
    }
}
