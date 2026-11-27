# ClassicalCiphers

The `ClassicalCiphers` utility provides classical text ciphers (Transposition, Caesar, Keyword, and Giovanni) for learning and puzzles. It replaces the deprecated `Cipher` class, whose name also clashed with `javax.crypto.Cipher`.

> **Not secure.** Never use these for real secrets.

## Purpose & Scope

The purpose of this utility is to provide:
- Classical substitution and transposition algorithms for demonstration and education
- Upper-case output: letters A-Z are substituted, while spaces, digits, and punctuation pass through unchanged
- Locale-safe casing (`Locale.ROOT`)

## API Reference

### static String transposition(String text)

Removes spaces, then emits even-indexed characters followed by odd-indexed ones.

```java
String enc = ClassicalCiphers.transposition("HELLO"); // "HLOEL"
```

### static String caesar(String text, int shift)

Shifts letters by the given offset. Any `int` is accepted; negative and greater-than-25 shifts wrap correctly.

```java
String enc = ClassicalCiphers.caesar("abc", -3); // "XYZ"
```

### static String keyword(String text, String keyword)

Substitutes using an alphabet made of the de-duplicated keyword letters followed by the unused alphabet. Non-letters in the keyword are ignored.

```java
String enc = ClassicalCiphers.keyword("HELLO", "KEY"); // "FBJJN"
```

### static String giovanni(String text, String keyword, String keyLetter)

Builds the keyword alphabet, then rotates it right by the alphabet index of `keyLetter`. Throws `IllegalArgumentException` if `keyLetter` is not a single letter A-Z.

```java
String enc = ClassicalCiphers.giovanni("HELLO", "KEY", "C"); // "CYHHL"
```
