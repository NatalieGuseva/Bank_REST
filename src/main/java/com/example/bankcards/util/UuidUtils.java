package com.example.bankcards.util;

import java.util.UUID;

public class UuidUtils {

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}