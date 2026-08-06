package com.school.eportal.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class NairaConverter {

    public static long nairaToKobo(BigDecimal naira) {
        return naira.multiply(BigDecimal.valueOf(100)).longValue();
    }

    public static BigDecimal koboToNaira(long kobo) {
        return BigDecimal.valueOf(kobo).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
