# Memory<T>

`Memory<T>` is a small list-like container backed by `ArrayList`. It keeps the v1 method names while adding iteration support.

## Purpose & Scope

The purpose of this architecture is to provide:
- Simple generic collection primitives with amortised O(1) `add`
- Common operations (`add`, `contains`, `remove`, `get`, `count`, `clear`) plus `isEmpty` and `toList`
- `Iterable<T>` support for use in for-each loops

Compared to v1, removing a missing element no longer drops the last element, removing from an empty container no longer throws `NegativeArraySizeException`, and `contains(null)` no longer throws.

## API Reference

### Memory<T>()

Creates an empty container.

```java
Memory<String> memory = new Memory<>();
```

### void add(T item)

Appends an element.

```java
memory.add("item1");
```

### boolean contains(T item)

Checks whether the container holds an element equal to the given item (`null` is allowed).

```java
boolean exists = memory.contains("item1");
```

### boolean remove(T item)

Removes the first occurrence. Returns `true` if something was removed.

```java
boolean removed = memory.remove("item1");
```

### T removeAt(int index)

Removes and returns the element at the zero-based index. Throws `IndexOutOfBoundsException` if out of range.

```java
String removed = memory.removeAt(0);
```

### T get(int index)

Returns the element at the zero-based index. Throws `IndexOutOfBoundsException` if out of range.

```java
String item = memory.get(0);
```

### int count()

Returns the number of stored elements.

```java
int size = memory.count();
```

### boolean isEmpty()

Returns `true` when the container holds no elements.

```java
boolean empty = memory.isEmpty();
```

### void clear()

Removes all elements.

```java
memory.clear();
```

### List<T> toList()

Returns an unmodifiable live view of the contents.

```java
List<String> view = memory.toList();
```

### Iterator<T> iterator()

Iterates over the unmodifiable view, enabling for-each loops.

```java
for (String s : memory) Log.d("TAG", s);
```
