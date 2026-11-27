# SimpleList

`SimpleList` is a `RecyclerView` subclass that renders a vertical list of string items as buttons, with click, long-click, and padding customization.

## Purpose & Scope

The purpose of this component is to provide:
- Rapid construction of vertical list menus
- A single adapter for the view's lifetime with fine-grained change notifications, preserving scroll position and item animations
- Listeners read at click time, so they can be swapped without rebuilding anything

Mutate the list only through the methods below. The list returned by `getItems()` is read-only.

## API Reference

### SimpleList(Context context)

Creates the list. `(Context, AttributeSet)` and `(Context, AttributeSet, int)` constructors are available for XML inflation.

```java
SimpleList list = new SimpleList(context);
```

### void addItem(String item)

Appends an item.

```java
list.addItem("Apple");
```

### void addItems(Collection<String> newItems)

Appends several items with one range notification.

```java
list.addItems(Arrays.asList("Banana", "Cherry"));
```

### void setItems(Collection<String> newItems)

Replaces all items.

```java
list.setItems(Arrays.asList("One", "Two"));
```

### void removeItem(int position)

Removes the item at the position. Throws `IndexOutOfBoundsException` if out of range.

```java
list.removeItem(0);
```

### void clear()

Removes all items.

```java
list.clear();
```

### List<String> getItems()

Returns a read-only live view of the items.

```java
List<String> items = list.getItems();
```

### void setOnItemClickListener(OnItemClickListener listener)

Registers a callback invoked with the tapped position.

```java
list.setOnItemClickListener(position -> {
    String item = list.getItems().get(position);
});
```

### void setOnItemLongClickListener(OnItemLongClickListener listener)

Registers a callback invoked with the held position. The long press is consumed when a listener is set.

```java
list.setOnItemLongClickListener(position -> { /* handle long press */ });
```

### void setItemPadding(int dp) / setItemPadding(int left, int top, int right, int bottom)

Sets uniform or per-side padding (in dp) for item buttons. Applies to existing rows immediately.

```java
list.setItemPadding(12);
```

### List<String> getData()

**Deprecated.** Alias of `getItems()`; the returned list is now read-only.

```java
List<String> data = list.getData();
```
