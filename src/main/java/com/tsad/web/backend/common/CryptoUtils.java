package com.tsad.web.backend.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CryptoUtils {
    private static final Logger log = LoggerFactory.getLogger(CryptoUtils.class);

    public String hashSHA384(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] encodedHash = digest.digest(input.getBytes());
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            log.error("hashSHA384() ... Error generating SHA-384 hash");
            throw new RuntimeException("hashSHA384() ... Error generating SHA-384 hash", e);
        }
    }

    public String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            log.error("hashSHA256() ... Error generating SHA-256 hash");
            throw new RuntimeException("hashSHA256() ... Error generating SHA-256 hash", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
