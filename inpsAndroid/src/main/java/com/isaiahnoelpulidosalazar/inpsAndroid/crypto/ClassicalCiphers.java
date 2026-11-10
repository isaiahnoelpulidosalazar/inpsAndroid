package com.isaiahnoelpulidosalazar.inpsAndroid.crypto;

import java.util.Locale;
import java.util.Objects;

/**
 * Classical text ciphers for learning and puzzles. <b>Not secure; never use for real secrets.</b>
 *
 * <p>All outputs are upper-case. Letters A-Z are substituted; every other character (spaces,
 * digits, punctuation) passes through unchanged.</p>
 */
public final class ClassicalCiphers {

    private static final int SIZE = 26;

    private ClassicalCiphers() {
        throw new UnsupportedOperationException("ClassicalCiphers cannot be instantiated");
    }

    /** Removes spaces, then emits even-indexed characters followed by odd-indexed ones. */
    public static String transposition(String text) {
        String s = text.replace(" ", "").toUpperCase(Locale.ROOT);
        StringBuilder even = new StringBuilder(s.length());
        StringBuilder odd = new StringBuilder(s.length() / 2 + 1);
        for (int i = 0; i < s.length(); i++) {
            (i % 2 == 0 ? even : odd).append(s.charAt(i));
        }
        return even.append(odd).toString();
    }

    /** Caesar shift. Any int is accepted (negative and greater than 25 wrap correctly). */
    public static String caesar(String text, int shift) {
        int k = Math.floorMod(shift, SIZE);
        char[] map = new char[SIZE];
        for (int i = 0; i < SIZE; i++) {
            map[i] = (char) ('A' + (i + k) % SIZE);
        }
        return substitute(text, map);
    }

    /** Keyword substitution: keyword letters (de-duplicated) followed by the unused alphabet. */
    public static String keyword(String text, String keyword) {
        return substitute(text, keywordAlphabet(keyword));
    }

    /**
     * Keyword cipher whose cipher alphabet is then rotated right by the alphabet index of
     * {@code keyLetter}.
     *
     * @throws IllegalArgumentException if {@code keyLetter} is not a single letter A-Z
     */
    public static String giovanni(String text, String keyword, String keyLetter) {
        Objects.requireNonNull(keyLetter, "keyLetter");
        String key = keyLetter.toUpperCase(Locale.ROOT);
        if (key.length() != 1 || key.charAt(0) < 'A' || key.charAt(0) > 'Z') {
            throw new IllegalArgumentException("keyLetter must be a single letter A-Z: " + keyLetter);
        }
        int k = key.charAt(0) - 'A';
        char[] base = keywordAlphabet(keyword);
        char[] rotated = new char[SIZE];
        for (int i = 0; i < SIZE; i++) {
            rotated[(i + k) % SIZE] = base[i];
        }
        return substitute(text, rotated);
    }

    private static char[] keywordAlphabet(String keyword) {
        char[] out = new char[SIZE];
        boolean[] used = new boolean[SIZE];
        int n = 0;
        for (char c : keyword.toUpperCase(Locale.ROOT).toCharArray()) {
            if (c >= 'A' && c <= 'Z' && !used[c - 'A']) { // non-letters in the keyword are ignored
                used[c - 'A'] = true;
                out[n++] = c;
            }
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            if (!used[c - 'A']) out[n++] = c;
        }
        return out;
    }

    /** O(n) direct-index substitution (v1 scanned the alphabet per character and used String +=). */
    private static String substitute(String text, char[] cipherAlphabet) {
        String upper = text.toUpperCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(upper.length());
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            out.append(c >= 'A' && c <= 'Z' ? cipherAlphabet[c - 'A'] : c);
        }
        return out.toString();
    }
}
