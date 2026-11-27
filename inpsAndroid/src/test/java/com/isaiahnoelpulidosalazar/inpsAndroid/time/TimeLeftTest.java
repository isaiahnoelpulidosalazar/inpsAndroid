package com.isaiahnoelpulidosalazar.inpsAndroid.time;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TimeLeftTest {

    private static final double EPS = 1e-6;

    @Test
    public void testMillisOverloads() {
        long now = 1_000_000L;
        assertEquals(30.0, TimeLeft.seconds(now, now + 30_000L), EPS);
        assertEquals(2.5, TimeLeft.minutes(now, now + 150_000L), EPS);
        assertEquals(1.5, TimeLeft.hours(now, now + 5_400_000L), EPS);
        assertEquals(2.0, TimeLeft.days(now, now + 172_800_000L), EPS);
        assertEquals(-10.0, TimeLeft.seconds(now + 10_000L, now), EPS);
    }

    @Test
    public void testDateOverloadsAndNulls() {
        Date start = new Date(1_000_000L);
        Date end = new Date(1_000_000L + 3_600_000L);

        assertEquals(3600.0, TimeLeft.seconds(start, end), EPS);
        assertEquals(60.0, TimeLeft.minutes(start, end), EPS);
        assertEquals(1.0, TimeLeft.hours(start, end), EPS);
        assertEquals(1.0 / 24.0, TimeLeft.days(start, end), EPS);

        assertEquals(0.0, TimeLeft.seconds(null, end), EPS);
        assertEquals(0.0, TimeLeft.minutes(start, null), EPS);
        assertEquals(0.0, TimeLeft.hours(null, null), EPS);
        assertEquals(0.0, TimeLeft.days(null, end), EPS);
    }
}
