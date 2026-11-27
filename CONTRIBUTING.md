# Contributing to inpsAndroid

Thank you for your interest in contributing to **inpsAndroid**! This guide outlines how to report bugs, propose enhancements, and submit pull requests.

## 1. Reporting Issues

Before opening a new issue, please search existing issues and check [CHANGELOG.md](./CHANGELOG.md) and [docs/migrating-to-v3.md](./docs/migrating-to-v3.md) to verify whether an API was renamed or removed in `3.0.0`.

### Bug Reports
Include as much detail as possible so maintainers can reproduce and fix the issue quickly:
- **Library version** (e.g., `3.0.0`) and **Android `minSdk` / target device API level**
- **Affected module/class** (e.g., `core.network.WebRequest`, `ui.SimpleList`)
- **Minimal reproduction** (Java/XML snippet or failing JUnit/Robolectric test)
- **Expected vs. actual behavior**, including relevant stack traces or logcat output

### Feature & Enhancement Requests
- Explain the use case and why it fits within **inpsAndroid** (`core`, `ui`, or `edu`).
- Prefer lightweight, zero-bloat designs that work cleanly on `minSdk 24+` without introducing heavy transitive dependencies.

## 2. Development Setup

- **JDK:** Java 17 (Temurin or OpenJDK 17)
- **Android SDK:** `compileSdk 36`, `minSdk 24`
- **Modules:**
  - `:inpsAndroid` — the Android library module (`com.isaiahnoelpulidosalazar.inpsandroid`)
  - `:app` — the light-themed, tabbed docs & interactive playground demo app

## 3. Coding & Architecture Conventions

Please follow the repository's existing structure and constraints when modifying `:inpsAndroid`:

- **Package organization:** Place new code in the appropriate audience package under `com.isaiahnoelpulidosalazar.inpsandroid`:
  - `core` (`concurrent`, `convert`, `database`, `network`, `time`, `validation`)
  - `ui` (views, dialogs, graphics helpers)
  - `edu` (educational algorithms, geometry formulas, audio synthesis)
- **Resource & attribute prefixing:** `:inpsAndroid` enforces `resourcePrefix 'inps_'`. Prefix all library drawables, layouts, IDs with `inps_` and custom XML attributes in `inps_attrs.xml` with `inps`.
- **Dependencies:** Keep `com.google.android.material:material` out of `:inpsAndroid`'s main dependencies (`implementation`/`api`). Library drawables and widgets should rely on `androidx.appcompat`, `androidx.recyclerview`, `androidx.constraintlayout`, and standard Android/AppCompat theme attributes (`colorPrimary`, `colorControlHighlight`, `android:colorBackground`).
- **Safety & threading:**
  - Validate SQL identifiers and bind parameters in `EasySQL`.
  - Document blocking I/O or background thread expectations in Javadoc.
  - Keep utility classes `final` with a `private` constructor throwing `UnsupportedOperationException("No instances")`.
- **No external network calls in tests:** Use local sockets (`ServerSocket`) or injected test executors (`WebRequest.setExecutor`) rather than hitting live URLs.

## 4. Submitting Pull Requests

1. **Fork & branch** from `main` using a clear branch name (e.g., `fix/webrequest-charset` or `docs/add-examples`).
2. **Keep changes focused:** Limit each PR to a single bug fix, feature, or documentation update.
3. **Write or update tests:** Every bug fix or new feature in `:inpsAndroid` should include JUnit 4 or Robolectric unit tests in the mirrored test package. See [TESTING.md](./TESTING.md) for conventions and commands.
4. **Update documentation:** If your change modifies public classes, methods, or XML attributes, update the corresponding Markdown files under [`docs/`](./docs/README.md), [README.md](./README.md), and [CHANGELOG.md](./CHANGELOG.md).
5. **Verify locally before opening the PR:**

   ```bash
   ./gradlew test
   ./gradlew assembleRelease
   ```

   Both checks must pass cleanly—GitHub Actions runs `./gradlew test` on pull requests ([`.github/workflows/test_inpsAndroid.yml`](./.github/workflows/test_inpsAndroid.yml)) and `./gradlew test assembleRelease` on pushes ([`.github/workflows/build_inpsAndroid.yml`](./.github/workflows/build_inpsAndroid.yml)).
6. **Open a Pull Request** describing *what* changed, *why*, and how it was tested, linking any related issues.

## License

By contributing to **inpsAndroid**, you agree that your contributions will be licensed under the [MIT License](./LICENSE).
