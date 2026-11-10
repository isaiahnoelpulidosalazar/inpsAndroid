package com.isaiahnoelpulidosalazar.inpsAndroid.time;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/** Fractional time remaining between two instants. Date overloads return {@code 0.0} for null. */
public final class TimeLeft {

    private static final double MS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
    private static final double MS_PER_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final double MS_PER_HOUR = TimeUnit.HOURS.toMillis(1);
    private static final double MS_PER_DAY = TimeUnit.DAYS.toMillis(1);

    private TimeLeft() {
        throw new UnsupportedOperationException("TimeLeft cannot be instantiated");
    }

    public static double seconds(long nowMs, long untilMs) { return (untilMs - nowMs) / MS_PER_SECOND; }
    public static double minutes(long nowMs, long untilMs) { return (untilMs - nowMs) / MS_PER_MINUTE; }
    public static double hours(long nowMs, long untilMs)   { return (untilMs - nowMs) / MS_PER_HOUR; }
    public static double days(long nowMs, long untilMs)    { return (untilMs - nowMs) / MS_PER_DAY; }

    public static double seconds(Date now, Date until) { return now == null || until == null ? 0.0 : seconds(now.getTime(), until.getTime()); }
    public static double minutes(Date now, Date until) { return now == null || until == null ? 0.0 : minutes(now.getTime(), until.getTime()); }
    public static double hours(Date now, Date until)   { return now == null || until == null ? 0.0 : hours(now.getTime(), until.getTime()); }
    public static double days(Date now, Date until)    { return now == null || until == null ? 0.0 : days(now.getTime(), until.getTime()); }
}
