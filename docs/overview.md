# Project Overview

Welcome to the **inpsAndroid** documentation (version `3.0.3`). This library eliminates boilerplate across Android projects by providing core networking, SQLite, notification, threading, conversion, and validation utilities alongside drop-in UI widgets and educational algorithm/audio modules.

```groovy
implementation 'com.github.isaiahnoelpulidosalazar:inpsAndroid:3.0.3'
```

## Purpose & Scope

The purpose of this architecture is to provide:
- Lightweight core Android primitives (`core`) for main-thread dispatch, bounded HTTP requests, parameterized SQLite storage, validation, conversions, and time calculations
- Drop-in UI widgets (`ui`) with shared `RoundedViewHelper` styling, `inps_`-prefixed resources, outline clipping, and animations
- Educational modules (`edu`) for sorting algorithms, closed-form geometry formulas, and asynchronous MIDI-addressed piano synthesis

### Core Architecture Components

1. **Concurrency & Network (`core`)**: `MainThread` for main-looper posting and `WebRequest` for bounded background HTTP with main-thread callbacks and cancellable `Call` handles.
2. **Persistence & Notifications (`core`)**: `EasySQL` (parameterized SQLite wrapper with validated identifiers, transactions, and `tableExists()`) and `EasyNotification` (channel-aware notification dispatcher with permission guards and rich styles).
3. **Validation, Conversion & Time (`core`)**: `EmailDomainAllowList`, `StringChecks`, `TimeLeft`, and `Converters`.
4. **UI & Graphics (`ui`)**: `RoundedButton`, `RoundedCheckBox`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`, `RoundedAlertDialog`, `RoundedBottomSheet`, `SimpleList`, `FlippableImageView`, `Fullscreen`, and `Drawables`.
5. **Algorithms, Math & Audio (`edu`)**: `Sort`, `AreaCalculator`, and `SoundSynthesizer`.

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
- Your app theme must inherit from an AppCompat (or Material) theme defining `colorPrimary`, `colorControlHighlight`, and `android:colorBackground`
- `appcompat`, `recyclerview`, and `constraintlayout` are exposed as `api` dependencies (`com.google.android.material:material` is not required by the library)

## Quick Navigation

You can browse documentation sets by topic using the sidebar. The following modules are available:

**Core utilities**
- [MainThread](./main-thread) — Shared main-looper poster with immediate, delayed, and cancel support
- [WebRequest](./web-request) — Bounded async HTTP client with `Call.cancel()`, `json()`, and `maxResponseBytes()`
- [EasySQL](./easy-sql) — Parameterized SQLite wrapper with transactions and `tableExists()`
- [EasyNotification](./easy-notification) — Channel-aware notification dispatcher with API 33+ permission guards and rich styles
- [Converters](./converters) — Null-safe Base64, `ThreadLocal` date formatting, and fallback numeric parsing
- [EmailDomainAllowList](./email-domain-allow-list) — Immutable email domain allow-list validator
- [StringChecks](./string-checks) — Null-safe symbol, digit, and space checks
- [TimeLeft](./time-left) — Fractional time remaining between two instants

**UI & graphics**
- [Rounded Views](./rounded-views) — `RoundedButton`, `RoundedCheckBox` (`CARD` / `PILL` / `MINIMAL` + bounce toggle), `RoundedConstraintLayout`, `RoundedEditText`, `RoundedLinearLayout`
- [RoundedAlertDialog](./rounded-alert-dialog) — Custom rounded dialog with dual `RoundedButton` actions
- [RoundedBottomSheet](./rounded-bottom-sheet) — Draggable top-rounded modal bottom sheet with dual `RoundedButton` actions
- [SimpleList](./simple-list) — `FrameLayout`-wrapped `RecyclerView` with `ListAdapter`, `DiffUtil`, `TextView` rows, and optional `RoundedCheckBox` mode
- [FlippableImageView](./flippable-image-view) — 3D card-flip image component with `Speed` and state restoration
- [Fullscreen](./fullscreen) — Per-window edge-to-edge immersive fullscreen toggle
- [Drawables](./drawables) — Pixel-level drawable/bitmap comparison (including `HARDWARE` bitmaps)

**Algorithms, math & audio**
- [Sort](./sort) — Comparison, non-comparison, and structure-based sorting algorithms
- [AreaCalculator](./area-calculator) — 2D area and 3D surface-area formulas
- [SoundSynthesizer](./sound-synthesizer) — Asynchronous MIDI-addressed synthesized piano notes

**Upgrading**
- [Migrating to 3.0.0](./migrating-to-v3) — v1/v2 replacement table, removed APIs, `inps_` prefixes, and behaviour changes
