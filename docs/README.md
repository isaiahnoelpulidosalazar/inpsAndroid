# inpsAndroid Documentation (`3.0.2`)

Welcome to the **inpsAndroid** `3.0.2` documentation set.

## Core (`com.isaiahnoelpulidosalazar.inpsandroid.core`)
- [MainThread](./main-thread.md) — Shared main-looper poster (`post`, `postDelayed`, `run`, `cancel`)
- [WebRequest](./web-request.md) — Bounded async HTTP client with `Call.cancel()`, `json()`, and `maxResponseBytes()`
- [EasySQL](./easy-sql.md) — Parameterized SQLite wrapper with validated identifiers, transactions, and `tableExists()`
- [EasyNotification](./easy-notification.md) — Channel-aware notification dispatcher with permission guards and rich styles
- [Converters](./converters.md) — Null-safe Base64, `ThreadLocal` date formatting, and fallback numeric parsing
- [EmailDomainAllowList](./email-domain-allow-list.md) — Immutable email allow-list validator
- [StringChecks](./string-checks.md) — Null-safe symbol, digit, and space checks
- [TimeLeft](./time-left.md) — Fractional time remaining between two instants

## UI (`com.isaiahnoelpulidosalazar.inpsandroid.ui`)
- [Rounded Views](./rounded-views.md) — `RoundedButton`, `RoundedCheckBox`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`
- [RoundedAlertDialog](./rounded-alert-dialog.md) — Builder-style rounded dialog with `RoundedButton` actions
- [RoundedBottomSheet](./rounded-bottom-sheet.md) — Draggable modal bottom sheet with drag handle, insets handling, and `RoundedButton` actions
- [SimpleList](./simple-list.md) — `FrameLayout`-wrapped `RecyclerView` with `ListAdapter`, `DiffUtil`, `TextView` rows, and optional `RoundedCheckBox` mode
- [FlippableImageView](./flippable-image-view.md) — 3D card-flip image view with `Speed` and state restoration
- [Fullscreen](./fullscreen.md) — Per-window immersive fullscreen helper preserving prior window state
- [Drawables](./drawables.md) — Pixel-level drawable and `Bitmap` comparison (including `HARDWARE` bitmaps)

## Edu (`com.isaiahnoelpulidosalazar.inpsandroid.edu`)
- [Sort](./sort.md) — Comparison, non-comparison, and structure sorting algorithms
- [AreaCalculator](./area-calculator.md) — 2D area and 3D surface-area formulas
- [SoundSynthesizer](./sound-synthesizer.md) — Asynchronous MIDI-addressed piano note synthesis

## Upgrading
- [Migrating from v1 / v2 to 3.0.0](./migrating-to-v3.md) — Package mapping, removed APIs, and `inps_` resource prefixes
