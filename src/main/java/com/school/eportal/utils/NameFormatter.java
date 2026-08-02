package com.school.eportal.utils;

public class NameFormatter {


    public static String toProperCase(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }

        String trimmed = name.trim().replaceAll("\\s+", " "); // collapse extra spaces
        StringBuilder result = new StringBuilder(trimmed.length());
        boolean capitalizeNext = true;

        for (char c : trimmed.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
                // capitalize the next letter if this char is a boundary
                if (c == ' ' || c == '-' || c == '\'') {
                    capitalizeNext = true;
                }
            }
        }

        return result.toString();
    }


}