# Migrating to inpsAndroid 2.0.0

`com.isaiahnoelpulidosalazar:inpsAndroid:2.0.0`

v2 is **source-compatible for almost everything**: every v1 class that moved or was replaced is still there as a
`@Deprecated` wrapper that delegates to the new implementation. You can bump the version, fix the handful of
[breaking changes](#3-breaking-changes-in-non-deprecated-apis), and migrate the deprecation warnings at your own pace.

Deprecated wrappers are planned for removal in **3.0.0**.

---

## 1. Quick reference (deprecated -> replacement)

| v1 (deprecated)                                   | v2 replacement                                                            |
|---------------------------------------------------|---------------------------------------------------------------------------|
| `Actions`                                         | `concurrent.MainThread.post / postDelayed / run`                          |
| `URLRequest`                                      | `network.HttpRequest`                                                     |
| `EasySQL`                                         | `database.Database`                                                       |
| `Check.Email` (static, mutable global state)      | `validation.EmailValidator` (immutable, built with a `Builder`)           |
| `Check.hasSymbols / hasNumbers / hasSpaces`       | `validation.StringChecks`                                                 |
| `Check.howMany{Seconds,Minutes,Hours,Days}Left`   | `time.TimeLeft.seconds / minutes / hours / days`                          |
| `Check.equalDrawableAndBitmap`                    | `graphics.Drawables.equalsBitmap`                                         |
| `Convert`                                         | `convert.Converters`                                                      |
| `Cipher`                                          | `crypto.ClassicalCiphers`                                                 |
| `FlippableImageView` 9 extra constructors         | standard 3 constructors + `setSpeed` / `setReverseBackImage` / XML attrs  |
| `FlippableImageView.getFrontImage/getBackImage`   | `getFrontDrawable / getBackDrawable`                                      |
| `RoundedAlertDialog.hide()`                       | `RoundedAlertDialog.dismiss()`                                            |
| `SimpleList.getData()`                            | `SimpleList.getItems()` (read-only)                                       |
| `SoundSynthesizer.playTone(int index)`            | `SoundSynthesizer.playNote(int midiNote)`                                 |

Unchanged names (no migration needed apart from the notes in section 3): `AreaCalculator`, `Sort`, `Memory`,
`Fullscreen`, `RoundedButton`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`.

---

## 2. Deprecated API migrations

### 2.1 `Actions` -> `MainThread`

```java
// v1
Actions a = new Actions().setArg("Hello").build(() -> { String msg = (String) a.getArg(); ... });
a.execute();
a.executeDelayed(500);

// v2: capture the argument in the lambda, no mutable holder object
String msg = "Hello";
MainThread.post(() -> show(msg));
MainThread.postDelayed(() -> show(msg), 500);
```

Behaviour change: v1 used `new Handler()` which binds to the *calling* thread's Looper (crashing on worker threads).
v2 always posts to the **main** looper.

### 2.2 `URLRequest` -> `HttpRequest`

```java
// v1
URLRequest request = new URLRequest();
request.build(() -> Log.d("TAG", (String) request.getDefaultActionArg()));
request.execute("https://api.example.com/data", "GET");

// v2
HttpRequest.get("https://api.example.com/data")
        .header("Accept", "application/json")
        .timeoutMs(10_000)
        .enqueue(new HttpRequest.Callback() {
            @Override public void onSuccess(HttpRequest.Response r) {
                if (r.isSuccessful()) Log.d("TAG", r.body);
                else Log.w("TAG", "HTTP " + r.code + ": " + r.body);
            }
            @Override public void onError(Exception e) { Log.e("TAG", "network failure", e); }
        });

HttpRequest.post(url, "{\"a\":1}").header("Content-Type", "application/json").enqueue(callback);
```

* Callbacks run on the main thread. `execute()` is the blocking variant (background threads only).
* Status codes and error bodies are real fields instead of the `"Error: <code>"` string convention.
* The deprecated wrapper keeps the `"Error: ..."` convention, but the body now keeps line breaks
  (v1 concatenated lines without `\n`) and is decoded as UTF-8.
* `AsyncTask` is gone; requests use a bounded 4-thread pool with connect/read timeouts (default 15 s).

### 2.3 `EasySQL` -> `Database`

```java
// v1
EasySQL db = new EasySQL(context);
db.createTable("mydb", "users", new String[]{"id:INTEGER", "name:TEXT"});
db.insertToTable("mydb", "users", new String[]{"id:1", "name:Alice"});
db.deleteFromTable("mydb", "users", "id:1");
List<String> cells = db.getTableValues("mydb", "users");      // ["id:1", "name:'Alice'"]

// v2 (call off the main thread)
Database db = new Database(context, "mydb");
Map<String, String> cols = new LinkedHashMap<>();
cols.put("id", "INTEGER PRIMARY KEY");
cols.put("name", "TEXT");
db.createTable("users", cols);

Map<String, String> row = new LinkedHashMap<>();
row.put("id", "1");
row.put("name", "O'Brien: the \"first\"");   // quotes and colons are now safe
db.insert("users", row);

db.delete("users", "id", "1");
for (Map<String, Object> r : db.selectAll("users")) {
    long id = (Long) r.get("id");               // Long / Double / String / byte[] / null
    String name = (String) r.get("name");
}
db.inTransaction(() -> { /* several inserts, all-or-nothing */ });
db.close();                                      // when finished with the file
```

Behaviour changes (all fix bugs or injection holes):

* **SQL injection closed.** Values are bound as parameters. Table/column names must match
  `[A-Za-z_][A-Za-z0-9_]*`, otherwise `IllegalArgumentException` (the legacy wrapper logs and ignores it).
* Column type strings may only contain letters, digits, `_`, spaces, parentheses and commas
  (no quotes or semicolons), e.g. `INTEGER PRIMARY KEY`. `DEFAULT 'x'` is therefore not accepted.
* `createTable` uses `CREATE TABLE IF NOT EXISTS`.
* The connection is cached per `Database` instance instead of being opened/closed around every call.
* `deleteFromTable` no longer crashes on empty tables (v1 guessed the column type from the first row);
  values containing `:` are no longer truncated.
* The database file format is unchanged (version 1) - existing databases open as-is.
* `getTableValues*` still return the `col:value` / `col:'text'` strings through the legacy wrapper.

### 2.4 `Check` -> `EmailValidator`, `StringChecks`, `TimeLeft`, `Drawables`

```java
// v1
Check.Email.addValidDomainName("gmail");
Check.Email.addValidDomainExtension("com");
boolean ok = Check.Email.isValid("user@gmail.com");

// v2
EmailValidator validator = new EmailValidator.Builder()
        .addDomainName("gmail").addDomainExtension("com")
        .build();                                   // or .useFullDomain(true).addDomain("gmail.com")
boolean ok = validator.isValid("user@gmail.com");

StringChecks.hasSymbols(s);  StringChecks.hasNumbers(s);  StringChecks.hasSpaces(s);
double secs = TimeLeft.seconds(now, until);          // Date or long millis overloads
boolean same = Drawables.equalsBitmap(context, R.drawable.icon, bitmap);
```

Behaviour changes:

* **Security fix:** split mode previously looked only at the first two domain labels, so
  `user@gmail.com.evil.example` was accepted. v2 requires the domain to be exactly `name.extension`.
  Multi-label domains such as `co.uk` should use **full-domain mode**.
* Matching is case-insensitive.
* The validator is immutable and thread-safe; the old static sets were unsynchronised `HashSet`s.
* `Drawables.equalsBitmap` rasterises vector/shape drawables instead of returning `false`.

### 2.5 `Convert` -> `Converters`

```java
Converters.capitalize("john");              // "John"; null/"" are returned unchanged (v1 threw)
Converters.toBase64("hi");                  // NO_WRAP (v1: Base64.DEFAULT adds a trailing "\n")
Converters.fromBase64(b64);                 // null on invalid input; decoded as UTF-8
Converters.toInt("12", -1);                 // explicit fallback; no printStackTrace()
Converters.toMonthDayYear(date);            // "5/4/2026"  (was dateToMMDDYY)
Converters.toDayMonthYear(date);            // "4/5/2026"  (was dateToDDMMYY)
Converters.toYearMonthDay(date);            // "2026/5/4"  (was dateToYYMMDD)
Converters.formatDate(date, "dd MMM yyyy"); // any SimpleDateFormat pattern, Locale.US
```

The deprecated `java.util.Date.getYear()/getMonth()/getDate()` calls are gone. Output is identical.
If you stored v1 Base64 strings they still decode; strings **you generate** will no longer end in a newline.

### 2.6 `Cipher` -> `ClassicalCiphers`

```java
ClassicalCiphers.transposition(text);
ClassicalCiphers.caesar(text, shift);
ClassicalCiphers.keyword(text, keyword);
ClassicalCiphers.giovanni(text, keyword, "C");
```

Same outputs for valid input. Differences: any `int` shift works (v1 crashed on negative or >25 shifts);
`giovanni` throws `IllegalArgumentException` for a `keyLetter` that is not a single A-Z letter; non-letters
inside a keyword are ignored (v1 inserted them into the cipher alphabet); upper-casing is
`Locale.ROOT`-safe (v1 broke under the Turkish locale). The old `Cipher` name also clashed with `javax.crypto.Cipher`.

### 2.7 `FlippableImageView`

```java
// v1
new FlippableImageView(context, true, FlippableImageView.Speed.FAST);

// v2
FlippableImageView v = new FlippableImageView(context);
v.setReverseBackImage(true);
v.setSpeed(FlippableImageView.Speed.FAST);
```

```xml
<com.isaiahnoelpulidosalazar.inpsAndroid.FlippableImageView
    android:layout_width="120dp" android:layout_height="160dp"
    app:reverseBackImage="true"
    app:flipSpeed="fast" />   <!-- slow | normal | fast -->
```

Behaviour changes: see section 3.

### 2.8 `RoundedAlertDialog.hide()` -> `dismiss()`

`hide()` still calls `cancel()` (so cancel listeners fire). `dismiss()` does not. Both are safe before `show()`.

### 2.9 `SoundSynthesizer.playTone(int)` -> `playNote(int midi)`

The v1 index table (0-83 = octaves 1-7 laid out as `C D E F G A B C# D# F# G# A#`, 84 = C8) maps to MIDI as
`midi = 12 * (index / 12 + 2) + offset[index % 12]`; v1 index `0` (C1) is MIDI `24`. Use standard MIDI numbers
going forward (`60` = middle C, `69` = A4). Tracks are now created lazily (max 16 cached), not 85 up front.

---

## 3. Breaking changes in non-deprecated APIs

| Area | Change | What to do |
|------|--------|------------|
| `AreaCalculator.Point` | `x`, `y` are now `final` | Create a new `Point` instead of assigning fields |
| `AreaCalculator`, `Sort`, `Fullscreen`, `SoundSynthesizer` | now `final` with private constructors | Stop instantiating/subclassing (they were static-only) |
| `Memory<T>` | type parameter renamed from `GenericType`; `remove(T)` returns `boolean`; `removeAt` returns the element; out-of-range throws `IndexOutOfBoundsException` | Catch the superclass if you caught `ArrayIndexOutOfBoundsException`. **Bug fixes:** removing a missing element no longer deletes the last element; `contains(null)` no longer throws |
| `Sort.bogoSort` | throws `IllegalArgumentException` for arrays longer than 12 | Use a real sort (`Arrays.sort`) |
| `Sort.bucketSortUniform` | rejects `NaN` | - |
| `Sort` internals | quick/intro sort use insertion sort for small ranges; patience sort uses primitive arrays | Output is identical |
| `SimpleList` | `getData()` is deprecated and now returns a **read-only** list; item padding is applied to existing rows immediately; a single adapter is kept for the view's lifetime | Mutate through `addItem/addItems/setItems/removeItem/clear` |
| `RoundedLinearLayout`, `RoundedConstraintLayout` | default corner radius is **8dp** for every constructor (v1: 8 *pixels* from XML, 256dp pill from code) | Set `app:cornerRadius` explicitly if you relied on the old look |
| `RoundedAlertDialog` | `setTitle` takes `CharSequence`; `addView` **replaces** the previous content view (v1 crashed on a second call); click setters work in any order | - |
| `RoundedAlertDialog` layout | dialog content height is capped at 480dp (`layout_constraintHeight_max`) instead of an ineffective `0.75` percent | Edit `rounded_alert_dialog_layout.xml` in your app to override |
| `FlippableImageView` | animation uses `ViewPropertyAnimator`: `SLOW/NORMAL/FAST` = 600/300/150 ms for a full flip (v1: 5/15/30 degrees per frame) | - |
| | `setFrontImage/setBackImage` accept any `Drawable` or `Bitmap` and display immediately when that side is visible | - |
| | `setReverseBackImage` applies at once and mirrors via view scale (no bitmap copies); `getBackImage()` returns the *un-mirrored* bitmap | Use `getBackDrawable()` |
| | `instantFlip()` now swaps the image (v1 only rotated) | - |
| | `flip()` during a flip reverses smoothly; `isFlipped()` is the requested state | - |
| `Fullscreen` | `enable/disable` take `Activity` (an `AppCompatActivity` still works); implemented with `WindowInsetsControllerCompat`; calling `enable` makes the window edge-to-edge (`setDecorFitsSystemWindows(false)`) | Handle insets yourself in layouts that must avoid the bars |
| Resources | new attrs `reverseBackImage`, `flipSpeed` | - |

---

## 4. Gradle / dependency changes

```groovy
// build.gradle (your app)
implementation 'com.isaiahnoelpulidosalazar:inpsAndroid:2.0.0'
```

* The library now declares `appcompat`, `recyclerview` and `constraintlayout` as `api`, because its public
  classes extend their types. You can drop duplicate declarations if you only added them for this library.
* `material` stays an implementation dependency, but your app theme must still define the Material colour attributes
  used by the drawables (`colorPrimary`, `colorSurface`, `colorControlHighlight`) - use a `Theme.Material3.*` or
  `Theme.MaterialComponents.*` parent as before.
* `minSdk` is still 24; `compileSdk` 36. The library module no longer sets `targetSdk` (ignored for libraries).

---

## 5. Suggested migration order

1. Bump the dependency to `2.0.0` and build. Fix the compile errors from section 3 (usually none).
2. Run your tests; check `Check.Email` split-mode usage with multi-label domains and any layout using
   `RoundedLinearLayout`/`RoundedConstraintLayout` without `app:cornerRadius`.
3. Replace deprecated calls using the table in section 1 (Android Studio: *Analyze > Inspect Code > Deprecated API usage*).
4. Move all `Database` calls off the main thread (v1 `EasySQL` had the same blocking behaviour, but was easier to miss).
