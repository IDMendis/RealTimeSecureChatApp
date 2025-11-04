package utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * MODULE 3: Encryption Module - Use Java's javax.crypto for AES encryption
 * Handles encryption and decryption of messages using AES algorithm
 * Team Member 3 should implement this class
 * 
 * Java Version: 17 LTS (Long Term Support)
 * Requires JDK 17 or higher
 */
public class EncryptionUtil {
    
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256; // 256-bit AES encryption
    
    /**
     * Generate a new AES encryption key
     * @return Generated SecretKey
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Failed to generate encryption key: " + e.getMessage());
            throw new RuntimeException("Encryption key generation failed", e);
        }
    }
    
    /**
     * Convert key to String for storage/transmission
     * @param key SecretKey to convert
     * @return Base64 encoded key string
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * Convert String back to SecretKey
     * @param keyStr Base64 encoded key string
     * @return SecretKey object
     */
    public static SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
    
    /**
     * Encrypt a message using AES
     * @param message Plain text message to encrypt
     * @param key SecretKey for encryption
     * @return Encrypted message as Base64 string
     */
    public static String encrypt(String message, SecretKey key) {
        try {
            // TODO: Full implementation by Team Member 3
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
            throw new RuntimeException("Message encryption failed", e);
        }
    }
    
    /**
     * Decrypt a message using AES
     * @param encryptedMessage Encrypted message as Base64 string
     * @param key SecretKey for decryption
     * @return Decrypted plain text message
     */
    public static String decrypt(String encryptedMessage, SecretKey key) {
        try {
            // TODO: Full implementation by Team Member 3
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.err.println("Decryption failed: " + e.getMessage());
            throw new RuntimeException("Message decryption failed", e);
        }
    }
    
    /**
     * Test the encryption/decryption functionality
     */
    public static void main(String[] args) {
        try {
            // Generate key
            SecretKey key = generateKey();
            System.out.println("Generated encryption key");
            
            // Test message
            String originalMessage = "Hello, this is a secure message!";
            System.out.println("Original message: " + originalMessage);
            
            // Encrypt
            String encrypted = encrypt(originalMessage, key);
            System.out.println("Encrypted message: " + encrypted);
            
            // Decrypt
            String decrypted = decrypt(encrypted, key);
            System.out.println("Decrypted message: " + decrypted);
            
            // Verify
            if (originalMessage.equals(decrypted)) {
                System.out.println("✅ Encryption/Decryption test PASSED");
            } else {
                System.err.println("❌ Encryption/Decryption test FAILED");
            }
            
        } catch (Exception e) {
            System.err.println("Encryption test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
