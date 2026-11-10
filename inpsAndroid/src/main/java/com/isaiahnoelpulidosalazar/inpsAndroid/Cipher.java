package com.isaiahnoelpulidosalazar.inpsAndroid;

import com.isaiahnoelpulidosalazar.inpsAndroid.crypto.ClassicalCiphers;

/**
 * Legacy cipher entry points.
 *
 * @deprecated Since 2.0.0. Use {@link ClassicalCiphers} (the name {@code Cipher} also clashes with
 *             {@code javax.crypto.Cipher}). See {@code migrating_to_v2.md}.
 */
@Deprecated
public class Cipher {

    public static String transpositionCipher(String text) { return ClassicalCiphers.transposition(text); }

    public static String giovanniCipher(String text, String keyword, String keyLetter) {
        return ClassicalCiphers.giovanni(text, keyword, keyLetter);
    }

    public static String keywordCipher(String text, String keyword) { return ClassicalCiphers.keyword(text, keyword); }

    public static String caesarCipher(String text, int shift) { return ClassicalCiphers.caesar(text, shift); }
}
