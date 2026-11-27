# Migrating to 2.0.0

`com.isaiahnoelpulidosalazar:inpsAndroid:2.0.0` is source-compatible for almost everything. Every moved or replaced v1 class remains as a `@Deprecated` wrapper, so you can bump the version, fix the few breaking changes, and migrate warnings gradually. Wrappers are planned for removal in `3.0.0`.

## Purpose & Scope

This page provides:
- A quick map from deprecated APIs to replacements
- The behaviour changes you should check before upgrading
- Gradle and theme requirements

For full per-class detail, see [Legacy API](./legacy-api) and each module page.

## Dependency

```groovy
implementation 'com.isaiahnoelpulidosalazar:inpsAndroid:2.0.0'
```

- `appcompat`, `recyclerview`, and `constraintlayout` are exposed as `api`, so you can remove duplicate declarations
- `material` is an `implementation` dependency, but your theme must still provide `colorPrimary`, `colorSurface`, and `colorControlHighlight`
- `minSdk` 24, `compileSdk` 36

## Deprecated API Replacements

| v1 (deprecated) | v2 replacement |
|---|---|
| `Actions` | [`MainThread.post / postDelayed / run`](./main-thread) |
| `URLRequest` | [`HttpRequest`](./http-request) |
| `EasySQL` | [`Database`](./database) |
| `Check.Email` | [`EmailValidator`](./email-validator) |
| `Check.hasSymbols / hasNumbers / hasSpaces` | [`StringChecks`](./string-checks) |
| `Check.howMany*Left` | [`TimeLeft`](./time-left) |
| `Check.equalDrawableAndBitmap` | [`Drawables.equalsBitmap`](./drawables) |
| `Convert` | [`Converters`](./converters) |
| `Cipher` | [`ClassicalCiphers`](./classical-ciphers) |
| `FlippableImageView` extra constructors | Standard constructors + `setSpeed` / `setReverseBackImage` / XML attributes |
| `FlippableImageView.getFrontImage / getBackImage` | `getFrontDrawable / getBackDrawable` |
| `RoundedAlertDialog.hide()` | `dismiss()` |
| `SimpleList.getData()` | `getItems()` (read-only) |
| `SoundSynthesizer.playTone(int)` | `playNote(int midiNote)` |

## Example: Actions to MainThread

```java
// v1
Actions a = new Actions().setArg("Hello").build(() -> show((String) a.getArg()));
a.executeDelayed(500);

// v2
String msg = "Hello";
MainThread.postDelayed(() -> show(msg), 500);
```

## Breaking Changes in Non-Deprecated APIs

| Area | Change |
|---|---|
| `AreaCalculator.Point` | `x` and `y` are now `final` |
| `AreaCalculator`, `Sort`, `Fullscreen`, `SoundSynthesizer` | Now `final`; static-only classes have private constructors |
| `Memory<T>` | Type parameter renamed; `remove` returns `boolean`; `removeAt` returns the element; out-of-range throws `IndexOutOfBoundsException`; removal and `contains(null)` bugs fixed |
| `Sort.bogoSort` | Throws for arrays longer than 12 |
| `Sort.bucketSortUniform` | Rejects `NaN` |
| `SimpleList` | `getData()` is deprecated and read-only; padding applies to existing rows; a single adapter is kept |
| `RoundedLinearLayout`, `RoundedConstraintLayout` | Default corner radius is **8dp** for every constructor (v1: 8px from XML, 256dp from code). Set `app:cornerRadius` if you relied on the old look |
| `RoundedAlertDialog` | `setTitle` takes `CharSequence`; `addView` replaces the previous content; setters work in any order; content height capped at 480dp |
| `FlippableImageView` | Duration-based animation (600/300/150 ms); accepts any `Drawable`/`Bitmap`; `instantFlip()` now swaps the image; `setReverseBackImage` applies immediately |
| `Fullscreen` | Takes `Activity`; uses `WindowInsetsControllerCompat`; `enable` makes the window edge-to-edge |

## Notable Behaviour Changes

- **Security:** `EmailValidator` split mode no longer accepts `user@gmail.com.evil.example`. Use full-domain mode for multi-label domains such as `co.uk`.
- **SQL injection closed:** `Database` binds values as parameters and validates identifiers and column types.
- **Base64:** `Converters.toBase64` uses `NO_WRAP`. Previously stored v1 strings still decode.
- **Main thread:** `MainThread` always targets the main looper, never the calling thread's looper.
- **Database format:** unchanged (version 1), so existing files open as-is.

## Suggested Migration Order

1. Bump the dependency to `2.0.0` and build, then fix any compile errors from the table above.
2. Run your tests. Check split-mode email validation with multi-label domains and any rounded layouts missing `app:cornerRadius`.
3. Replace deprecated calls using the replacement table (Android Studio: *Analyze > Inspect Code > Deprecated API usage*).
4. Move all `Database` calls off the main thread.
