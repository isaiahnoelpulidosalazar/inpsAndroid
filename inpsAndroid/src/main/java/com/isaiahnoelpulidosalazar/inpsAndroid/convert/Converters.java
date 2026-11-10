package com.isaiahnoelpulidosalazar.inpsAndroid.convert;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Type and format conversions. Nothing here prints stack traces; parsing failures return the
 * supplied fallback instead.
 */
public final class Converters {

    private Converters() {
        throw new UnsupportedOperationException("Converters cannot be instantiated");
    }

    /** Upper-cases the first character. {@code null} and empty strings are returned unchanged. */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        int first = str.codePointAt(0);
        return new StringBuilder(str.length())
                .appendCodePoint(Character.toUpperCase(first))
                .append(str, Character.charCount(first), str.length())
                .toString();
    }

    /** UTF-8 Base64 without line wrapping (v1 used {@code Base64.DEFAULT}, which appends "\n"). */
    public static String toBase64(String str) {
        return Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    /** @return the decoded UTF-8 string, or {@code null} if {@code str} is null or not valid Base64. */
    public static String fromBase64(String str) {
        if (str == null) return null;
        try {
            return new String(Base64.decode(str, Base64.DEFAULT), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int toInt(String str, int fallback) {
        if (str == null) return fallback;
        try { return Integer.parseInt(str.trim()); } catch (NumberFormatException e) { return fallback; }
    }

    public static float toFloat(String str, float fallback) {
        if (str == null) return fallback;
        try { return Float.parseFloat(str.trim()); } catch (NumberFormatException e) { return fallback; }
    }

    public static double toDouble(String str, double fallback) {
        if (str == null) return fallback;
        try { return Double.parseDouble(str.trim()); } catch (NumberFormatException e) { return fallback; }
    }

    public static int toInt(String str) { return toInt(str, 0); }
    public static float toFloat(String str) { return toFloat(str, 0f); }
    public static double toDouble(String str) { return toDouble(str, 0.0); }

    /** Formats {@code date} with {@code pattern} in the device time zone; a {@code null} date gives {@code null}. */
    public static String formatDate(Date date, String pattern) {
        if (date == null) return null;
        // SimpleDateFormat is not thread-safe, so a fresh instance is used per call.
        return new SimpleDateFormat(pattern, Locale.US).format(date);
    }

    /** e.g. {@code 5/4/2026} (month/day/year, unpadded). */
    public static String toMonthDayYear(Date date) { return formatDate(date, "M/d/yyyy"); }

    /** e.g. {@code 4/5/2026} (day/month/year, unpadded). */
    public static String toDayMonthYear(Date date) { return formatDate(date, "d/M/yyyy"); }

    /** e.g. {@code 2026/5/4} (year/month/day, unpadded). */
    public static String toYearMonthDay(Date date) { return formatDate(date, "yyyy/M/d"); }
}
