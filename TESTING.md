# Testing Guide (`inpsAndroid`)

`inpsAndroid` uses **JUnit 4** (`4.13.2`) and **Robolectric** (`4.15.1`) to run all library tests—including SQLite, HTTP, `Looper`, `View`, and resource-dependent components—directly on the JVM without an Android emulator.

## 1. Gradle Test Configuration

Configured in [`inpsAndroid/build.gradle`](./inpsAndroid/build.gradle):

```groovy
android {
    testOptions {
        unitTests {
            includeAndroidResources = true
            all {
                dependsOn 'prepareRobolectricSdk'
                systemProperty 'robolectric.offline', 'true'
                systemProperty 'robolectric.dependency.dir', layout.buildDirectory.dir('robolectric-deps').get().asFile.absolutePath
            }
        }
    }
}

dependencies {
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.robolectric:robolectric:4.15.1'
    testImplementation 'androidx.test:core:1.6.1'
    testImplementation 'com.google.android.material:material:1.12.0'
    robolectricSdk 'org.robolectric:android-all-instrumented:15-robolectric-12650502-i7'
}
```

- `includeAndroidResources = true` allows Robolectric to inflate library layouts (`inps_rounded_alert_dialog_layout`, `inps_simple_list_item`), load `inps_` drawables, and resolve `inps_attrs.xml`.
- `robolectricSdk` + `prepareRobolectricSdk` resolve and cache the Robolectric Android 15 runtime jar via Gradle (`~/.gradle`) and run tests with `robolectric.offline=true`, preventing `MavenArtifactFetcher` `HttpURLConnection` network failures during test execution.
- `com.google.android.material:material` is scoped to `testImplementation` only—it is used to test Material3 themes alongside AppCompat themes without pulling Material into the library's main dependencies.

## 2. Running Tests

### Run all library unit tests

```bash
# In standard local environments or CI:
./gradlew :inpsAndroid:testDebugUnitTest

# Or for all modules:
./gradlew test
```

> **Note:** In container environments where the Gradle wrapper is disabled, run `gradle :inpsAndroid:testDebugUnitTest` or `gradle test` directly.

### Run a specific test class or method

```bash
./gradlew :inpsAndroid:testDebugUnitTest \
  --tests "com.isaiahnoelpulidosalazar.inpsandroid.core.network.WebRequestTest"

./gradlew :inpsAndroid:testDebugUnitTest \
  --tests "com.isaiahnoelpulidosalazar.inpsandroid.ui.UiWidgetsTest.testSimpleListSetItemsWithGetItemsAndTextViewRows"
```

### Viewing Reports & CI

- Local HTML test report: `inpsAndroid/build/reports/tests/testDebugUnitTest/index.html`
- GitHub Actions workflow ([`.github/workflows/test_inpsAndroid.yml`](./.github/workflows/test_inpsAndroid.yml)) runs `./gradlew test` on every push and pull request and uploads the `test-reports` artifact.

## 3. Test Suite Layout

Test packages under `inpsAndroid/src/test/java/com/isaiahnoelpulidosalazar/inpsandroid/` mirror the main source tree:

| Package | Test File | Runner |
|---|---|---|
| `core.concurrent` | `MainThreadTest.java` | Robolectric (`sdk = 35`) |
| `core.convert` | `ConvertersTest.java` | Robolectric (`sdk = 35`) |
| `core.database` | `EasySQLTest.java` | Robolectric (`sdk = 35`) |
| `core.network` | `WebRequestTest.java` | Robolectric (`sdk = 35`) |
| `core.time` | `TimeLeftTest.java` | Plain JUnit 4 |
| `core.validation` | `ValidationTest.java` | Plain JUnit 4 |
| `ui` | `DrawablesTest.java`, `UiWidgetsTest.java` | Robolectric (`sdk = 35`) |
| `edu` | `AreaCalculatorTest.java` | Plain JUnit 4 |
| `edu` | `SoundSynthesizerTest.java` | Robolectric (`sdk = 35`) |
| `edu.sort` | `SortTest.java` | Plain JUnit 4 |

## 4. Writing New Tests

### Plain JVM Tests (Pure Java Utilities)

Classes with no Android framework dependencies (`AreaCalculator`, `Sort`, `TimeLeft`, `EmailDomainAllowList`, `StringChecks`) should use plain JUnit 4 for maximum speed:

```java
package com.isaiahnoelpulidosalazar.inpsandroid.edu;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ExampleMathTest {
    @Test
    public void testCircleArea() {
        assertEquals(Math.PI * 25.0, AreaCalculator.circleArea(5.0), 1e-6);
    }
}
```

### Robolectric Tests (Android Framework & UI)

Annotate Android-dependent test classes with:

```java
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class ExampleAndroidTest {
    // ...
}
```

Use these established project conventions when testing Android components:

1. **Themed Contexts for Views & Drawables**
   Wrap `ApplicationProvider.getApplicationContext()` in a `ContextThemeWrapper` providing either `androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar` or `com.google.android.material.R.style.Theme_Material3_Light_NoActionBar`:
   ```java
   Context themed = new ContextThemeWrapper(
           ApplicationProvider.getApplicationContext(),
           androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar
   );
   ```

2. **Draining the Main Looper (`MainThread`, `SimpleList`, `Fullscreen`)**
   Advance or flush queued main-thread work using Robolectric shadows:
   ```java
   Shadows.shadowOf(Looper.getMainLooper()).idle();
   // Or advance virtual time for delayed tasks / ActionBar animations:
   Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(500));
   ```

3. **Networking (`WebRequestTest`)**
   - Use an ephemeral local `java.net.ServerSocket(0, 16, InetAddress.getByName("127.0.0.1"))` rather than `com.sun.net.httpserver.HttpServer` (which is unavailable on the Android compile classpath) and never hit external hosts.
   - Inject test executors with package-private `WebRequest.setExecutor(...)` (for example, to simulate `RejectedExecutionException` when the queue is full) and always reset `WebRequest.setExecutor(null)` in `@Before` and `@After`.

4. **SQLite (`EasySQLTest`)**
   Construct `new EasySQL(ApplicationProvider.getApplicationContext(), "test_inps.db")` and close it in `@After`.

5. **Custom Views & Layouts (`UiWidgetsTest`)**
   To inspect rendered rows inside `SimpleList`, call `measure(...)` and `layout(...)` after idling the main looper, then inspect `list.getRecyclerView().getChildAt(...)`.

6. **Bitmaps & Audio (`DrawablesTest`, `SoundSynthesizerTest`)**
   - To construct a `Bitmap.Config.HARDWARE` instance under Robolectric SDK 35, record into an `android.graphics.Picture` and call `Bitmap.createBitmap(picture, width, height, Bitmap.Config.HARDWARE)`.
   - In Robolectric, `AudioTrack` instances in `MODE_STATIC` remain `STATE_UNINITIALIZED`, so tests for `SoundSynthesizer` should verify frequency math, MIDI range validation, `prepare(...)`, and post-`release()` safety without asserting on native playback completion.
