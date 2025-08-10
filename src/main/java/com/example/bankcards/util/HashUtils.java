package com.example.bankcards.util;

import java.security.MessageDigest;
import java.util.Base64;

public class HashUtils {

    // SHA-256 хэширование строки
    public static String sha256(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash);
    }
}