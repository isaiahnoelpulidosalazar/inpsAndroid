package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.graphics.Bitmap;

import com.isaiahnoelpulidosalazar.inpsAndroid.graphics.Drawables;
import com.isaiahnoelpulidosalazar.inpsAndroid.time.TimeLeft;
import com.isaiahnoelpulidosalazar.inpsAndroid.validation.EmailValidator;
import com.isaiahnoelpulidosalazar.inpsAndroid.validation.StringChecks;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Legacy validation helpers, kept as thin delegating wrappers.
 *
 * @deprecated Since 2.0.0. Use {@link EmailValidator}, {@link StringChecks}, {@link TimeLeft}
 *             and {@link Drawables}. See {@code migrating_to_v2.md}.
 */
@Deprecated
public final class Check {

    private Check() {
        throw new UnsupportedOperationException("Check class cannot be instantiated");
    }

    /**
     * Legacy global-state e-mail validator.
     *
     * @deprecated Use an {@link EmailValidator} instance instead of mutable static state.
     */
    @Deprecated
    public static final class Email {

        private static final Set<String> names = new HashSet<>();
        private static final Set<String> extensions = new HashSet<>();
        private static final Set<String> domains = new HashSet<>();
        private static boolean fullDomainMode = false;
        private static EmailValidator cached;

        private Email() {
            throw new UnsupportedOperationException("Check.Email class cannot be instantiated");
        }

        public static synchronized void addValidDomainName(String str) {
            if (str != null) { names.add(str); cached = null; }
        }

        public static synchronized void addValidDomainExtension(String str) {
            if (str != null) { extensions.add(str); cached = null; }
        }

        public static synchronized void addValidDomain(String str) {
            if (str != null) { domains.add(str); cached = null; }
        }

        public static void shouldUseFullDomain() {
            shouldUseFullDomain(true);
        }

        public static synchronized void shouldUseFullDomain(boolean useFullDomain) {
            fullDomainMode = useFullDomain;
            cached = null;
        }

        public static boolean isValid(String str) {
            return validator().isValid(str);
        }

        private static synchronized EmailValidator validator() {
            if (cached == null) {
                EmailValidator.Builder b = new EmailValidator.Builder().useFullDomain(fullDomainMode);
                for (String s : names) b.addDomainName(s);
                for (String s : extensions) b.addDomainExtension(s);
                for (String s : domains) b.addDomain(s);
                cached = b.build();
            }
            return cached;
        }
    }

    public static boolean equalDrawableAndBitmap(Context context, int resourceId, Bitmap bitmap) {
        return Drawables.equalsBitmap(context, resourceId, bitmap);
    }

    public static boolean hasSymbols(String str) { return StringChecks.hasSymbols(str); }

    public static boolean hasNumbers(String str) { return StringChecks.hasNumbers(str); }

    public static boolean hasSpaces(String str) { return StringChecks.hasSpaces(str); }

    public static double howManySecondsLeft(Date now, Date until) { return TimeLeft.seconds(now, until); }

    public static double howManyMinutesLeft(Date now, Date until) { return TimeLeft.minutes(now, until); }

    public static double howManyHoursLeft(Date now, Date until) { return TimeLeft.hours(now, until); }

    public static double howManyDaysLeft(Date now, Date until) { return TimeLeft.days(now, until); }
}
