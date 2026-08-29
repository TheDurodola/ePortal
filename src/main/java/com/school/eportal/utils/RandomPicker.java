package com.school.eportal.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class RandomPicker {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String source = "0123456789";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomAlphanumeric() {
        return RANDOM.ints(7, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String generateSixRandomNumber() {
        StringBuilder result = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(source.length());
            result.append(source.charAt(index));
        }
        return result.toString();
    }


    public static String pickSixWithoutRepetition() {
        char[] chars = source.toCharArray();
        // Fisher-Yates partial shuffle, only 6 swaps needed
        for (int i = 0; i < 6; i++) {
            int j = i + RANDOM.nextInt(chars.length - i);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars, 0, 6);
    }

}