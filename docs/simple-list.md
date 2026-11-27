# SimpleList

Package: `com.isaiahnoelpulidosalazar.inpsandroid.ui`

`SimpleList` extends `FrameLayout` and encapsulates an internal `RecyclerView` backed by `ListAdapter` and `DiffUtil`. Rows are rendered as ripple-enabled `TextView` items (`inps_simple_list_item`).

## Features

- `setItems(Collection<String>)` snapshots the incoming collection before clearing, so `list.setItems(list.getItems())` is safe
- `getItems()` returns an unmodifiable live view of the current items
- `addItem`, `addItems`, `removeItem`, and `clear` submit incremental diffs without resetting scroll position
- Supports `OnItemClickListener`, `OnItemLongClickListener`, and `setItemPadding(dp)` / `setItemPadding(l, t, r, b)`

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.ui.SimpleList;

SimpleList list = findViewById(R.id.simple_list);
list.setItemPadding(14, 12, 14, 12);
list.setItems(Arrays.asList("Overview", "Core", "UI", "Edu"));

list.setOnItemClickListener(pos -> {
    String selected = list.getItems().get(pos);
});
list.setOnItemLongClickListener(pos -> list.removeItem(pos));
```
