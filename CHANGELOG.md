# Changelog

## 3.0.4

Quality, tooling, and resource-prefix hardening release introducing Checkstyle, JaCoCo test coverage reporting, updated dependencies, and zero-warning Lint/javac compliance.

### Breaking Changes
- **Prefixed `<declare-styleable>` Groups (`inps_attrs.xml`)**: Renamed all 7 styleable declarations to satisfy `resourcePrefix 'inps_'`: `InpsRoundedButton`, `InpsRoundedEditText`, `InpsRoundedConstraintLayout`, `InpsRoundedLinearLayout`, `InpsRoundedCheckBox`, `InpsSimpleList`, and `InpsFlippableImageView`. XML attributes (`app:inps*`) are unchanged; only direct `R.styleable.*` references require updating.

### Quality, Tooling & Fixes
- **Checkstyle Enforcement**: Added `config/checkstyle/checkstyle.xml` (Google-based 4-space ruleset) and a manually registered `checkstyle` verification task across modules with `maxWarnings = 0` and `-Xlint:deprecation -Xlint:unchecked`.
- **Unit Test Coverage & CI**: Enabled `enableUnitTestCoverage = true` for `:inpsAndroid` debug builds and updated `.github/workflows/test_inpsAndroid.yml` to run `./gradlew lint checkstyle`, `./gradlew test :inpsAndroid:createDebugUnitTestCoverageReport`, and upload both test/lint/checkstyle reports and coverage artifacts.
- **API 36 & Lint Cleanup**: Updated `RoundedCheckBox` accessibility state handling for API 36 (`BAKLAVA`), replaced `SimpleList` `notifyDataSetChanged()` calls with `notifyItemRangeChanged(...)`, fixed `[InflateParams]` in `RoundedAlertDialog` / `RoundedBottomSheet`, and cleared all `:app` Lint and `UiWidgetsTest` Robolectric `4.18.1` issues.
- **Dependency Updates**: Centralized dependencies in `gradle/libs.versions.toml` and updated AndroidX (`appcompat:1.8.0`, `recyclerview:1.4.0`, `constraintlayout:2.2.2`, `viewpager2:1.1.0`), Material (`1.14.0`), AndroidX Test Core (`1.7.0`), and Robolectric (`4.18.1` / `15-robolectric-14663667-2-i7`).

## 3.0.3

Maintenance release fixing Robolectric test runtime artifact fetching in CI and offline environments.

### Fixes & Build Tooling
- **Robolectric Offline SDK Resolution (`inpsAndroid/build.gradle`)**: Added a dedicated `robolectricSdk` Gradle configuration (`org.robolectric:android-all-instrumented:15-robolectric-12650502-i7`) synced into `build/robolectric-deps` by `prepareRobolectricSdk` and wired `robolectric.offline=true` + `robolectric.dependency.dir` into unit test tasks, eliminating runtime `MavenArtifactFetcher` / `HttpURLConnection` `IOException` failures in `MainThreadTest`, `ConvertersTest`, and other Robolectric suites.

## 3.0.2

Documentation & polish release adding comprehensive Android Studio Javadoc and XML comments across the entire codebase without modifying runtime APIs.

### Documentation & Tooling
- **Full Codebase Javadoc**: Enriched all public classes, constructors, interfaces, callbacks, enum constants, and methods across `core`, `ui`, `edu`, and the `:app` demo with `@param`, `@return`, and `@throws` tags for Android Studio Quick Documentation, Parameter Info, and Maven `withJavadocJar()` generation.
- **XML Attribute & Manifest Comments**: Documented all custom attributes and `<declare-styleable>` groups in `inps_attrs.xml` and the `POST_NOTIFICATIONS` permission in `AndroidManifest.xml`.

## 3.0.1

Feature release expanding the `core` and `ui` toolkits without breaking `3.0.0` APIs.

### New Components & Enhancements
- **`EasyNotification`** (`core.notification`): Custom notification dispatcher managing API 26+ channels, API 33+ `POST_NOTIFICATIONS` permission checks (`RESULT_BLOCKED` fallback + `DispatchListener`), fluent styles (`bigText`, `bigPicture`, `inboxLines`), progress bars, actions, and `FLAG_IMMUTABLE` `tapActivity()` helpers.
- **`RoundedBottomSheet`** (`ui`): Builder-style modal bottom sheet with top-rounded corners, outline clipping, toggleable drag handle, swipe-to-dismiss gestures, system navigation bar and IME inset handling, scrollable body content (`addView` / `setMessage`), and dual `RoundedButton` actions.
- **`RoundedCheckBox`** (`ui`): Checkable rounded widget supporting `CARD`, `PILL`, and `MINIMAL` styles (`app:inpsCheckStyle`) and a user-toggleable overshoot bounce animation (`app:inpsBounceAnimation`).
- **`SimpleList`** (`ui`): Added optional multi-select `RoundedCheckBox` rows (`app:inpsCheckboxesEnabled`), bounce animation toggle forwarding, style customization, and selection tracking helpers alongside classic `TextView` rows.

## 3.0.0

Breaking release reorganizing packages, removing legacy/duplicate APIs, prefixing resources and attributes, and hardening concurrency, networking, UI, and validation components.

### Package & Namespace Changes
- Aligned Java source packages to lowercase `com.isaiahnoelpulidosalazar.inpsandroid` (matching the Gradle `namespace`).
- Reorganized classes by audience:
  - `com.isaiahnoelpulidosalazar.inpsandroid.core`: `concurrent.MainThread`, `convert.Converters`, `validation.EmailDomainAllowList`, `validation.StringChecks`, `time.TimeLeft`, `network.WebRequest`, `database.EasySQL`
  - `com.isaiahnoelpulidosalazar.inpsandroid.ui`: `RoundedButton`, `RoundedEditText`, `RoundedLinearLayout`, `RoundedConstraintLayout`, `RoundedAlertDialog`, `SimpleList`, `FlippableImageView`, `Fullscreen`, `Drawables`
  - `com.isaiahnoelpulidosalazar.inpsandroid.edu`: `AreaCalculator`, `SoundSynthesizer`, `sort.Sort`

### Renames & Removals
- Renamed `HttpRequest` to `WebRequest` (`core.network`).
- Renamed `Database` to `EasySQL` (`core.database`).
- Renamed `EmailValidator` to `EmailDomainAllowList` (`core.validation`).
- Removed the `crypto` package (`ClassicalCiphers`).
- Removed `Memory<T>`.
- Removed duplicate `AreaCalculator` formulas (`parallelogramArea`, `rightTriangleArea`, `squareAsRhombusArea`) and precomputed `sqrt(3)`.
- Removed `Sort.pigeonholeSort` (merged negative-supporting offset histogram logic into `Sort.countingSort`) and `Sort.timsort`.
- Removed `FlippableImageView.toBitmap` and renamed `setFrontImage(Bitmap)` / `setBackImage(Bitmap)` to `setFrontBitmap(Bitmap)` / `setBackBitmap(Bitmap)` to eliminate `null` overload ambiguity.
- Removed unused `colors.xml` (`black`, `white`).
- Dropped `com.google.android.material:material` from main library dependencies; drawables now use AppCompat and framework attributes (`?android:attr/colorBackground`, `?attr/colorPrimary`, `?attr/colorControlHighlight`).

### Resource & Attribute Prefixes (`resourcePrefix 'inps_'`)
- Renamed drawables:
  - `bottom_border` -> `inps_bottom_border`
  - `top_border` -> `inps_top_border`
  - `generic_rounded_primary_background` -> `inps_rounded_primary_background`
  - `generic_rounded_translucent_background` -> `inps_rounded_translucent_background`
  - `rounded_alert_dialog_background` -> `inps_rounded_alert_dialog_background`
  - `simple_list_item_background` -> `inps_simple_list_item_background`
- Renamed layouts & IDs:
  - `rounded_alert_dialog_layout` -> `inps_rounded_alert_dialog_layout` (with `inps_rounded_alert_dialog_title`, `inps_rounded_alert_dialog_view`, `inps_rounded_alert_dialog_left_button`, `inps_rounded_alert_dialog_right_button`)
  - `simple_list_item` -> `inps_simple_list_item` (`TextView` id `inps_simple_list_item_text`)
- Prefixed XML attributes:
  - `cornerRadius` -> `inpsCornerRadius`
  - `isTranslucent` -> `inpsIsTranslucent`
  - `reverseBackImage` -> `inpsReverseBackImage`
  - `flipSpeed` -> `inpsFlipSpeed`

### Behaviour Changes & Historical v1 -> v2 Notes Moved from Javadoc
- **WebRequest**:
  - Bounded worker pool at 4 threads and queue at 64; reports `RejectedExecutionException` via `Callback.onError` on the main thread when full.
  - `enqueue()` returns a `WebRequest.Call` handle (`cancel()` interrupts the worker and suppresses callbacks on both worker and main threads).
  - `GET`, `HEAD`, and bodyless requests no longer call `setDoOutput(true)`.
  - Added `json(String)` (`Content-Type: application/json; charset=utf-8`), default `text/plain; charset=utf-8` for bodies without an explicit `Content-Type`, `maxResponseBytes(int)` (default 8 MB), and response `charset` parsing with UTF-8 fallback.
  - Historical: replaced the `AsyncTask`-based `URLRequest` from v1.
- **EasySQL**:
  - `tableExists(String)` now validates the table identifier via the same path as all other methods.
  - Historical: v1 opened and closed the SQLite file multiple times per call; v2/v3 retains a single lazily opened `SQLiteOpenHelper` with on-disk version 1.
- **Converters**:
  - `toBase64(null)` now returns `null`.
  - `formatDate()` caches `SimpleDateFormat` per thread and pattern in a `ThreadLocal` and resets `TimeZone.getDefault()` on each call.
  - Historical: v1 `toBase64` used `Base64.DEFAULT` (which appended `\n`).
- **EmailDomainAllowList**:
  - Added local-part validation requiring a non-empty local part with no whitespace and exactly one `@`.
  - Historical: v1 split mode only checked the first two labels, accepting domains such as `user@gmail.com.evil.example`.
- **MainThread**:
  - Historical: v1 allocated an implicitly bound `Handler` per call (crashing on threads without a `Looper`).
- **SimpleList**:
  - Now extends `FrameLayout` wrapping an internal `RecyclerView` with `ListAdapter` and `DiffUtil` and `TextView` rows instead of `Button` rows.
  - `setItems()` snapshots the incoming collection before clearing so `setItems(getItems())` no longer empties the list.
  - Historical: v1 created a new adapter on every mutation, resetting scroll position.
- **Rounded Views & RoundedAlertDialog**:
  - Shared attribute parsing, background creation, ripple, and outline setup consolidated into `RoundedViewHelper`.
  - `RoundedLinearLayout` and `RoundedConstraintLayout` enable `setClipToOutline(true)` with `ViewOutlineProvider.BACKGROUND`.
  - `RoundedButton` chooses black or white text from background luminance and respects explicit XML `android:background`.
  - `RoundedAlertDialog` uses `RoundedButton` in a single-level layout, detaches views from any existing parent in `addView()`, applies `setCancelable()` after `show()`, and no longer hardcodes `@color/white`.
  - Historical: v1 `RoundedAlertDialog` threw `NullPointerException` if click listeners were configured before button setup.
- **FlippableImageView**:
  - Added `.withLayer()` to the flip animation and `onSaveInstanceState` / `onRestoreInstanceState` support for `flipped`.
  - Historical: v1 re-posted per-frame `Handler` runnables, threw `ClassCastException` on non-bitmap drawables, and `instantFlip()` failed to swap the drawable.
- **Fullscreen**:
  - Saves and restores previous `decorFitsSystemWindows`, system bar visibility, and AppCompat action bar visibility per `Window`; added `reapplyOnWindowFocusChanged(Activity, boolean)`.
  - Historical: v1 used deprecated `SYSTEM_UI_FLAG_*` flags and relied on catching an NPE when no ActionBar was present.
- **Drawables**:
  - `equalsBitmap` copies `HARDWARE` or null-config bitmaps to `ARGB_8888` before comparison and catches `IllegalArgumentException` to return `false`.
  - Historical: v1 returned `false` unconditionally for non-bitmap drawables.
- **Sort**:
  - Split into family helpers under `edu.sort`.
  - Every public method now throws `NullPointerException` on `null` input instead of returning `null`.
  - `countingSort` supports negative integers and enforces the 10,000,000 range cap; `beadSort` enforces the 10,000,000 max-value cap before allocation; `bucketSortUniform` returns a new sorted array without mutating its input.
- **SoundSynthesizer**:
  - Track synthesis and playback run asynchronously on a dedicated single-thread executor; added `prepare(int...)`, `AudioTrack.PERFORMANCE_MODE_LOW_LATENCY` on API 26+, return-code checks, and `0.5` amplitude scaling.
  - Historical: v1 eagerly allocated 85 native `AudioTrack`s in the constructor on the calling thread.
