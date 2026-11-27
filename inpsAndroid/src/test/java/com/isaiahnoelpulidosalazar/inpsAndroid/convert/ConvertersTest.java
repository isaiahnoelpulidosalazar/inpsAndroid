package com.isaiahnoelpulidosalazar.inpsAndroid.convert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class ConvertersTest {

    @Test
    public void testCapitalize() {
        assertNull(Converters.capitalize(null));
        assertEquals("", Converters.capitalize(""));
        assertEquals("Hello", Converters.capitalize("hello"));
        assertEquals("Already", Converters.capitalize("Already"));
    }

    @Test
    public void testBase64RoundTrip() {
        String original = "inpsAndroid 2.0.0 — UTF-8 ✓";
        String encoded = Converters.toBase64(original);
        assertEquals(-1, encoded.indexOf('\n'));
        assertEquals(original, Converters.fromBase64(encoded));

        assertNull(Converters.fromBase64(null));
        assertNull(Converters.fromBase64("a"));
    }

    @Test
    public void testNumericParsing() {
        assertEquals(42, Converters.toInt(" 42 "));
        assertEquals(0, Converters.toInt("abc"));
        assertEquals(-1, Converters.toInt(null, -1));

        assertEquals(3.5f, Converters.toFloat(" 3.5 "), 1e-6f);
        assertEquals(0f, Converters.toFloat("bad"), 1e-6f);
        assertEquals(9.9f, Converters.toFloat(null, 9.9f), 1e-6f);

        assertEquals(2.718, Converters.toDouble(" 2.718 "), 1e-9);
        assertEquals(0.0, Converters.toDouble("bad"), 1e-9);
        assertEquals(-5.5, Converters.toDouble(null, -5.5), 1e-9);
    }

    @Test
    public void testDateFormatting() {
        assertNull(Converters.formatDate(null, "yyyy-MM-dd"));
        assertNull(Converters.toMonthDayYear(null));
        assertNull(Converters.toDayMonthYear(null));
        assertNull(Converters.toYearMonthDay(null));

        TimeZone previous = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.set(2026, Calendar.MAY, 4, 12, 0, 0);
            Date date = cal.getTime();

            assertEquals("5/4/2026", Converters.toMonthDayYear(date));
            assertEquals("4/5/2026", Converters.toDayMonthYear(date));
            assertEquals("2026/5/4", Converters.toYearMonthDay(date));
            assertEquals("2026-05-04", Converters.formatDate(date, "yyyy-MM-dd"));
        } finally {
            TimeZone.setDefault(previous);
        }
    }
}
