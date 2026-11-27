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
| `RoundedCheckBox` | `LinearLayout` (`Checkable`) | Modern `CARD`, `PILL`, or `MINIMAL` checkbox with custom-drawn checkmark and toggleable overshoot bounce animation | `app:inpsCheckStyle`, `app:inpsBounceAnimation`, `app:inpsCornerRadius` |

## RoundedCheckBox Styles & Bounce Toggle

- `RoundedCheckBox.Style.CARD` (`card`) — Rounded `12dp` card with hairline border and soft primary tint when checked
- `RoundedCheckBox.Style.PILL` (`pill`) — Full capsule pill container
- `RoundedCheckBox.Style.MINIMAL` (`minimal`) — Borderless row with ripple feedback
- `app:inpsBounceAnimation="true|false"` / `setBounceAnimationEnabled(boolean)` — Turns the overshoot bounce animation on or off

## Usage

```xml
<com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedLinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    app:inpsCornerRadius="16dp"
    app:inpsIsTranslucent="true">

    <com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedCheckBox
        android:id="@+id/cb_bounce"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:checked="true"
        android:text="Bounce animation enabled"
        app:inpsBounceAnimation="true"
        app:inpsCheckStyle="pill" />

    <com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedButton
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:text="Continue"
        app:inpsCornerRadius="12dp" />
</com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedLinearLayout>
```
