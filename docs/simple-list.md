# SimpleList

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

`SimpleList` extends `FrameLayout` and encapsulates an internal `RecyclerView` backed by `ListAdapter` and `DiffUtil`. It supports classic ripple-enabled `TextView` rows (`inps_simple_list_item`) as well as an optional modern `RoundedCheckBox` mode (`inps_simple_list_checkbox_item`) with a toggleable bounce animation.

## Features

- **Classic & Checkbox Modes**: Toggle with `setCheckboxesEnabled(boolean)` or `app:inpsCheckboxesEnabled="true|false"`
- **Modern Styles & Bounce Toggle**: Configure `setCheckStyle(RoundedCheckBox.Style)`, `setCheckboxCornerRadiusDp(float)`, and `setBounceAnimationEnabled(boolean)` (or `app:inpsBounceAnimation`, `app:inpsCheckStyle`, `app:inpsCornerRadius`)
- **Selection APIs**: `setChecked(pos, checked)`, `isChecked(pos)`, `setCheckedItems(Collection<String>)`, `getCheckedItems()`, `getCheckedPositions()`, `checkAll()`, `clearChecked()`, and `setOnItemCheckedChangeListener`
- `setItems(Collection<String>)` snapshots the incoming collection before clearing (so `list.setItems(list.getItems())` is safe) and preserves checked state
- `getItems()` returns an unmodifiable live view of the current items
- `addItem`, `addItems`, `removeItem`, and `clear` submit incremental diffs without resetting scroll position

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.ui.RoundedCheckBox;
import com.isaiahnoelpulidosalazar.inpsandroid.ui.SimpleList;

SimpleList list = findViewById(R.id.simple_list);
list.setCheckboxesEnabled(true);
list.setCheckStyle(RoundedCheckBox.Style.CARD);
list.setBounceAnimationEnabled(true);
list.setItems(Arrays.asList("Overview", "Core", "UI", "Edu"));

list.setOnItemCheckedChangeListener((pos, item, isChecked) -> {
    List<String> selected = list.getCheckedItems();
});
```
