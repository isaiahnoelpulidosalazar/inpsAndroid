# TimeLeft

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.time`

`TimeLeft` calculates the fractional time remaining between two instants. The `Date` overloads return `0.0` when either argument is `null`.

## API Reference

| Method | Description |
|---|---|
| `seconds(long nowMs, long untilMs)` / `seconds(Date now, Date until)` | Fractional seconds remaining. |
| `minutes(long nowMs, long untilMs)` / `minutes(Date now, Date until)` | Fractional minutes remaining. |
| `hours(long nowMs, long untilMs)` / `hours(Date now, Date until)` | Fractional hours remaining. |
| `days(long nowMs, long untilMs)` / `days(Date now, Date until)` | Fractional days remaining. |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.time.TimeLeft;

long now = System.currentTimeMillis();
double mins = TimeLeft.minutes(now, now + 90_000L); // 1.5
double hrs  = TimeLeft.hours(startDate, endDate);
```
