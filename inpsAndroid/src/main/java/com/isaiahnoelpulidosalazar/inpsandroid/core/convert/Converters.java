package com.isaiahnoelpulidosalazar.inpsandroid.core.convert;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Type and format conversions. Parsing failures return the supplied fallback rather than throwing.
 */
public final class Converters {

    private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMATS =
            ThreadLocal.withInitial(HashMap::new);

    private Converters() {
        throw new UnsupportedOperationException("No instances");
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

    /**
     * Encodes {@code str} as UTF-8 Base64 without line wrapping.
     *
     * @return Base64 string, or {@code null} if {@code str} is {@code null}
     */
    public static String toBase64(String str) {
        if (str == null) return null;
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
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static float toFloat(String str, float fallback) {
        if (str == null) return fallback;
        try {
            return Float.parseFloat(str.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static double toDouble(String str, double fallback) {
        if (str == null) return fallback;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static int toInt(String str) { return toInt(str, 0); }

    public static float toFloat(String str) { return toFloat(str, 0f); }

    public static double toDouble(String str) { return toDouble(str, 0.0); }

    /**
     * Formats {@code date} with {@code pattern} ({@link Locale#US}) in the current default time
     * zone using a per-thread cached formatter.
     *
     * @return formatted string, or {@code null} if {@code date} or {@code pattern} is {@code null}
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null || pattern == null) return null;
        Map<String, SimpleDateFormat> map = DATE_FORMATS.get();
        SimpleDateFormat sdf = map.get(pattern);
        if (sdf == null) {
            sdf = new SimpleDateFormat(pattern, Locale.US);
            map.put(pattern, sdf);
        }
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /** e.g. {@code 5/4/2026} (month/day/year, unpadded). */
    public static String toMonthDayYear(Date date) { return formatDate(date, "M/d/yyyy"); }

    /** e.g. {@code 4/5/2026} (day/month/year, unpadded). */
    public static String toDayMonthYear(Date date) { return formatDate(date, "d/M/yyyy"); }

    /** e.g. {@code 2026/5/4} (year/month/day, unpadded). */
    public static String toYearMonthDay(Date date) { return formatDate(date, "yyyy/M/d"); }
}
