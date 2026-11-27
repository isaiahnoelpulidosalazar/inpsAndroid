# TimeLeft

The `TimeLeft` utility calculates fractional time remaining between two instants. It replaces the date helpers of the deprecated `Check` class.

## Purpose & Scope

The purpose of this utility is to provide:
- Simple second, minute, hour, and day differences as `double` values
- Overloads for epoch milliseconds (`long`) and `Date`
- Null-safe `Date` overloads that return `0.0`

Results are negative when `until` is earlier than `now`.

## API Reference

### Millisecond Overloads

#### static double seconds(long nowMs, long untilMs)
Seconds remaining between two epoch-millisecond values.

```java
double s = TimeLeft.seconds(System.currentTimeMillis(), deadlineMs);
```

#### static double minutes(long nowMs, long untilMs)
Minutes remaining.

```java
double m = TimeLeft.minutes(nowMs, untilMs);
```

#### static double hours(long nowMs, long untilMs)
Hours remaining.

```java
double h = TimeLeft.hours(nowMs, untilMs);
```

#### static double days(long nowMs, long untilMs)
Days remaining.

```java
double d = TimeLeft.days(nowMs, untilMs);
```

---

### Date Overloads

#### static double seconds(Date now, Date until)
Seconds remaining between two dates, or `0.0` if either is `null`.

```java
double s = TimeLeft.seconds(new Date(), futureDate);
```

#### static double minutes(Date now, Date until)
Minutes remaining, or `0.0` for `null`.

```java
double m = TimeLeft.minutes(new Date(), futureDate);
```

#### static double hours(Date now, Date until)
Hours remaining, or `0.0` for `null`.

```java
double h = TimeLeft.hours(new Date(), futureDate);
```

#### static double days(Date now, Date until)
Days remaining, or `0.0` for `null`.

```java
double d = TimeLeft.days(new Date(), futureDate);
```
