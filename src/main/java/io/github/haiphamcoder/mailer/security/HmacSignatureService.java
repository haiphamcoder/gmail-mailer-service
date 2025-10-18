package io.github.haiphamcoder.mailer.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for generating and verifying HMAC-SHA512 signatures for API authentication.
 * <p>
 * This service provides methods to:
 * <ul>
 *   <li>Generate HMAC signatures using timestamp + project token</li>
 *   <li>Verify incoming request signatures</li>
 *   <li>Validate timestamp to prevent replay attacks</li>
 * </ul>
 * <p>
 * The signature format follows the pattern:
 * <pre>
 * signature = HMAC-SHA512(timestamp + project_token, secret_key)
 * </pre>
 * <p>
 * Security features:
 * - Timestamp validation prevents replay attacks (default: 5 minutes tolerance)
 * - HMAC-SHA512 provides strong cryptographic authentication
 * - Secret key should be stored securely (environment variables, secret managers)
 */
@Service
@Slf4j
public class HmacSignatureService {

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final long DEFAULT_TIMESTAMP_TOLERANCE_SECONDS = 300; // 5 minutes

    /**
     * Generates an HMAC-SHA512 signature for the given timestamp and project token.
     *
     * @param timestamp    the current timestamp in milliseconds
     * @param projectToken the project token
     * @param secretKey    the secret key for HMAC generation
     * @return the generated signature as a hexadecimal string
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public String generateSignature(long timestamp, String projectToken, String secretKey) {
        if (projectToken == null || projectToken.isBlank()) {
            throw new IllegalArgumentException("Project token cannot be null or empty");
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }

        try {
            String data = timestamp + projectToken;
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
            mac.init(secretKeySpec);
            
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate HMAC signature", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    /**
     * Verifies an HMAC signature against the provided parameters.
     *
     * @param timestamp    the timestamp from the request
     * @param projectToken the project token
     * @param secretKey    the secret key for verification
     * @param providedSignature the signature provided in the request
     * @return true if the signature is valid, false otherwise
     */
    public boolean verifySignature(long timestamp, String projectToken, String secretKey, String providedSignature) {
        if (providedSignature == null || providedSignature.isBlank()) {
            log.warn("Provided signature is null or empty");
            return false;
        }

        try {
            String expectedSignature = generateSignature(timestamp, projectToken, secretKey);
            return MessageDigest.isEqual(
                providedSignature.getBytes(StandardCharsets.UTF_8),
                expectedSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for HMAC signature verification", e);
            return false;
        } catch (Exception e) {
            log.error("Failed to verify HMAC signature", e);
            return false;
        }
    }

    /**
     * Validates that the timestamp is within the acceptable tolerance window.
     * This prevents replay attacks by ensuring requests are not too old.
     *
     * @param timestamp the timestamp to validate (in milliseconds)
     * @param toleranceSeconds the tolerance window in seconds (default: 5 minutes)
     * @return true if the timestamp is valid, false otherwise
     */
    public boolean isTimestampValid(long timestamp, long toleranceSeconds) {
        long currentTime = Instant.now().toEpochMilli();
        long timestampTime = timestamp;
        long difference = Math.abs(currentTime - timestampTime);
        
        boolean isValid = difference <= (toleranceSeconds * 1000);
        
        if (!isValid) {
            log.warn("Timestamp validation failed. Current: {}, Provided: {}, Difference: {}ms", 
                currentTime, timestampTime, difference);
        }
        
        return isValid;
    }

    /**
     * Validates timestamp with default tolerance (5 minutes).
     *
     * @param timestamp the timestamp to validate
     * @return true if the timestamp is valid, false otherwise
     */
    public boolean isTimestampValid(long timestamp) {
        return isTimestampValid(timestamp, DEFAULT_TIMESTAMP_TOLERANCE_SECONDS);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes the byte array to convert
     * @return the hexadecimal representation
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
