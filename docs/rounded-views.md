# Rounded Views

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

Shared attribute parsing, rounded background creation, ripple feedback, and outline clipping live in `RoundedViewHelper`.

## Components

| View | Base Class | Default Style | XML Attributes |
|---|---|---|---|
| `RoundedButton` | `AppCompatButton` | Primary color pill with contrast-aware ripple and black/white text; preserves explicit `android:background` | `app:inpsCornerRadius` |
| `RoundedEditText` | `AppCompatEditText` | Translucent pill background (`?attr/colorControlHighlight`) | `app:inpsCornerRadius` |
| `RoundedLinearLayout` | `LinearLayout` | Rounded `8dp` container with `setClipToOutline(true)` | `app:inpsCornerRadius`, `app:inpsIsTranslucent` |
| `RoundedConstraintLayout` | `ConstraintLayout` | Rounded `8dp` container with `setClipToOutline(true)` | `app:inpsCornerRadius`, `app:inpsIsTranslucent` |

## Usage

```xml
<com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedLinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    app:inpsCornerRadius="16dp"
    app:inpsIsTranslucent="true">

    <com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Name"
        app:inpsCornerRadius="12dp" />

    <com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedButton
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:text="Continue"
        app:inpsCornerRadius="12dp" />
</com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedLinearLayout>
```
