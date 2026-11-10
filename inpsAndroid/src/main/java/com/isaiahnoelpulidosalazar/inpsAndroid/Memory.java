package com.isaiahnoelpulidosalazar.inpsAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A small list-like container with the v1 method names, now backed by {@link ArrayList}
 * (amortised O(1) add instead of copying the array twice per call).
 *
 * <p>v1 bugs fixed: removing a missing element used to drop the last element, removing from an
 * empty container threw {@link NegativeArraySizeException}, and {@code contains} threw on
 * {@code null} elements.</p>
 *
 * @param <T> element type
 */
public class Memory<T> implements Iterable<T> {

    private final List<T> storage = new ArrayList<>();

    public void add(T item) { storage.add(item); }

    public boolean contains(T item) { return storage.contains(item); }

    /** Removes the first occurrence of {@code item}. @return {@code true} if something was removed. */
    public boolean remove(T item) { return storage.remove(item); }

    /** @throws IndexOutOfBoundsException if {@code index} is out of range */
    public T removeAt(int index) { return storage.remove(index); }

    /** @throws IndexOutOfBoundsException if {@code index} is out of range */
    public T get(int index) { return storage.get(index); }

    public int count() { return storage.size(); }

    public boolean isEmpty() { return storage.isEmpty(); }

    public void clear() { storage.clear(); }

    /** @return an unmodifiable live view of the contents. */
    public List<T> toList() { return Collections.unmodifiableList(storage); }

    @Override
    public Iterator<T> iterator() { return toList().iterator(); }
}
