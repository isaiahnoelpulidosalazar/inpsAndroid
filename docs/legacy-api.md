# Legacy API

The v1 classes `Actions`, `Check`, `Cipher`, `Convert`, `EasySQL`, and `URLRequest` remain in 2.0.0 as `@Deprecated` wrappers that delegate to the new implementations. They are planned for removal in `3.0.0`.

## Purpose & Scope

The purpose of these wrappers is to provide:
- Source compatibility so you can bump the version and migrate at your own pace
- Behaviour matching v1 contracts where practical, with the v2 bug and security fixes applied underneath

See [Migrating to 2.0.0](./migrating-to-v2) for before/after examples.

## Replacement Map

| Deprecated | Replacement |
|---|---|
| `Actions` | [MainThread](./main-thread) |
| `URLRequest` | [HttpRequest](./http-request) |
| `EasySQL` | [Database](./database) |
| `Check.Email` | [EmailValidator](./email-validator) |
| `Check.hasSymbols / hasNumbers / hasSpaces` | [StringChecks](./string-checks) |
| `Check.howMany{Seconds,Minutes,Hours,Days}Left` | [TimeLeft](./time-left) |
| `Check.equalDrawableAndBitmap` | [Drawables](./drawables) |
| `Convert` | [Converters](./converters) |
| `Cipher` | [ClassicalCiphers](./classical-ciphers) |

## Actions

Builds a `Runnable` carrying an optional argument and posts it to the main thread.

```java
Actions action = new Actions().setArg("Payload").build(() -> Log.d("TAG", (String) action.getArg()));
action.execute();
action.executeDelayed(1000);
```

- `setArg(Object)`, `getArg()`, `build(Runnable)`, `execute()`, `executeDelayed(int delay)`
- Posts to the **main** looper (v1 bound to the calling thread's looper)
- Throws `IllegalStateException` if `execute` is called before `build`

## Check

Delegates to the new validators. Signatures are unchanged.

```java
Check.Email.addValidDomainName("gmail");
Check.Email.addValidDomainExtension("com");
boolean ok = Check.Email.isValid("user@gmail.com");
```

- `Check.Email`: `addValidDomainName`, `addValidDomainExtension`, `addValidDomain`, `shouldUseFullDomain()` / `shouldUseFullDomain(boolean)`, `isValid`
- `Check.equalDrawableAndBitmap`, `hasSymbols`, `hasNumbers`, `hasSpaces`
- `Check.howManySecondsLeft / MinutesLeft / HoursLeft / DaysLeft(Date now, Date until)`
- `Check.Email` still uses synchronized static state. The default mode is split mode, and `shouldUseFullDomain()` with no argument enables full-domain mode. Split mode now requires `name.extension` exactly and matching is case-insensitive.

## Cipher

```java
String enc = Cipher.caesarCipher("HELLO WORLD", 3);
```

- `transpositionCipher(String)`, `giovanniCipher(String, String, String)`, `keywordCipher(String, String)`, `caesarCipher(String, int)`
- Same outputs for valid input. Any `int` shift is now accepted, and `giovanniCipher` throws `IllegalArgumentException` for an invalid key letter.

## Convert

```java
String name = Convert.toRealName("john"); // "John"
```

- `toRealName`, `toBase64`, `fromBase64`, `dateToMMDDYY`, `dateToDDMMYY`, `dateToYYMMDD`, `toInt`, `toFloat`, `toDouble`, `toString`
- `Convert.toBase64` still uses `Base64.DEFAULT` and therefore ends with a newline, unlike `Converters.toBase64`
- Date methods return unpadded values (for example `5/4/2026`), and numeric parsers fall back to `0`

## EasySQL

```java
EasySQL db = new EasySQL(context);
db.createTable("mydb", "users", new String[]{"id:INTEGER", "name:TEXT"});
db.insertToTable("mydb", "users", new String[]{"id:1", "name:Alice"});
db.deleteFromTable("mydb", "users", "id:1");
List<String> cells = db.getTableValues("mydb", "users");
```

- `createTable`, `doesTableExist`, `insertToTable`, `deleteFromTable`, `getTableValues`, `getTableValuesAsArray`, `clearTable`, `deleteTable`
- Inputs are `"name:TYPE"` / `"column:value"` strings split at the **first** colon
- Exceptions are still swallowed and logged (tag `EasySQL`), but statements are now parameterised and one connection is cached per database
- `getTableValues*` return `column:value` for numbers and `column:'text'` for text
- Call from a background thread

## URLRequest

```java
URLRequest request = new URLRequest();
request.build(() -> Log.d("HTTP", (String) request.getDefaultActionArg()));
request.execute("https://api.example.com/data", "GET");
```

- `build(Runnable)`, `execute(String URL, String method)`, `getDefaultActionArg()`
- Only HTTP 200 yields the body; anything else yields `"Error: <code or message>"`
- No longer uses `AsyncTask`; delegates to `HttpRequest` and delivers via `Actions` on the main thread
