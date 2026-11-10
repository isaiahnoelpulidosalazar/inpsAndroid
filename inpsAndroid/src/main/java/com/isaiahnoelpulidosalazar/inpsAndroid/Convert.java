package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.util.Base64;

import com.isaiahnoelpulidosalazar.inpsAndroid.convert.Converters;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Legacy conversion helpers, kept as thin delegating wrappers.
 *
 * @deprecated Since 2.0.0. Use {@link Converters}. See {@code migrating_to_v2.md}.
 */
@Deprecated
public class Convert {

    public static String toRealName(String str) { return Converters.capitalize(str); }

    /** Legacy output keeps {@link Base64#DEFAULT} (trailing newline). {@link Converters#toBase64} does not. */
    public static String toBase64(String str) {
        return Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
    }

    public static String fromBase64(String str) { return Converters.fromBase64(str); }

    public static String dateToMMDDYY(Date date) { return Converters.toMonthDayYear(date); }

    public static String dateToDDMMYY(Date date) { return Converters.toDayMonthYear(date); }

    public static String dateToYYMMDD(Date date) { return Converters.toYearMonthDay(date); }

    public static int toInt(String str) { return Converters.toInt(str); }

    public static float toFloat(String str) { return Converters.toFloat(str); }

    public static double toDouble(String str) { return Converters.toDouble(str); }

    public static String toString(Object obj) { return String.valueOf(obj); }
}
