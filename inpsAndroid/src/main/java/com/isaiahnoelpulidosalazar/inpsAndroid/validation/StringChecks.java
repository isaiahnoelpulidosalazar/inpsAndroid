package com.isaiahnoelpulidosalazar.inpsAndroid.validation;

/** Null-safe character-class checks on strings. */
public final class StringChecks {

    private static final String SYMBOLS = "~`!@#$%^&*()_+-=[]{}\\|'\";:,.<>/?";

    private StringChecks() {
        throw new UnsupportedOperationException("StringChecks cannot be instantiated");
    }

    /** @return {@code true} if {@code str} contains any symbol from the fixed ASCII symbol set. */
    public static boolean hasSymbols(String str) {
        if (str == null) return false;
        for (int i = 0; i < str.length(); i++) {
            if (SYMBOLS.indexOf(str.charAt(i)) >= 0) return true;
        }
        return false;
    }

    /** @return {@code true} if {@code str} contains a digit ({@link Character#isDigit(char)}). */
    public static boolean hasNumbers(String str) {
        if (str == null) return false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) return true;
        }
        return false;
    }

    /** @return {@code true} if {@code str} contains a space character. */
    public static boolean hasSpaces(String str) {
        return str != null && str.indexOf(' ') >= 0;
    }
}
