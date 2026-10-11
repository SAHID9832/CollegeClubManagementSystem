package com.collegeclub.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Enterprise Cryptographic Password Hashing Utility.
 * Follows OWASP recommendations:
 * - PBKDF2WithHmacSHA256
 * - Cryptographically secure per-user salt (16 bytes via SecureRandom)
 * - Configurable iterations (Default: 600,000)
 * - Constant-time comparison (MessageDigest.isEqual) to prevent timing attacks
 * - Stored format: pbkdf2$<iterations>$<saltB64>$<hashB64>
 */
public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static int defaultIterations = 600_000;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static void setDefaultIterations(int iterations) {
        if (iterations < 1000) {
            throw new IllegalArgumentException("Iteration count must be at least 1,000.");
        }
        defaultIterations = iterations;
    }

    public static int getDefaultIterations() {
        return defaultIterations;
    }

    /**
     * Hashes a plaintext password using PBKDF2WithHmacSHA256 and default iterations.
     */
    public static String hashPassword(String password) {
        return hashPassword(password, defaultIterations);
    }

    /**
     * Hashes a plaintext password with a specified iteration count.
     */
    public static String hashPassword(String password, int iterations) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS);

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);

        return "pbkdf2$" + iterations + "$" + saltB64 + "$" + hashB64;
    }

    /**
     * Verifies a candidate password against an encoded hash.
     * Uses constant-time comparison to protect against side-channel timing attacks.
     */
    public static boolean verifyPassword(String candidatePassword, String storedHash) {
        if (candidatePassword == null || storedHash == null || !storedHash.startsWith("pbkdf2$")) {
            return false;
        }

        String[] parts = storedHash.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            byte[] actualHash = pbkdf2(candidatePassword.toCharArray(), salt, iterations, expectedHash.length * 8);

            return MessageDigest.isEqual(actualHash, expectedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Cryptographic algorithm error: " + e.getMessage(), e);
        }
    }
}
