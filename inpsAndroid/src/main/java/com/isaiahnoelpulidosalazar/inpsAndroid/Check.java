package com.isaiahnoelpulidosalazar.inpsandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class providing static helper methods for common validation and comparison tasks,
 * such as email validation, string analysis, drawable comparison, and time-difference calculations.
 */
public final class Check {

    private Check() {
        throw new UnsupportedOperationException("Check class cannot be instantiated");
    }

    /**
     * A nested utility class for validating email addresses against configurable allowed domains.
     *
     * <p>Supports two modes of validation:</p>
     * <ul>
     *   <li><b>Full domain mode</b>: validates the entire domain (e.g., {@code gmail.com})</li>
     *   <li><b>Split mode</b>: validates the domain name and extension separately
     *       (e.g., name = {@code gmail}, extension = {@code com})</li>
     * </ul>
     *
     * <p>Example usage (split mode):</p>
     * <pre>{@code
     * Check.Email.addValidDomainName("gmail");
     * Check.Email.addValidDomainExtension("com");
     * boolean valid = Check.Email.isValid("user@gmail.com"); // true
     * }</pre>
     *
     * <p>Example usage (full domain mode):</p>
     * <pre>{@code
     * Check.Email.addValidDomain("gmail.com");
     * Check.Email.shouldUseFullDomain(true);
     * boolean valid = Check.Email.isValid("user@gmail.com"); // true
     * }</pre>
     */
    public static final class Email {

        private static final Set<String> validDomainNames = new HashSet<>();
        private static final Set<String> validDomainExtensions = new HashSet<>();
        private static final Set<String> validDomains = new HashSet<>();
        private static volatile boolean shouldUseFullDomain = false;

        private Email() {
            throw new UnsupportedOperationException("Check.Email class cannot be instantiated");
        }

        /**
         * Adds a domain name (the part before the dot) to the list of accepted domain names.
         * Used in split validation mode.
         *
         * @param str the domain name to add (e.g., {@code "gmail"})
         */
        public static void addValidDomainName(String str) {
            if (str != null){
                validDomainNames.add(str);
            }
        }

        /**
         * Adds a domain extension (the part after the dot) to the list of accepted extensions.
         * Used in split validation mode.
         *
         * @param str the domain extension to add (e.g., {@code "com"})
         */
        public static void addValidDomainExtension(String str) {
            if (str != null){
                validDomainExtensions.add(str);
            }
        }

        /**
         * Adds a complete domain string to the list of accepted full domains.
         * Used in full domain validation mode.
         *
         * @param str the full domain to accept (e.g., {@code "gmail.com"})
         */
        public static void addValidDomain(String str) {
            if (str != null){
                validDomains.add(str);
            }
        }

        /**
         * Enables full domain validation mode.
         * When enabled, {@link #isValid(String)} matches the entire domain after {@code @}.
         */
        public static void shouldUseFullDomain() {
            shouldUseFullDomain = true;
        }

        /**
         * Sets whether full domain validation mode is enabled or disabled.
         *
         * @param useFullDomain {@code true} to enable full domain validation; {@code false} to use split mode
         */
        public static void shouldUseFullDomain(boolean useFullDomain) {
            shouldUseFullDomain = useFullDomain;
        }

        /**
         * Validates an email address against the configured allowed domains.
         *
         * <p>In full domain mode, the domain after {@code @} must exactly match one of the
         * entries added via {@link #addValidDomain(String)}.</p>
         *
         * <p>In split mode, both the domain name and domain extension must each appear
         * in their respective allowed lists.</p>
         *
         * @param str the email address to validate
         * @return {@code true} if the email matches the configured domain rules; {@code false} otherwise
         */
        public static boolean isValid(String str) {
            if (str == null || !str.contains("@")) {
                return false;
            }

            int atIndex = str.lastIndexOf('@');
            if (atIndex == 0 || atIndex == str.length() - 1) {
                return false;
            }

            String domainPart = str.substring(atIndex + 1);

            if (shouldUseFullDomain) {
                return validDomains.contains(domainPart);
            } else {
                String[] parts = domainPart.split("\\.");
                if (parts.length < 2) {
                    return false;
                }
                return validDomainNames.contains(parts[0]) && validDomainExtensions.contains(parts[1]);
            }
        }
    }

    /**
     * Compares a drawable resource to a {@link Bitmap} for pixel-level equality.
     *
     * @param context    the {@link Context} used to load the drawable resource
     * @param resourceId the resource ID of the drawable to compare
     * @param bitmap     the {@link Bitmap} to compare against
     * @return {@code true} if the drawable's bitmap and the provided bitmap are identical;
     *         {@code false} otherwise
     */
    public static boolean equalDrawableAndBitmap(Context context, int resourceId, Bitmap bitmap) {
        if (context == null || bitmap == null){
            return false;
        }

        Drawable drawable = AppCompatResources.getDrawable(context, resourceId);
        
        if (drawable instanceof BitmapDrawable) {
            Bitmap drawableBitmap = ((BitmapDrawable) drawable).getBitmap();
            return drawableBitmap != null && drawableBitmap.sameAs(bitmap);
        }
        
        return false;
    }

    /**
     * Checks whether a string contains any special symbol characters.
     *
     * <p>The set of symbols checked includes: {@code ~`!@#$%^&*()_+-=[]{}\\|'";:,.<>/?}</p>
     *
     * @param str the string to inspect
     * @return {@code true} if the string contains at least one symbol; {@code false} otherwise
     */
    public static boolean hasSymbols(String str) {
        if (str == null || str.isEmpty()){
            return false;
        }
        
        String symbols = "~`!@#$%^&*()_+-=[]{}\\|'\";:,.<>/?";
        for (int i = 0; i < str.length(); i++) {
            if (symbols.indexOf(str.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string contains any numeric digit characters (0–9).
     *
     * @param str the string to inspect
     * @return {@code true} if the string contains at least one digit; {@code false} otherwise
     */
    public static boolean hasNumbers(String str) {
        if (str == null || str.isEmpty()){
            return false;
        }
        
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string contains any whitespace (space) characters.
     *
     * @param str the string to inspect
     * @return {@code true} if the string contains at least one space; {@code false} otherwise
     */
    public static boolean hasSpaces(String str) {
        if (str == null || str.isEmpty()){
            return false;
        }
        
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the number of seconds remaining between two dates.
     *
     * @param now   the current date/time
     * @param until the target future date/time
     * @return the number of seconds from {@code now} until {@code until} as a {@code double}
     */
    public static double howManySecondsLeft(Date now, Date until) {
        if (now == null || until == null){
            return 0.0;
        }
        return (until.getTime() - now.getTime()) / 1000.0;
    }

    /**
     * Calculates the number of minutes remaining between two dates.
     *
     * @param now   the current date/time
     * @param until the target future date/time
     * @return the number of minutes from {@code now} until {@code until} as a {@code double}
     */
    public static double howManyMinutesLeft(Date now, Date until) {
        if (now == null || until == null){
            return 0.0;
        }
        return (until.getTime() - now.getTime()) / 60000.0;
    }

    /**
     * Calculates the number of hours remaining between two dates.
     *
     * @param now   the current date/time
     * @param until the target future date/time
     * @return the number of hours from {@code now} until {@code until} as a {@code double}
     */
    public static double howManyHoursLeft(Date now, Date until) {
        if (now == null || until == null){
            return 0.0;
        }
        return (until.getTime() - now.getTime()) / 3600000.0;
    }

    /**
     * Calculates the number of days remaining between two dates.
     *
     * @param now   the current date/time
     * @param until the target future date/time
     * @return the number of days from {@code now} until {@code until} as a {@code double}
     */
    public static double howManyDaysLeft(Date now, Date until) {
        if (now == null || until == null){
            return 0.0;
        }
        return (until.getTime() - now.getTime()) / 86400000.0;
    }
}