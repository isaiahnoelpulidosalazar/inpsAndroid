# Converters

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.convert`

`Converters` provides null-safe string, Base64, numeric, and date conversions. Numeric parsing methods return the supplied fallback rather than throwing `NumberFormatException`.

## API Reference

| Method | Description |
|---|---|
| `capitalize(String str)` | Upper-cases the first code point; returns `null` or `""` unchanged. |
| `toBase64(String str)` | Encodes UTF-8 text as Base64 (`Base64.NO_WRAP`); returns `null` when `str` is `null`. |
| `fromBase64(String str)` | Decodes Base64 to UTF-8 text; returns `null` when `str` is `null` or invalid. |
| `toInt(String str)` / `toInt(String str, int fallback)` | Trims and parses `int`, returning `0` or `fallback` on invalid/`null` input. |
| `toFloat(String str)` / `toFloat(String str, float fallback)` | Trims and parses `float`, returning `0f` or `fallback` on invalid/`null` input. |
| `toDouble(String str)` / `toDouble(String str, double fallback)` | Trims and parses `double`, returning `0.0` or `fallback` on invalid/`null` input. |
| `formatDate(Date date, String pattern)` | Formats `date` using `Locale.US`, `TimeZone.getDefault()`, and a `ThreadLocal` formatter cache. Returns `null` if either argument is `null`. |
| `toMonthDayYear(Date date)` | Formats using `"M/d/yyyy"`. |
| `toDayMonthYear(Date date)` | Formats using `"d/M/yyyy"`. |
| `toYearMonthDay(Date date)` | Formats using `"yyyy/M/d"`. |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.convert.Converters;

String title = Converters.capitalize("inpsAndroid"); // "InpsAndroid"
String encoded = Converters.toBase64("hello");       // "aGVsbG8="
String decoded = Converters.fromBase64(encoded);     // "hello"
int port = Converters.toInt("8080", 80);             // 8080
String today = Converters.toYearMonthDay(new Date());
```
