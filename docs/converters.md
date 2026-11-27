# Converters

The `Converters` utility provides type and format conversions: string casing, Base64, safe numeric parsing, and date formatting. It replaces the deprecated `Convert` class.

## Purpose & Scope

The purpose of this utility is to provide:
- Safe numeric parsing with explicit fallbacks, so no `NumberFormatException` and no printed stack traces
- Null-safe string helpers and Base64 encoding/decoding as UTF-8
- Thread-safe date formatting (a fresh `SimpleDateFormat` is created per call, using `Locale.US` and the device time zone)

## API Reference

### String & Base64 Operations

#### static String capitalize(String str)
Upper-cases the first character (code-point aware). `null` and empty strings are returned unchanged.

```java
String name = Converters.capitalize("john"); // "John"
```

#### static String toBase64(String str)
Encodes a UTF-8 string with `Base64.NO_WRAP` (no trailing newline).

```java
String b64 = Converters.toBase64("hello");
```

#### static String fromBase64(String str)
Decodes Base64 to a UTF-8 string. Returns `null` if the input is `null` or invalid.

```java
String text = Converters.fromBase64("aGVsbG8=");
```

---

### Safe Numeric Parsing

#### static int toInt(String str, int fallback)
Trims and parses an integer, returning the fallback on `null` or parse failure.

```java
int val = Converters.toInt(" 7 ", -1); // 7
```

#### static float toFloat(String str, float fallback)
Trims and parses a float, returning the fallback on failure.

```java
float val = Converters.toFloat("3.14", 0f);
```

#### static double toDouble(String str, double fallback)
Trims and parses a double, returning the fallback on failure.

```java
double val = Converters.toDouble("x", -1.0); // -1.0
```

#### static int toInt(String str) / toFloat(String str) / toDouble(String str)
Convenience overloads using `0`, `0f`, and `0.0` as the fallback.

```java
int val = Converters.toInt("42");
```

---

### Date Formatting

#### static String formatDate(Date date, String pattern)
Formats a date with any `SimpleDateFormat` pattern. A `null` date returns `null`.

```java
String d = Converters.formatDate(new Date(), "dd MMM yyyy");
```

#### static String toMonthDayYear(Date date)
Formats as unpadded month/day/year, for example `5/4/2026`.

```java
String d = Converters.toMonthDayYear(new Date());
```

#### static String toDayMonthYear(Date date)
Formats as unpadded day/month/year, for example `4/5/2026`.

```java
String d = Converters.toDayMonthYear(new Date());
```

#### static String toYearMonthDay(Date date)
Formats as unpadded year/month/day, for example `2026/5/4`.

```java
String d = Converters.toYearMonthDay(new Date());
```
