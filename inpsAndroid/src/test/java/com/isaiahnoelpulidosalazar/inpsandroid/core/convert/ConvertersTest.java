package com.isaiahnoelpulidosalazar.inpsandroid.core.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
  public void testBase64RoundTripAndNull() {
    assertNull(Converters.toBase64(null));

    String original = "inpsAndroid 3.0.0 \u2014 UTF-8 \u2713";
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
  public void testDateFormattingAndTimeZoneReset() {
    assertNull(Converters.formatDate(null, "yyyy-MM-dd"));
    assertNull(Converters.formatDate(new Date(), null));
    assertNull(Converters.toMonthDayYear(null));
    assertNull(Converters.toDayMonthYear(null));
    assertNull(Converters.toYearMonthDay(null));

    TimeZone previous = TimeZone.getDefault();
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      cal.set(2026, Calendar.MAY, 4, 23, 0, 0);
      Date date = cal.getTime();

      assertEquals("5/4/2026", Converters.toMonthDayYear(date));
      assertEquals("4/5/2026", Converters.toDayMonthYear(date));
      assertEquals("2026/5/4", Converters.toYearMonthDay(date));
      assertEquals("2026-05-04 23:00", Converters.formatDate(date, "yyyy-MM-dd HH:mm"));

      // Cached formatter picks up updated default time zone on the same thread
      TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo")); // UTC+9 -> next day 08:00
      assertEquals("2026-05-05 08:00", Converters.formatDate(date, "yyyy-MM-dd HH:mm"));
    } finally {
      TimeZone.setDefault(previous);
    }
  }
}
