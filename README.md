# inpsAndroid

Welcome to the **inpsAndroid** documentation (version `3.0.3`). This library eliminates boilerplate across Android projects by providing core networking, SQLite, notifications, threading, conversion, and validation utilities alongside drop-in UI widgets and educational algorithm/audio modules.

```groovy
implementation 'com.github.isaiahnoelpulidosalazar:inpsAndroid:3.0.3'
```

## Purpose & Scope

**inpsAndroid** is organized into three audience-focused packages under `com.isaiahnoelpulidosalazar.inpsandroid`:

- **`core`** — Lightweight Android primitives for main-thread dispatch, bounded HTTP requests, parameterized SQLite storage, date/Base64/numeric conversions, validation, and time calculations
- **`ui`** — Configurable rounded widgets (`Rounded*`), `RoundedAlertDialog`, `RoundedBottomSheet`, `SimpleList`, `FlippableImageView`, `Fullscreen`, and `Drawables`
- **`edu`** — Sorting algorithm implementations (`Sort`), geometric area/surface-area formulas (`AreaCalculator`), and MIDI-addressed piano synthesis (`SoundSynthesizer`)

> **Breaking changes in `3.0.0`:** Deprecated v1 wrappers, `crypto` (`ClassicalCiphers`), and `Memory<T>` have been removed, packages have been reorganized under lowercase `com.isaiahnoelpulidosalazar.inpsandroid`, and XML resources/attributes use the `inps_` / `inps` prefix. See [CHANGELOG.md](./CHANGELOG.md) for the complete migration summary.

### Package Layout

| Package | Contents |
|---|---|
| `...inpsandroid.core.concurrent` | `MainThread` |
| `...inpsandroid.core.network` | `WebRequest` |
| `...inpsandroid.core.database` | `EasySQL` |
| `...inpsandroid.core.notification` | `EasyNotification` |
| `...inpsandroid.core.convert` | `Converters` |
| `...inpsandroid.core.validation` | `EmailDomainAllowList`, `StringChecks` |
| `...inpsandroid.core.time` | `TimeLeft` |
| `...inpsandroid.ui` | `RoundedButton`, `RoundedCheckBox`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`, `RoundedViewHelper`, `RoundedAlertDialog`, `RoundedBottomSheet`, `SimpleList`, `FlippableImageView`, `Fullscreen`, `Drawables` |
| `...inpsandroid.edu` | `AreaCalculator`, `SoundSynthesizer` |
| `...inpsandroid.edu.sort` | `Sort` |

### Requirements

- `minSdk` 24, `compileSdk` 36, Java 17
- App theme must inherit from an AppCompat (or Material) theme defining `colorPrimary`, `colorControlHighlight`, and `android:colorBackground`
- `androidx.appcompat:appcompat`, `androidx.recyclerview:recyclerview`, and `androidx.constraintlayout:constraintlayout` are exposed as `api` dependencies (`material` is not required by the library)

## Module Highlights

### Core (`...inpsandroid.core`)
- **`MainThread`** — Shared main-looper poster supporting immediate, delayed, and cancellable runnables.
- **`WebRequest`** — Async HTTP client backed by a 4-thread pool and a bounded queue of 64, returning a cancellable `WebRequest.Call` handle with `json(String)`, configurable `maxResponseBytes(int)` (default 8 MB), charset-aware decoding, and main-thread callbacks.
- **`EasySQL`** — Parameterized SQLite wrapper with validated identifiers, transactions, and `tableExists(String)` (all methods block on disk I/O).
- **`EasyNotification`** — Channel-aware notification dispatcher with API 33+ `POST_NOTIFICATIONS` checks, `BigText`/`BigPicture`/`Inbox` styles, actions, and `tapActivity()` helpers.
- **`Converters`** — Null-safe Base64 encoding/decoding, thread-safe cached date formatting, and safe numeric parsing.
- **`EmailDomainAllowList`** — Immutable email domain allow-list validator with local-part and domain checks.
- **`StringChecks`** — Null-safe symbol, digit, and whitespace checks.
- **`TimeLeft`** — Fractional time remaining between two instants.

### UI (`...inpsandroid.ui`)
- **Rounded Views** (`RoundedButton`, `RoundedCheckBox`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`) — Shared styling via `RoundedViewHelper` supporting `app:inpsCornerRadius`, `app:inpsIsTranslucent` (on layouts), outline clipping on rounded containers, luminance-based text contrast on `RoundedButton`, and explicit XML `android:background` preservation. `RoundedCheckBox` adds `CARD`, `PILL`, and `MINIMAL` styles (`app:inpsCheckStyle`) plus a toggleable bounce animation (`app:inpsBounceAnimation`).
- **`RoundedAlertDialog`** — Rounded dual-action dialog using `RoundedButton` and theme-aware colors, supporting `addView`, `setCancelable`, and button click listeners.
- **`RoundedBottomSheet`** — Top-rounded modal bottom sheet with an optional drag handle, swipe-to-dismiss gesture, edge-to-edge inset padding, `addView`/`setMessage` content, and dual `RoundedButton` actions.
- **`SimpleList`** — `FrameLayout`-wrapped `RecyclerView` using `ListAdapter` and `DiffUtil` with ripple-enabled `TextView` rows, snapshot-safe `setItems`, and optional multi-select `RoundedCheckBox` rows (`app:inpsCheckboxesEnabled`, `app:inpsCheckStyle`, `app:inpsBounceAnimation`).
- **`FlippableImageView`** — 3D card-flip image view with `app:inpsReverseBackImage`, `app:inpsFlipSpeed` (`slow` | `normal` | `fast`), `setFrontBitmap`/`setBackBitmap`, and instance-state persistence.
- **`Fullscreen`** — Per-window edge-to-edge/immersive fullscreen controller that saves and restores previous system bar and action bar visibility.
- **`Drawables`** — Pixel-level drawable and bitmap comparison (handling `HARDWARE` bitmaps safely).

### Educational & Media (`...inpsandroid.edu`)
- **`Sort`** (`...edu.sort.Sort`) — Comparison, non-comparison (including negative-capable `countingSort`), and structure-based sorting algorithms.
- **`AreaCalculator`** — 2D area and 3D surface-area formulas.
- **`SoundSynthesizer`** — Asynchronous MIDI-addressed piano note synthesis (`prepare(int...)`, `playNote`, `frequencyOf`, `release`) using low-latency `AudioTrack` on API 26+.

## Migrating from v1 / v2 to 3.0.0

Version `3.0.0` removes all `@Deprecated` v1 wrappers (`Actions`, `Check`, `Cipher`, `Convert`, v1 `EasySQL`, `URLRequest`) and reorganizes the entire library under `com.isaiahnoelpulidosalazar.inpsandroid` (`core`, `ui`, `edu`).

### 1. Class & Package Mapping

| v1 | v2 (`...inpsAndroid`) | v3 (`...inpsandroid`) |
|---|---|---|
| `URLRequest` | `network.HttpRequest` | `core.network.WebRequest` |
| `EasySQL` | `database.Database` | `core.database.EasySQL` |
| `Convert` | `convert.Converters` | `core.convert.Converters` |
| `Check` | `validation.EmailValidator` | `core.validation.EmailDomainAllowList` |
| `Check` | `validation.StringChecks` | `core.validation.StringChecks` |
| `Actions` | `concurrent.MainThread` | `core.concurrent.MainThread` |
| `Actions` | `Fullscreen` | `ui.Fullscreen` |
| `Cipher` | `crypto.ClassicalCiphers` | **Removed** (use `javax.crypto`) |
| — | `time.TimeLeft` | `core.time.TimeLeft` |
| — | `graphics.Drawables` | `ui.Drawables` |
| — | `RoundedButton`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`, `RoundedViewHelper`, `RoundedAlertDialog`, `SimpleList`, `FlippableImageView` | `ui.*` |
| — | `AreaCalculator`, `SoundSynthesizer` | `edu.*` |
| — | `Sort` | `edu.sort.Sort` |
| — | `Memory<T>` | **Removed** (use `java.util.ArrayList<T>`) |

### 2. Removed Methods & API Changes

- **`Sort`**: `pigeonholeSort` was merged into `countingSort` (now supports negative integers with a `10_000_000` range cap), and `timsort` was removed (use `Arrays.sort`). All `Sort` methods now throw `NullPointerException` on `null` input instead of returning `null`.
- **`AreaCalculator`**: Removed formula duplicates `parallelogramArea` (use `rectangleArea`), `rightTriangleArea` (use `triangleArea`), and `squareAsRhombusArea` (use `squareArea(side) / 2`).
- **`FlippableImageView`**: Removed `toBitmap`. Renamed `setFrontImage(Bitmap)` and `setBackImage(Bitmap)` to `setFrontBitmap(Bitmap)` and `setBackBitmap(Bitmap)` to eliminate `null` overload ambiguity.
- **`SimpleList`**: Now wraps a `RecyclerView` inside a `FrameLayout` instead of extending `RecyclerView` directly (use `getRecyclerView()` if needed). List rows now use `TextView` instead of `Button`, and `setItems()` snapshots incoming collections before updating via `ListAdapter`/`DiffUtil`.
- **`WebRequest`**: Bounded to 4 worker threads and a queue capacity of 64; `enqueue()` returns a cancellable `WebRequest.Call`; added `json(String)` and `maxResponseBytes(int)`.
- **`Converters` & `EmailDomainAllowList`**: `Converters.toBase64(null)` returns `null`. `EmailDomainAllowList` requires a non-empty local part without whitespace and a single `@`.

### 3. XML Attributes & Resource Prefix (`inps_`)

All library resources and custom attributes are now prefixed to avoid collisions (`resourcePrefix 'inps_'`):

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
| `@color/black`, `@color/white` (`colors.xml`) | **Removed** (use `@android:color/black`, `@android:color/white`, or app theme colors) |

## Documentation Pages

Browse the per-module Markdown reference pages in [`docs/`](./docs/README.md):

- **Core:** [MainThread](./docs/main-thread.md) · [WebRequest](./docs/web-request.md) · [EasySQL](./docs/easy-sql.md) · [EasyNotification](./docs/easy-notification.md) · [Converters](./docs/converters.md) · [EmailDomainAllowList](./docs/email-domain-allow-list.md) · [StringChecks](./docs/string-checks.md) · [TimeLeft](./docs/time-left.md)
- **UI:** [Rounded Views](./docs/rounded-views.md) · [RoundedAlertDialog](./docs/rounded-alert-dialog.md) · [RoundedBottomSheet](./docs/rounded-bottom-sheet.md) · [SimpleList](./docs/simple-list.md) · [FlippableImageView](./docs/flippable-image-view.md) · [Fullscreen](./docs/fullscreen.md) · [Drawables](./docs/drawables.md)
- **Edu:** [Sort](./docs/sort.md) · [AreaCalculator](./docs/area-calculator.md) · [SoundSynthesizer](./docs/sound-synthesizer.md)
- **Upgrading:** [Migrating from v1 / v2 to 3.0.0](./docs/migrating-to-v3.md)

## Testing

Run the library unit and Robolectric test suite with:

```bash
gradle :inpsAndroid:testDebugUnitTest
```

See [TESTING.md](./TESTING.md) for instructions on writing and running JUnit 4 and Robolectric tests, [CONTRIBUTING.md](./CONTRIBUTING.md) for issue and pull request guidelines, [CHANGELOG.md](./CHANGELOG.md) for the complete `3.0.0` changelog and historical v1/v2 notes, and [LICENSE](./LICENSE) for licensing terms.
