package com.isaiahnoelpulidosalazar.inpsandroid.core.validation;

/** Null-safe character-class inspection helpers for strings. */
public final class StringChecks {

  private static final String SYMBOLS = "~`!@#$%^&*()_+-=[]{}\\|'\";:,.<>/?";

  private StringChecks() {
    throw new UnsupportedOperationException("No instances");
  }

  /**
   * Checks whether {@code str} contains at least one punctuation or symbol character from the
   * standard ASCII symbol set.
   *
   * @param str input text, may be {@code null}
   * @return {@code true} if {@code str} contains any symbol character, {@code false} otherwise
   */
  public static boolean hasSymbols(String str) {
    if (str == null) return false;
    for (int i = 0; i < str.length(); i++) {
      if (SYMBOLS.indexOf(str.charAt(i)) >= 0) return true;
    }
    return false;
  }

  /**
   * Checks whether {@code str} contains at least one digit ({@link Character#isDigit(char)}).
   *
   * @param str input text, may be {@code null}
   * @return {@code true} if {@code str} contains a digit, {@code false} otherwise
   */
  public static boolean hasNumbers(String str) {
    if (str == null) return false;
    for (int i = 0; i < str.length(); i++) {
      if (Character.isDigit(str.charAt(i))) return true;
    }
    return false;
  }

  /**
   * Checks whether {@code str} contains a literal space character ({@code ' '}).
   *
   * @param str input text, may be {@code null}
   * @return {@code true} if {@code str} contains {@code ' '}, {@code false} otherwise
   */
  public static boolean hasSpaces(String str) {
    return str != null && str.indexOf(' ') >= 0;
  }
}
