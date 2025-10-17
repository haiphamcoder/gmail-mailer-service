package io.github.haiphamcoder.mailer.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskingUtil {

    /**
     * Masks an email address by preserving minimal context and hiding the rest.
     * Strategy:
     * - Preserve 1st and last character of local-part when length >= 3
     * - When local-part length == 2, preserve 1st character
     * - When local-part length <= 1, fully mask local-part
     * - Domain is preserved as-is (use infrastructure logs to protect further if
     * needed)
     *
     * Examples:
     * - "john.doe@example.com" -> "j******e@example.com"
     * - "ab@example.com" -> "a*@example.com"
     * - "a@example.com" -> "*@example.com"
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            // Not a typical email format, fallback to generic mask
            return mask(email, 1, 0, '*');
        }
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex); // includes '@'

        String maskedLocal;
        if (local.length() >= 3) {
            maskedLocal = mask(local, 1, 1, '*');
        } else if (local.length() == 2) {
            maskedLocal = mask(local, 1, 0, '*');
        } else {
            maskedLocal = mask(local, 0, 0, '*');
        }
        return maskedLocal + domain;
    }

    /**
     * Masks the middle of a string, preserving a number of characters at the start
     * and end.
     * If the requested unmasked character count exceeds input length, the input is
     * returned unchanged.
     */
    public static String mask(String input, int unmaskedStart, int unmaskedEnd, char maskChar) {
        if (input == null) {
            return null;
        }
        if (input.isEmpty()) {
            return input;
        }
        int length = input.length();
        if (unmaskedStart < 0) {
            unmaskedStart = 0;
        }
        if (unmaskedEnd < 0) {
            unmaskedEnd = 0;
        }
        if (unmaskedStart + unmaskedEnd >= length) {
            return input;
        }
        String start = input.substring(0, unmaskedStart);
        String end = input.substring(length - unmaskedEnd);
        int maskLen = length - unmaskedStart - unmaskedEnd;
        String masked = String.valueOf(maskChar).repeat(maskLen);
        return start + masked + end;
    }
}
