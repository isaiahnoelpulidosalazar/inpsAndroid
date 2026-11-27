# Migrating from v1 / v2 to 3.0.0

`inpsAndroid 3.0.0` is a breaking release that removes all `@Deprecated` v1 wrappers (`Actions`, `Check`, `Cipher`, `Convert`, v1 `EasySQL`, `URLRequest`), removes `ClassicalCiphers` and `Memory<T>`, reorganizes packages under `com.isaiahnoelpulidosalazar.inpsandroid` (`core`, `ui`, `edu`), and prefixes all library resources and attributes with `inps_` / `inps`.

## Dependency & Theme Requirements

```groovy
implementation 'com.github.isaiahnoelpulidosalazar:inpsAndroid:3.0.0'
```

- `minSdk` 24, `compileSdk` 36, Java 17
- `appcompat`, `recyclerview`, and `constraintlayout` are exposed as `api` dependencies; `com.google.android.material:material` is no longer a main library dependency
- Your app theme must define `colorPrimary`, `colorControlHighlight`, and `android:colorBackground` (AppCompat or Material theme)

## 1. Package & Class Mapping

| v1 | v2 (`...inpsAndroid`) | v3 (`...inpsandroid`) |
|---|---|---|
| `URLRequest` | `network.HttpRequest` | [`core.network.WebRequest`](./web-request) |
| `EasySQL` | `database.Database` | [`core.database.EasySQL`](./easy-sql) |
| `Convert` | `convert.Converters` | [`core.convert.Converters`](./converters) |
| `Check.Email` | `validation.EmailValidator` | [`core.validation.EmailDomainAllowList`](./email-domain-allow-list) |
| `Check.hasSymbols / hasNumbers / hasSpaces` | `validation.StringChecks` | [`core.validation.StringChecks`](./string-checks) |
| `Check.howMany*Left` | `time.TimeLeft` | [`core.time.TimeLeft`](./time-left) |
| `Check.equalDrawableAndBitmap` | `graphics.Drawables` | [`ui.Drawables`](./drawables) |
| `Actions` | `concurrent.MainThread` | [`core.concurrent.MainThread`](./main-thread) |
| `Actions` / system UI helpers | `Fullscreen` | [`ui.Fullscreen`](./fullscreen) |
| `Cipher` | `crypto.ClassicalCiphers` | **Removed** (use `javax.crypto`) |
| — | `RoundedButton`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`, `RoundedViewHelper`, `RoundedAlertDialog`, `SimpleList`, `FlippableImageView` | `ui.*` |
| — | `AreaCalculator`, `SoundSynthesizer` | `edu.*` |
| — | `Sort` | [`edu.sort.Sort`](./sort) |
| — | `Memory<T>` | **Removed** (use `java.util.ArrayList<T>`) |

## 2. Example Migrations (v1 -> v3)

```java
// v1 Actions -> v3 MainThread
Actions a = new Actions().setArg("Hello").build(() -> show((String) a.getArg()));
a.executeDelayed(500);
// v3:
MainThread.postDelayed(() -> show("Hello"), 500);

// v1 Convert.toRealName -> v3 Converters.capitalize
String cap = Converters.capitalize("inpsAndroid");

// v1 EasySQL.doesTableExist -> v3 EasySQL.tableExists (background thread)
boolean exists = easySQL.tableExists("notes");
```

## 3. API & Behavior Changes

- **`WebRequest`**: Bounded to 4 threads and a queue capacity of 64 (delivers `RejectedExecutionException` to `Callback.onError` on the main thread when full); `enqueue()` returns a cancellable `WebRequest.Call`; added `json(String)` and `maxResponseBytes(int)` (8 MB default); parses response `charset` with UTF-8 fallback; never sets `doOutput(true)` for `GET`, `HEAD`, or bodyless requests.
- **`EasySQL`**: Added identifier-validated `tableExists(String)` and documented blocking disk I/O across all methods.
- **`Converters` & `EmailDomainAllowList`**: `Converters.toBase64(null)` returns `null`; `formatDate()` caches `SimpleDateFormat` per thread/pattern and resets `TimeZone.getDefault()`. `EmailDomainAllowList` validates a non-empty local part without whitespace and a single `@`.
- **`Sort`**: `pigeonholeSort` merged into `countingSort` (supports negative integers, 10M range cap); `timsort` removed (use `Arrays.sort`); all methods throw `NullPointerException` on `null` input instead of returning `null`.
- **`AreaCalculator`**: Removed `parallelogramArea` (use `rectangleArea`), `rightTriangleArea` (use `triangleArea`), and `squareAsRhombusArea` (use `squareArea(side) / 2`).
- **`FlippableImageView`**: Removed `toBitmap`; renamed `setFrontImage(Bitmap)` / `setBackImage(Bitmap)` to `setFrontBitmap(Bitmap)` / `setBackBitmap(Bitmap)`; uses `.withLayer()` and `onSaveInstanceState`/`onRestoreInstanceState` for `flipped`.
- **`SimpleList`**: Extends `FrameLayout` wrapping a `RecyclerView` with `ListAdapter`, `DiffUtil`, and `TextView` rows; `setItems()` snapshots incoming collections before clearing.
- **`Rounded*` & `RoundedAlertDialog`**: Consolidated into `RoundedViewHelper`; `RoundedLinearLayout` and `RoundedConstraintLayout` clip children to their rounded outline; `RoundedButton` picks black/white text from background luminance and respects explicit XML `android:background`; `RoundedAlertDialog` uses `RoundedButton`, detaches `addView()` children from any prior parent, and supports post-`show()` `setCancelable()`.
- **`Fullscreen`**: Saves and restores per-`Window` `decorFitsSystemWindows`, system bar visibility, and AppCompat `ActionBar` state; added `reapplyOnWindowFocusChanged(Activity, boolean)`.
- **`Drawables`**: Copies `HARDWARE` and null-config bitmaps to `ARGB_8888` before pixel comparison and catches `IllegalArgumentException`.
- **`SoundSynthesizer`**: Asynchronous `prepare(int...)`, `playNote`, and `release` on a dedicated single-thread executor; `AudioTrack.PERFORMANCE_MODE_LOW_LATENCY` on API 26+.

## 4. XML Attributes & Resources (`inps_`)

| v2 | v3 |
|---|---|
| `app:cornerRadius` | `app:inpsCornerRadius` |
| `app:isTranslucent` | `app:inpsIsTranslucent` |
| `app:reverseBackImage` | `app:inpsReverseBackImage` |
| `app:flipSpeed` | `app:inpsFlipSpeed` |
| `generic_rounded_primary_background` | `inps_rounded_primary_background` |
| `generic_rounded_translucent_background` | `inps_rounded_translucent_background` |
| `rounded_alert_dialog_background` | `inps_rounded_alert_dialog_background` |
| `rounded_alert_dialog_layout` | `inps_rounded_alert_dialog_layout` |
| `simple_list_item` / `simple_list_item_background` | `inps_simple_list_item` / `inps_simple_list_item_background` |
| `top_border` / `bottom_border` | `inps_top_border` / `inps_bottom_border` |
| `@color/black`, `@color/white` (`colors.xml`) | **Removed** |
