# inpsAndroid

Welcome to the **inpsAndroid** documentation (version `3.0.0`). This library eliminates boilerplate across Android projects by providing core networking, SQLite, threading, conversion, and validation utilities alongside drop-in UI widgets and educational algorithm/audio modules.

```groovy
implementation 'com.github.isaiahnoelpulidosalazar:inpsAndroid:3.0.0'
```

## Purpose & Scope

**inpsAndroid** is organized into three audience-focused packages under `com.isaiahnoelpulidosalazar.inpsandroid`:

- **`core`** — Lightweight Android primitives for main-thread dispatch, bounded HTTP requests, parameterized SQLite storage, date/Base64/numeric conversions, validation, and time calculations
- **`ui`** — Configurable rounded widgets (`Rounded*`), `RoundedAlertDialog`, `SimpleList`, `FlippableImageView`, `Fullscreen`, and `Drawables`
- **`edu`** — Sorting algorithm implementations (`Sort`), geometric area/surface-area formulas (`AreaCalculator`), and MIDI-addressed piano synthesis (`SoundSynthesizer`)

> **Breaking changes in `3.0.0`:** Deprecated v1 wrappers, `crypto` (`ClassicalCiphers`), and `Memory<T>` have been removed, packages have been reorganized under lowercase `com.isaiahnoelpulidosalazar.inpsandroid`, and XML resources/attributes use the `inps_` / `inps` prefix. See [CHANGELOG.md](./CHANGELOG.md) for the complete migration summary.

### Package Layout

| Package | Contents |
|---|---|
| `...inpsandroid.core.concurrent` | `MainThread` |
| `...inpsandroid.core.network` | `WebRequest` |
| `...inpsandroid.core.database` | `EasySQL` |
| `...inpsandroid.core.convert` | `Converters` |
| `...inpsandroid.core.validation` | `EmailDomainAllowList`, `StringChecks` |
| `...inpsandroid.core.time` | `TimeLeft` |
| `...inpsandroid.ui` | `RoundedButton`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`, `RoundedViewHelper`, `RoundedAlertDialog`, `SimpleList`, `FlippableImageView`, `Fullscreen`, `Drawables` |
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
- **`Converters`** — Null-safe Base64 encoding/decoding, thread-safe cached date formatting, and safe numeric parsing.
- **`EmailDomainAllowList`** — Immutable email domain allow-list validator with local-part and domain checks.
- **`StringChecks`** — Null-safe symbol, digit, and whitespace checks.
- **`TimeLeft`** — Fractional time remaining between two instants.

### UI (`...inpsandroid.ui`)
- **Rounded Views** (`RoundedButton`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`) — Shared styling via `RoundedViewHelper` supporting `app:inpsCornerRadius`, `app:inpsIsTranslucent` (on layouts), outline clipping on rounded containers, luminance-based text contrast on `RoundedButton`, and explicit XML `android:background` preservation.
- **`RoundedAlertDialog`** — Rounded dual-action dialog using `RoundedButton` and theme-aware colors, supporting `addView`, `setCancelable`, and button click listeners.
- **`SimpleList`** — `FrameLayout`-wrapped `RecyclerView` using `ListAdapter` and `DiffUtil` with ripple-enabled `TextView` rows and snapshot-safe `setItems`.
- **`FlippableImageView`** — 3D card-flip image view with `app:inpsReverseBackImage`, `app:inpsFlipSpeed` (`slow` | `normal` | `fast`), `setFrontBitmap`/`setBackBitmap`, and instance-state persistence.
- **`Fullscreen`** — Per-window edge-to-edge/immersive fullscreen controller that saves and restores previous system bar and action bar visibility.
- **`Drawables`** — Pixel-level drawable and bitmap comparison (handling `HARDWARE` bitmaps safely).

### Educational & Media (`...inpsandroid.edu`)
- **`Sort`** (`...edu.sort.Sort`) — Comparison, non-comparison (including negative-capable `countingSort`), and structure-based sorting algorithms.
- **`AreaCalculator`** — 2D area and 3D surface-area formulas.
- **`SoundSynthesizer`** — Asynchronous MIDI-addressed piano note synthesis (`prepare(int...)`, `playNote`, `stopNote`, `release`) using low-latency `AudioTrack` on API 26+.

## Testing

Run the library unit and Robolectric test suite with:

```bash
gradle :inpsAndroid:testDebugUnitTest
```

See [CHANGELOG.md](./CHANGELOG.md) for all `3.0.0` changes and historical v1/v2 notes, and [LICENSE](./LICENSE) for licensing terms.
