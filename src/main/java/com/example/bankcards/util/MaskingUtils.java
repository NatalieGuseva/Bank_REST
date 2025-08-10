package com.example.bankcards.util;

public class MaskingUtils {

    // Маскирование email (пример: e***@mail.com)
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        if (parts[0].length() <= 1) return "***@" + parts[1];
        return parts[0].charAt(0) + "***@" + parts[1];
    }

    // Маскирование телефона (пример: +7***1234)
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return phone;
        return phone.substring(0, Math.max(0, phone.length() - 4)).replaceAll(".", "*") +
                phone.substring(phone.length() - 4);
    }

    // Маскирование банковской карты (пример: 4111********1111)
    public static String maskCard(String card) {
        if (card == null || card.length() < 8) return card;
        return card.substring(0, 4) + "********" + card.substring(card.length() - 4);
    }
}