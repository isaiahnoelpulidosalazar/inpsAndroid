package com.isaiahnoelpulidosalazar.inpsandroid.core.time;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Computes fractional time remaining between two instants in seconds, minutes, hours, or days.
 *
 * <p>Values are signed ({@code until - now}), so past targets yield negative numbers. The
 * {@link Date} overloads return {@code 0.0} when either argument is {@code null}.</p>
 */
public final class TimeLeft {

    private static final double MS_PER_SECOND = TimeUnit.SECONDS.toMillis(1);
    private static final double MS_PER_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final double MS_PER_HOUR = TimeUnit.HOURS.toMillis(1);
    private static final double MS_PER_DAY = TimeUnit.DAYS.toMillis(1);

    private TimeLeft() {
        throw new UnsupportedOperationException("No instances");
    }

    /**
     * Calculates remaining seconds from {@code nowMs} to {@code untilMs}.
     *
     * @param nowMs   starting epoch timestamp in milliseconds
     * @param untilMs target epoch timestamp in milliseconds
     * @return fractional seconds between {@code nowMs} and {@code untilMs}
     */
    public static double seconds(long nowMs, long untilMs) {
        return (untilMs - nowMs) / MS_PER_SECOND;
    }

    /**
     * Calculates remaining minutes from {@code nowMs} to {@code untilMs}.
     *
     * @param nowMs   starting epoch timestamp in milliseconds
     * @param untilMs target epoch timestamp in milliseconds
     * @return fractional minutes between {@code nowMs} and {@code untilMs}
     */
    public static double minutes(long nowMs, long untilMs) {
        return (untilMs - nowMs) / MS_PER_MINUTE;
    }

    /**
     * Calculates remaining hours from {@code nowMs} to {@code untilMs}.
     *
     * @param nowMs   starting epoch timestamp in milliseconds
     * @param untilMs target epoch timestamp in milliseconds
     * @return fractional hours between {@code nowMs} and {@code untilMs}
     */
    public static double hours(long nowMs, long untilMs) {
        return (untilMs - nowMs) / MS_PER_HOUR;
    }

    /**
     * Calculates remaining days from {@code nowMs} to {@code untilMs}.
     *
     * @param nowMs   starting epoch timestamp in milliseconds
     * @param untilMs target epoch timestamp in milliseconds
     * @return fractional days between {@code nowMs} and {@code untilMs}
     */
    public static double days(long nowMs, long untilMs) {
        return (untilMs - nowMs) / MS_PER_DAY;
    }

    /**
     * Calculates fractional seconds remaining between two {@link Date} instances.
     *
     * @param now   starting instant, may be {@code null}
     * @param until target instant, may be {@code null}
     * @return fractional seconds remaining, or {@code 0.0} if either {@link Date} is {@code null}
     */
    public static double seconds(Date now, Date until) {
        return now == null || until == null ? 0.0 : seconds(now.getTime(), until.getTime());
    }

    /**
     * Calculates fractional minutes remaining between two {@link Date} instances.
     *
     * @param now   starting instant, may be {@code null}
     * @param until target instant, may be {@code null}
     * @return fractional minutes remaining, or {@code 0.0} if either {@link Date} is {@code null}
     */
    public static double minutes(Date now, Date until) {
        return now == null || until == null ? 0.0 : minutes(now.getTime(), until.getTime());
    }

    /**
     * Calculates fractional hours remaining between two {@link Date} instances.
     *
     * @param now   starting instant, may be {@code null}
     * @param until target instant, may be {@code null}
     * @return fractional hours remaining, or {@code 0.0} if either {@link Date} is {@code null}
     */
    public static double hours(Date now, Date until) {
        return now == null || until == null ? 0.0 : hours(now.getTime(), until.getTime());
    }

    /**
     * Calculates fractional days remaining between two {@link Date} instances.
     *
     * @param now   starting instant, may be {@code null}
     * @param until target instant, may be {@code null}
     * @return fractional days remaining, or {@code 0.0} if either {@link Date} is {@code null}
     */
    public static double days(Date now, Date until) {
        return now == null || until == null ? 0.0 : days(now.getTime(), until.getTime());
    }
}
