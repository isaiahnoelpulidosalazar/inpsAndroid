# StringChecks

The `StringChecks` utility provides null-safe character-class checks on strings. It replaces the string helpers of the deprecated `Check` class.

## Purpose & Scope

The purpose of this utility is to provide:
- Fast single-pass checks for symbols, digits, and spaces
- Null safety: every method returns `false` for `null`

The symbol set is the fixed ASCII set ``~`!@#$%^&*()_+-=[]{}\|'";:,.<>/?``.

## API Reference

### static boolean hasSymbols(String str)

Returns `true` if the string contains any character from the fixed ASCII symbol set.

```java
boolean hasSym = StringChecks.hasSymbols("item $5");
```

### static boolean hasNumbers(String str)

Returns `true` if the string contains a digit (`Character.isDigit`).

```java
boolean hasNum = StringChecks.hasNumbers("abc123");
```

### static boolean hasSpaces(String str)

Returns `true` if the string contains a space character.

```java
boolean hasSp = StringChecks.hasSpaces("hello world");
```
