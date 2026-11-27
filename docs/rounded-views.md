# Rounded Views

A collection of custom Android view widgets featuring configurable corner radii, themed backgrounds, and interactive effects.

## Purpose & Scope

The purpose of these custom view classes is to provide:
- Clean XML and programmatic configuration of rounded backgrounds and corner radii
- Solid (`colorPrimary`) or translucent (`colorControlHighlight`) background modes
- Luminance-aware ripple feedback on buttons

All views share one background factory. Theme colors come from your app theme, so it must define `colorPrimary` and `colorControlHighlight`.

## XML Attributes

| Attribute | Applies to | Description |
|---|---|---|
| `app:cornerRadius` | all four views | Corner radius as a dimension. Layouts default to **8dp**; button and edit text keep the drawable's pill radius (256dp) unless set |
| `app:isTranslucent` | `RoundedConstraintLayout`, `RoundedLinearLayout` | Uses the translucent background when `true` (default `false`) |

```xml
<com.isaiahnoelpulidosalazar.inpsAndroid.RoundedLinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cornerRadius="16dp"
    app:isTranslucent="true" />
```

## Components

### RoundedButton
Custom `AppCompatButton` with a rounded primary-colour background and a ripple whose colour (light or dark) is chosen from the background luminance.

```java
RoundedButton btn = new RoundedButton(context);
```

### RoundedConstraintLayout
Custom `ConstraintLayout` with configurable rounded corners and solid or translucent background modes.

```java
RoundedConstraintLayout layout = new RoundedConstraintLayout(context);
```

### RoundedEditText
Custom `AppCompatEditText` with a translucent, pill-shaped background and optional corner radius.

```java
RoundedEditText editText = new RoundedEditText(context);
```

### RoundedLinearLayout
Custom `LinearLayout` with rounded corners and solid or translucent backgrounds.

```java
RoundedLinearLayout layout = new RoundedLinearLayout(context);
```
