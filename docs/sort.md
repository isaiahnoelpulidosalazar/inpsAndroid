# Sort

The `Sort` utility provides an educational collection of sorting algorithms over `int[]` (and `double[]` for bucket sort), all ascending.

## Purpose & Scope

The purpose of this utility is to provide:
- Standard comparison, distribution, and structure-based sorting algorithms
- Reference implementations useful for benchmarking and teaching (for production code prefer `Arrays.sort`)

**Mutation behaviour**
- **In place** (mutates and returns the argument): bubble, cocktailShaker, oddEven, selection, insertion, shell, heap, bogo, bucketSortUniform
- **Copying** (argument untouched, new array returned): quick, merge, intro, tim, counting, pigeonhole, tree, patience, bead
- `null` input returns `null` (or the argument itself for in-place sorts); arrays with 0 or 1 elements are returned as-is or cloned

## API Reference

### Comparison Sorts

#### static int[] bubbleSort(int[] arr)
Bubble Sort with early-exit optimization. Sorts in place.

```java
int[] sorted = Sort.bubbleSort(new int[]{5, 2, 8, 1});
```

#### static int[] cocktailShakerSort(int[] arr)
Bidirectional Cocktail Shaker Sort. Sorts in place.

```java
int[] sorted = Sort.cocktailShakerSort(new int[]{5, 2, 8, 1});
```

#### static int[] oddEvenSort(int[] arr)
Odd-Even (Brick) Sort. Sorts in place.

```java
int[] sorted = Sort.oddEvenSort(new int[]{5, 2, 8, 1});
```

#### static int[] selectionSort(int[] arr)
Selection Sort. Sorts in place.

```java
int[] sorted = Sort.selectionSort(new int[]{5, 2, 8, 1});
```

#### static int[] insertionSort(int[] arr)
Insertion Sort. Sorts in place.

```java
int[] sorted = Sort.insertionSort(new int[]{5, 2, 8, 1});
```

#### static int[] shellsort(int[] arr)
Shell sort with Knuth's gap sequence. Sorts in place.

```java
int[] sorted = Sort.shellsort(new int[]{5, 2, 8, 1});
```

#### static int[] heapsort(int[] arr)
In-place Heap Sort with O(1) extra space.

```java
int[] sorted = Sort.heapsort(new int[]{5, 2, 8, 1});
```

#### static int[] quicksort(int[] arr)
Hoare-partitioned Quick Sort with a middle pivot, insertion sort for small ranges, and O(log n) stack depth. Returns a new array.

```java
int[] sorted = Sort.quicksort(new int[]{5, 2, 8, 1});
```

#### static int[] mergeSort(int[] arr)
Top-down Merge Sort with a single scratch buffer. Returns a new array.

```java
int[] sorted = Sort.mergeSort(new int[]{5, 2, 8, 1});
```

#### static int[] introsort(int[] arr)
Introsort: quicksort that falls back to heap sort past 2*log2(n) depth. Returns a new array.

```java
int[] sorted = Sort.introsort(new int[]{5, 2, 8, 1});
```

#### static int[] timsort(int[] arr)
Sorts boxed values with `Arrays.sort(Object[])` (TimSort). Returns a new array; boxing costs O(n) extra allocations.

```java
int[] sorted = Sort.timsort(new int[]{5, 2, 8, 1});
```

---

### Non-Comparison & Specialized Sorts

#### static int[] countingSort(int[] arr)
Counting Sort for values in `[0, 10,000,000]`. Throws `IllegalArgumentException` for negatives or larger values. Returns a new array.

```java
int[] sorted = Sort.countingSort(new int[]{5, 2, 8, 1});
```

#### static double[] bucketSortUniform(double[] arr)
Bucket Sort for doubles in `[0, 1)` using flat arrays. Rejects `NaN` and out-of-range values with `IllegalArgumentException`. Sorts in place.

```java
double[] sorted = Sort.bucketSortUniform(new double[]{0.42, 0.12, 0.89});
```

#### static int[] pigeonholeSort(int[] arr)
Pigeonhole Sort. Negatives are allowed, but `max - min + 1` must not exceed 10,000,000. Returns a new array.

```java
int[] sorted = Sort.pigeonholeSort(new int[]{5, 2, 8, 1});
```

#### static int[] beadSort(int[] arr)
Gravity-based Bead Sort for non-negative values, O(n * max). Educational only. Returns a new array.

```java
int[] sorted = Sort.beadSort(new int[]{5, 2, 8, 1});
```

#### static int[] treeSort(int[] arr)
Tree Sort using an unbalanced BST with iterative insert and traversal. Degrades to O(n^2) on already-sorted input. Returns a new array.

```java
int[] sorted = Sort.treeSort(new int[]{5, 2, 8, 1});
```

#### static int[] patienceSorting(int[] arr)
Patience Sorting using primitive index chains and a heap for the final k-way merge. Returns a new array.

```java
int[] sorted = Sort.patienceSorting(new int[]{5, 2, 8, 1});
```

#### static int[] bogoSort(int[] arr)
Randomized shuffle until sorted, average O(n * n!). Throws `IllegalArgumentException` for arrays longer than 12. Sorts in place.

```java
int[] sorted = Sort.bogoSort(new int[]{5, 2, 8, 1});
```
