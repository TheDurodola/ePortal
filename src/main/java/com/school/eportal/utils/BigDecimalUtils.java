package com.school.eportal.utils;

import java.math.BigDecimal;

public final class BigDecimalUtils {

    private BigDecimalUtils() {
    }

    public static boolean lesserThan(BigDecimal a, BigDecimal b) {
        validateNotNull(a, b);
        return a.compareTo(b) < 0;
    }

    public static boolean lesserThanOrEqualsTo(BigDecimal a, BigDecimal b) {
        validateNotNull(a, b);
        return a.compareTo(b) <= 0;
    }

    public static boolean greaterThan(BigDecimal a, BigDecimal b) {
        validateNotNull(a, b);
        return a.compareTo(b) > 0;
    }

    public static boolean greaterThanOrEqualsTo(BigDecimal a, BigDecimal b) {
        validateNotNull(a, b);
        return a.compareTo(b) >= 0;
    }

    private static void validateNotNull(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("BigDecimal arguments cannot be null");
        }
    }
}