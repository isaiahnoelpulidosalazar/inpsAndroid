package com.isaiahnoelpulidosalazar.inpsAndroid;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Date;

/** The v1 test-suite, unchanged in intent: proves the deprecated wrappers still behave as in v1. */
@SuppressWarnings({"deprecation"})
public class LegacyApiTest {

    @Test
    public void testCheckEmailSplitMode() {
        Check.Email.shouldUseFullDomain(false);
        Check.Email.addValidDomainName("gmail");
        Check.Email.addValidDomainExtension("com");

        assertTrue(Check.Email.isValid("user@gmail.com"));
        assertFalse(Check.Email.isValid("user@gmail.org"));
        assertFalse(Check.Email.isValid("user@yahoo.com"));
        assertFalse(Check.Email.isValid("invalid-email"));
        assertFalse(Check.Email.isValid("@gmail.com"));
        assertFalse(Check.Email.isValid("user@"));
    }

    @Test
    public void testCheckEmailFullDomainMode() {
        Check.Email.shouldUseFullDomain(true);
        Check.Email.addValidDomain("yahoo.com");

        assertTrue(Check.Email.isValid("user@yahoo.com"));
        assertFalse(Check.Email.isValid("user@gmail.com"));
        Check.Email.shouldUseFullDomain(false);
    }

    @Test
    public void testCheckStringHelpers() {
        assertTrue(Check.hasSymbols("hello@world"));
        assertFalse(Check.hasSymbols("helloworld123"));
        assertTrue(Check.hasNumbers("room 101"));
        assertFalse(Check.hasNumbers("room one"));
        assertTrue(Check.hasSpaces("hello world"));
        assertFalse(Check.hasSpaces("helloworld"));
        assertFalse(Check.hasSymbols(null));
        assertFalse(Check.hasNumbers(""));
        assertFalse(Check.hasSpaces(null));
    }

    @Test
    public void testCheckTimeCalculations() {
        Date now = new Date(1000000);
        Date until = new Date(2000000);
        assertEquals(1000.0, Check.howManySecondsLeft(now, until), 0.001);
        assertEquals(1000.0 / 60.0, Check.howManyMinutesLeft(now, until), 0.001);
        assertEquals(1000.0 / 3600.0, Check.howManyHoursLeft(now, until), 0.001);
        assertEquals(1000.0 / 86400000.0 * 1000.0, Check.howManyDaysLeft(now, until), 0.001);
        assertEquals(0.0, Check.howManySecondsLeft(null, until), 0.001);
    }

    @Test
    public void testCipherCiphers() {
        assertEquals("HLOEL", Cipher.transpositionCipher("HELLO"));
        assertEquals("HLOEL", Cipher.transpositionCipher("HE L LO"));
        assertEquals("DEF", Cipher.caesarCipher("ABC", 3));
        assertEquals("DEF", Cipher.caesarCipher("abc", 3));
        assertEquals("A B C", Cipher.caesarCipher("x y z", 3));
        assertEquals("FBJJN", Cipher.keywordCipher("HELLO", "KEY"));
        assertEquals("CYHHL", Cipher.giovanniCipher("HELLO", "KEY", "C"));
    }

    @Test
    public void testConvertBasic() {
        assertEquals("John", Convert.toRealName("john"));
        assertEquals("A", Convert.toRealName("a"));
        assertEquals(123, Convert.toInt("123"));
        assertEquals(0, Convert.toInt("invalid"));
        assertEquals(1.23f, Convert.toFloat("1.23"), 0.001f);
        assertEquals(0.0f, Convert.toFloat("invalid"), 0.001f);
        assertEquals(1.2345, Convert.toDouble("1.2345"), 0.0001);
        assertEquals(0.0, Convert.toDouble("invalid"), 0.0001);
        assertEquals("null", Convert.toString(null));
        assertEquals("123", Convert.toString(123));
    }

    @Test
    public void testConvertDates() {
        Date date = new Date(126, 4, 4); // 2026-05-04 local time
        assertEquals("5/4/2026", Convert.dateToMMDDYY(date));
        assertEquals("4/5/2026", Convert.dateToDDMMYY(date));
        assertEquals("2026/5/4", Convert.dateToYYMMDD(date));
    }
}
