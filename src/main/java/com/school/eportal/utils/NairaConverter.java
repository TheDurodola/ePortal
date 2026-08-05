package com.school.eportal.utils;

public class NairaConverter {

    public static long nairaToKobo(long naira) {
        return naira * 100;
    }

    public static long koboToNaira(long kobo) {
        return kobo / 100;
    }
}
