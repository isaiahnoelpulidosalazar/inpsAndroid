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
 * Null-safe string, Base64, numeric, and date conversion utilities.
 *
 * <p>Numeric and Base64 decoding helpers return a safe fallback value rather than throwing on
 * malformed or {@code null} inputs.
 */
public final class Converters {

  /**
   * Per-thread formatter cache keyed by pattern; avoids SimpleDateFormat synchronization
   * contention.
   */
  private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMATS =
      ThreadLocal.withInitial(HashMap::new);

  private Converters() {
    throw new UnsupportedOperationException("No instances");
  }

  /**
   * Upper-cases the first Unicode code point of {@code str}.
   *
   * @param str input text, may be {@code null} or empty
   * @return string with its leading code point upper-cased, or {@code str} unchanged when
   *     null/empty
   */
  public static String capitalize(String str) {
    if (str == null || str.isEmpty()) return str;
    int first = str.codePointAt(0);
    return new StringBuilder(str.length())
        .appendCodePoint(Character.toUpperCase(first))
        .append(str, Character.charCount(first), str.length())
        .toString();
  }

  /**
   * Encodes {@code str} as UTF-8 Base64 without line wrapping ({@link Base64#NO_WRAP}).
   *
   * @param str plain text to encode, may be {@code null}
   * @return Base64-encoded string, or {@code null} if {@code str} is {@code null}
   */
  public static String toBase64(String str) {
    if (str == null) return null;
    return Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
  }

  /**
   * Decodes a Base64 string into UTF-8 text.
   *
   * @param str Base64-encoded payload, may be {@code null}
   * @return decoded UTF-8 string, or {@code null} if {@code str} is {@code null} or invalid Base64
   */
  public static String fromBase64(String str) {
    if (str == null) return null;
    try {
      return new String(Base64.decode(str, Base64.DEFAULT), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Parses a trimmed decimal string to {@code int}, returning {@code fallback} on failure.
   *
   * @param str string to parse, may be {@code null}
   * @param fallback value returned when parsing fails
   * @return parsed integer or {@code fallback}
   */
  public static int toInt(String str, int fallback) {
    if (str == null) return fallback;
    try {
      return Integer.parseInt(str.trim());
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  /**
   * Parses a trimmed numeric string to {@code float}, returning {@code fallback} on failure.
   *
   * @param str string to parse, may be {@code null}
   * @param fallback value returned when parsing fails
   * @return parsed float or {@code fallback}
   */
  public static float toFloat(String str, float fallback) {
    if (str == null) return fallback;
    try {
      return Float.parseFloat(str.trim());
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  /**
   * Parses a trimmed numeric string to {@code double}, returning {@code fallback} on failure.
   *
   * @param str string to parse, may be {@code null}
   * @param fallback value returned when parsing fails
   * @return parsed double or {@code fallback}
   */
  public static double toDouble(String str, double fallback) {
    if (str == null) return fallback;
    try {
      return Double.parseDouble(str.trim());
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  /**
   * Parses a trimmed string to {@code int}, defaulting to {@code 0} on invalid input.
   *
   * @param str string to parse, may be {@code null}
   * @return parsed integer or {@code 0}
   */
  public static int toInt(String str) {
    return toInt(str, 0);
  }

  /**
   * Parses a trimmed string to {@code float}, defaulting to {@code 0f} on invalid input.
   *
   * @param str string to parse, may be {@code null}
   * @return parsed float or {@code 0f}
   */
  public static float toFloat(String str) {
    return toFloat(str, 0f);
  }

  /**
   * Parses a trimmed string to {@code double}, defaulting to {@code 0.0} on invalid input.
   *
   * @param str string to parse, may be {@code null}
   * @return parsed double or {@code 0.0}
   */
  public static double toDouble(String str) {
    return toDouble(str, 0.0);
  }

  /**
   * Formats {@code date} using {@code pattern} ({@link Locale#US}) in the current default time
   * zone, caching formatters per thread and pattern.
   *
   * @param date date instant to format, may be {@code null}
   * @param pattern {@link SimpleDateFormat} pattern, may be {@code null}
   * @return formatted date string, or {@code null} if {@code date} or {@code pattern} is {@code
   *     null}
   */
  public static String formatDate(Date date, String pattern) {
    if (date == null || pattern == null) return null;
    Map<String, SimpleDateFormat> map = DATE_FORMATS.get();
    SimpleDateFormat sdf = map.get(pattern);
    if (sdf == null) {
      sdf = new SimpleDateFormat(pattern, Locale.US);
      map.put(pattern, sdf);
    }
    // Refresh timezone per call in case TimeZone.setDefault() changed after caching.
    sdf.setTimeZone(TimeZone.getDefault());
    return sdf.format(date);
  }

  /**
   * Formats {@code date} as unpadded {@code M/d/yyyy} (e.g. {@code 5/4/2026}).
   *
   * @param date date instant to format, may be {@code null}
   * @return formatted date string, or {@code null} if {@code date} is {@code null}
   */
  public static String toMonthDayYear(Date date) {
    return formatDate(date, "M/d/yyyy");
  }

  /**
   * Formats {@code date} as unpadded {@code d/M/yyyy} (e.g. {@code 4/5/2026}).
   *
   * @param date date instant to format, may be {@code null}
   * @return formatted date string, or {@code null} if {@code date} is {@code null}
   */
  public static String toDayMonthYear(Date date) {
    return formatDate(date, "d/M/yyyy");
  }

  /**
   * Formats {@code date} as unpadded {@code yyyy/M/d} (e.g. {@code 2026/5/4}).
   *
   * @param date date instant to format, may be {@code null}
   * @return formatted date string, or {@code null} if {@code date} is {@code null}
   */
  public static String toYearMonthDay(Date date) {
    return formatDate(date, "yyyy/M/d");
  }
}
