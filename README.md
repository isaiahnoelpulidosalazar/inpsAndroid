# Project Overview

Welcome to the **inpsAndroid** documentation (version `2.0.0`). This library eliminates boilerplate across Android projects by providing reusable components, safe SQLite and HTTP helpers, threading utilities, validation tools, and UI widgets.

```groovy
implementation 'com.github.isaiahnoelpulidosalazar:inpsAndroid:2.0.0'
```

## Purpose & Scope

The purpose of this architecture is to provide:
- A lightweight Android utility layer reducing common boilerplate code across Java/Android projects
- Safe and straightforward abstractions for local storage, threading, validation, and network communication
- Drop-in UI widgets with configurable rounded aesthetics and animations
- Source compatibility with v1 through `@Deprecated` wrappers that delegate to the new implementations (planned for removal in `3.0.0`)

### Core Architecture Components

1. **Concurrency & Network**: `MainThread` for main-looper posting and `HttpRequest` for background HTTP with main-thread callbacks.
2. **Persistence**: `Database`, a parameterised SQLite wrapper with validated identifiers and transactions.
3. **Validation & Conversion**: `EmailValidator`, `StringChecks`, `TimeLeft`, `Drawables`, and `Converters`.
4. **Algorithms & Math**: `ClassicalCiphers`, `Sort`, `AreaCalculator`, and `Memory`.
5. **Custom Visual & Audio Components**: Rounded views, `RoundedAlertDialog`, `SimpleList`, `FlippableImageView`, `Fullscreen`, and `SoundSynthesizer`.

### Package Layout

| Package | Contents |
|---|---|
| `com.isaiahnoelpulidosalazar.inpsAndroid` | UI widgets, `Sort`, `AreaCalculator`, `Memory`, `Fullscreen`, `SoundSynthesizer`, and the deprecated v1 wrappers |
| `...inpsAndroid.concurrent` | `MainThread` |
| `...inpsAndroid.network` | `HttpRequest` |
| `...inpsAndroid.database` | `Database` |
| `...inpsAndroid.validation` | `EmailValidator`, `StringChecks` |
| `...inpsAndroid.time` | `TimeLeft` |
| `...inpsAndroid.graphics` | `Drawables` |
| `...inpsAndroid.convert` | `Converters` |
| `...inpsAndroid.crypto` | `ClassicalCiphers` |

### Requirements

- `minSdk` 24, `compileSdk` 36, Java 17
- Your app theme must define the Material attributes used by the drawables (`colorPrimary`, `colorSurface`, `colorControlHighlight`), so use a `Theme.Material3.*` or `Theme.MaterialComponents.*` parent
- `appcompat`, `recyclerview`, and `constraintlayout` are exposed as `api` dependencies; `material` is an `implementation` dependency

## Quick Navigation

You can browse documentation sets by topic using the sidebar. The following modules are available:

**Core utilities**
- [MainThread](./main-thread) — Shared main-looper poster with immediate, delayed, and cancel support
- [HttpRequest](./http-request) — Async HTTP client with main-thread callbacks
- [Database](./database) — Safe SQLite wrapper with transactions
- [Converters](./converters) — Base64, date formatting, and safe numeric parsing
- [EmailValidator](./email-validator) — Immutable email domain allow-list validator
- [StringChecks](./string-checks) — Null-safe symbol, digit, and space checks
- [TimeLeft](./time-left) — Fractional time remaining between two instants
- [Drawables](./drawables) — Pixel-level drawable/bitmap comparison
- [ClassicalCiphers](./classical-ciphers) — Transposition, Caesar, Keyword, and Giovanni ciphers

**Algorithms & data**
- [Sort](./sort) — Sorting algorithm implementations
- [AreaCalculator](./area-calculator) — 2D area and 3D surface-area formulas
- [Memory](./memory) — Generic list-like container

**UI & media**
- [Rounded Views](./rounded-views) — RoundedButton, RoundedConstraintLayout, RoundedEditText, RoundedLinearLayout
- [RoundedAlertDialog](./rounded-alert-dialog) — Custom rounded dialog with dual action buttons
- [SimpleList](./simple-list) — Vertical button list backed by RecyclerView
- [FlippableImageView](./flippable-image-view) — 3D card-flip image component
- [Fullscreen](./fullscreen) — Immersive fullscreen toggle
- [SoundSynthesizer](./sound-synthesizer) — MIDI-addressed synthesized piano notes

**Upgrading**
- [Legacy API](./legacy-api) — Deprecated v1 classes (`Actions`, `Check`, `Cipher`, `Convert`, `EasySQL`, `URLRequest`)
- [Migrating to 2.0.0](./migrating-to-v2) — Replacement table, behaviour changes, and Gradle notes
