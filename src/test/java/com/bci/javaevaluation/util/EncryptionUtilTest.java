package com.bci.javaevaluation.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EncryptionUtilTest {

    private EncryptionUtil encryptionUtil;

    @BeforeEach
    public void setUp() {
        encryptionUtil = new EncryptionUtil();
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        String originalText = "Hello, World!";
        String encryptedText = encryptionUtil.encrypt(originalText);
        String decryptedText = encryptionUtil.decrypt(encryptedText);
        assertEquals(originalText, decryptedText, "The decrypted text should match the original text.");
    }

    @Test
    public void testEncryptInvalidData() {
        assertThrows(Exception.class, () -> {
            encryptionUtil.encrypt(null);
        }, "Encrypting null text should throw an exception.");
    }

    @Test
    public void testDecryptInvalidData() {
        assertThrows(Exception.class, () -> {
            encryptionUtil.decrypt(null);
        }, "Decrypting null text should throw an exception.");
    }

    @Test
    public void testDecryptInvalidEncryptedText() {
        assertThrows(Exception.class, () -> {
            encryptionUtil.decrypt("invalidEncryptedText");
        }, "Decrypting invalid encrypted text should throw an exception.");
    }
}
