package io.github.haiphamcoder.mailer.security;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Utility class for testing HMAC signature generation.
 * <p>
 * This utility provides methods to generate test signatures for development
 * and testing purposes. It should not be used in production code.
 * <p>
 * Example usage in Postman or curl:
 * <pre>
 * // JavaScript (Postman Pre-request Script)
 * const CryptoJS = require('crypto-js');
 * pm.environment.set("access_key", "L8YfR3ARc5058...");
 * pm.environment.set("secret_key", "134999f18f0ec5f0d2ad0...");
 * pm.environment.set("project_token", "7137837...");
 * pm.environment.set("timestamp", Date.now());
 * 
 * let signature = pm.environment.get("timestamp") + pm.environment.get("project_token");
 * let access_sign = CryptoJS.HmacSHA512(signature, pm.environment.get("secret_key")).toString();
 * pm.environment.set("access_sign", access_sign);
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class HmacTestUtil {

    private final HmacSignatureService hmacService;

    /**
     * Generates a test signature for the given parameters.
     * This method is intended for development and testing only.
     *
     * @param timestamp the timestamp in milliseconds
     * @param projectToken the project token
     * @param secretKey the secret key
     * @return the generated signature
     */
    public String generateTestSignature(long timestamp, String projectToken, String secretKey) {
        return hmacService.generateSignature(timestamp, projectToken, secretKey);
    }

    /**
     * Generates a test signature using current timestamp.
     *
     * @param projectToken the project token
     * @param secretKey the secret key
     * @return the generated signature
     */
    public String generateTestSignatureNow(String projectToken, String secretKey) {
        return hmacService.generateSignature(System.currentTimeMillis(), projectToken, secretKey);
    }

    /**
     * Validates a test signature.
     *
     * @param timestamp the timestamp
     * @param projectToken the project token
     * @param secretKey the secret key
     * @param signature the signature to validate
     * @return true if the signature is valid
     */
    public boolean validateTestSignature(long timestamp, String projectToken, String secretKey, String signature) {
        return hmacService.verifySignature(timestamp, projectToken, secretKey, signature);
    }
}
