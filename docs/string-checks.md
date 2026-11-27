# StringChecks

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.validation`

`StringChecks` provides null-safe character-class checks on strings. All methods return `false` when passed `null`.

## API Reference

| Method | Description |
|---|---|
| `hasSymbols(String str)` | Returns `true` if `str` contains any character from `` ~`!@#$%^&*()_+-=[]{}\\|'";:,.<>/? ``. |
| `hasNumbers(String str)` | Returns `true` if `str` contains any digit (`Character.isDigit(char)`). |
| `hasSpaces(String str)` | Returns `true` if `str` contains `' '`. |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.validation.StringChecks;

boolean hasSym = StringChecks.hasSymbols("hello!"); // true
boolean hasNum = StringChecks.hasNumbers("v3");     // true
boolean hasSpc = StringChecks.hasSpaces("a b");     // true
```
