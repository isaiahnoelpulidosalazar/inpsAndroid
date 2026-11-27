package com.isaiahnoelpulidosalazar.inpsandroid.core.time;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Fractional time remaining between two instants.
 *
 * <p>The {@link Date} overloads return {@code 0.0} when either argument is {@code null}.</p>
 */
public final class TimeLeft {

    private static final double MS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
    private static final double MS_PER_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final double MS_PER_HOUR = TimeUnit.HOURS.toMillis(1);
    private static final double MS_PER_DAY = TimeUnit.DAYS.toMillis(1);

    private TimeLeft() {
        throw new UnsupportedOperationException("No instances");
    }

    public static double seconds(long nowMs, long untilMs) { return (untilMs - nowMs) / MS_PER_SECOND; }

    public static double minutes(long nowMs, long untilMs) { return (untilMs - nowMs) / MS_PER_MINUTE; }

    public static double hours(long nowMs, long untilMs)   { return (untilMs - nowMs) / MS_PER_HOUR; }

    public static double days(long nowMs, long untilMs)    { return (untilMs - nowMs) / MS_PER_DAY; }

    /** @return fractional seconds remaining, or {@code 0.0} if either {@code Date} is {@code null}. */
    public static double seconds(Date now, Date until) {
        return now == null || until == null ? 0.0 : seconds(now.getTime(), until.getTime());
    }

    /** @return fractional minutes remaining, or {@code 0.0} if either {@code Date} is {@code null}. */
    public static double minutes(Date now, Date until) {
        return now == null || until == null ? 0.0 : minutes(now.getTime(), until.getTime());
    }

    /** @return fractional hours remaining, or {@code 0.0} if either {@code Date} is {@code null}. */
    public static double hours(Date now, Date until) {
        return now == null || until == null ? 0.0 : hours(now.getTime(), until.getTime());
    }

    /** @return fractional days remaining, or {@code 0.0} if either {@code Date} is {@code null}. */
    public static double days(Date now, Date until) {
        return now == null || until == null ? 0.0 : days(now.getTime(), until.getTime());
    }
}
