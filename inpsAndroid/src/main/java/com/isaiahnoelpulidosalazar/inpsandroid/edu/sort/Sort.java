package com.isaiahnoelpulidosalazar.inpsandroid.edu.sort;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Educational collection of sorting algorithms over {@code int[]} (and {@code double[]} for
 * bucket sort), all ascending. Every method rejects {@code null} with {@link NullPointerException}.
 *
 * <p><b>In place</b> (mutate and return the argument): {@link #bubbleSort},
 * {@link #cocktailShakerSort}, {@link #oddEvenSort}, {@link #selectionSort},
 * {@link #insertionSort}, {@link #shellsort}, {@link #heapsort}, {@link #bogoSort}.<br>
 * <b>Copying</b> (argument untouched, new array returned): {@link #quicksort}, {@link #mergeSort},
 * {@link #introsort}, {@link #countingSort}, {@link #bucketSortUniform}, {@link #beadSort},
 * {@link #treeSort}, {@link #patienceSorting}.</p>
 *
 * <p>For production code prefer {@link Arrays#sort(int[])}; these exist for teaching and demos.</p>
 */
public final class Sort {

    /** bogoSort is O(n * n!) on average; larger inputs effectively never finish. */
    private static final int BOGO_MAX_LENGTH = 12;

    private Sort() {
        throw new UnsupportedOperationException("No instances");
    }

    // ---------------------------------------------------------------- O(n^2) / in-place family

    /**
     * Sorts {@code arr} in place using bubble sort ({@code O(n^2)} worst/average, {@code O(n)}
     * when nearly sorted, {@code O(1)} extra space).
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] bubbleSort(int[] arr) {
        return ComparisonSorts.bubbleSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Sorts {@code arr} in place using bidirectional cocktail shaker sort ({@code O(n^2)} worst,
     * {@code O(1)} extra space).
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] cocktailShakerSort(int[] arr) {
        return ComparisonSorts.cocktailShakerSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Sorts {@code arr} in place using odd-even transposition sort ({@code O(n^2)} worst,
     * {@code O(1)} extra space).
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] oddEvenSort(int[] arr) {
        return ComparisonSorts.oddEvenSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Sorts {@code arr} in place using selection sort ({@code O(n^2)} comparisons, {@code O(1)}
     * extra space).
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] selectionSort(int[] arr) {
        return ComparisonSorts.selectionSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Sorts {@code arr} in place using insertion sort ({@code O(n^2)} worst, {@code O(n)} when
     * nearly sorted, {@code O(1)} extra space).
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] insertionSort(int[] arr) {
        return ComparisonSorts.insertionSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Sorts {@code arr} in place using Shell sort with Knuth's {@code 3h + 1} gap sequence.
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] shellsort(int[] arr) {
        return ComparisonSorts.shellsort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Sorts {@code arr} in place using binary max-heap sort ({@code O(n log n)} worst-case,
     * {@code O(1)} extra space).
     *
     * @param arr array to sort in place
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] heapsort(int[] arr) {
        return ComparisonSorts.heapsort(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- O(n log n) copying family

    /**
     * Returns a newly allocated sorted copy of {@code arr} using quicksort (Hoare partitioning,
     * middle pivot, insertion cutoff for small partitions).
     *
     * @param arr source array (left unmodified)
     * @return new sorted array
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] quicksort(int[] arr) {
        return ComparisonSorts.quicksort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Returns a newly allocated sorted copy of {@code arr} using top-down merge sort
     * ({@code O(n log n)} worst-case, stable, {@code O(n)} auxiliary space).
     *
     * @param arr source array (left unmodified)
     * @return new sorted array
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] mergeSort(int[] arr) {
        return ComparisonSorts.mergeSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Returns a newly allocated sorted copy of {@code arr} using introsort (quicksort falling back
     * to heapsort past {@code 2 * log2(n)} recursion depth, {@code O(n log n)} worst-case).
     *
     * @param arr source array (left unmodified)
     * @return new sorted array
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] introsort(int[] arr) {
        return ComparisonSorts.introsort(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- non-comparison copying family

    /**
     * Histogram-based counting sort supporting negative integers (offset by minimum); value range
     * ({@code max - min + 1}) must not exceed {@code 10,000,000}.
     *
     * @param arr source array (left unmodified)
     * @return new sorted array
     * @throws NullPointerException     if {@code arr} is {@code null}
     * @throws IllegalArgumentException if value span exceeds {@code 10,000,000}
     */
    public static int[] countingSort(int[] arr) {
        return NonComparisonSorts.countingSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Bucket sort for uniformly distributed doubles in {@code [0.0, 1.0)}.
     *
     * @param arr source array of values in {@code [0.0, 1.0)} (left unmodified)
     * @return new sorted array
     * @throws NullPointerException     if {@code arr} is {@code null}
     * @throws IllegalArgumentException if any value is outside {@code [0.0, 1.0)} or {@code NaN}
     */
    public static double[] bucketSortUniform(double[] arr) {
        return NonComparisonSorts.bucketSortUniform(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Bead (gravity) sort for non-negative integers up to {@code 10,000,000}.
     *
     * @param arr source array of non-negative integers (left unmodified)
     * @return new sorted array
     * @throws NullPointerException     if {@code arr} is {@code null}
     * @throws IllegalArgumentException if any element is negative or exceeds {@code 10,000,000}
     */
    public static int[] beadSort(int[] arr) {
        return NonComparisonSorts.beadSort(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- structure-based copying family

    /**
     * Tree sort using an iterative unbalanced binary search tree.
     *
     * @param arr source array (left unmodified)
     * @return new sorted array
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] treeSort(int[] arr) {
        return StructureSorts.treeSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Patience sorting building non-increasing piles merged with a min-heap ({@code O(n log n)}).
     *
     * @param arr source array (left unmodified)
     * @return new sorted array
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] patienceSorting(int[] arr) {
        return StructureSorts.patienceSorting(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- joke sort (in-place)

    /**
     * Bogo sort (random Fisher-Yates shuffle until sorted), in place. Average {@code O(n * n!)}.
     *
     * @param arr array to sort in place (at most {@value #BOGO_MAX_LENGTH} elements)
     * @return the same {@code arr} reference after sorting
     * @throws NullPointerException     if {@code arr} is {@code null}
     * @throws IllegalArgumentException if {@code arr.length > 12}
     */
    public static int[] bogoSort(int[] arr) {
        Objects.requireNonNull(arr, "arr");
        if (arr.length <= 1) return arr;
        if (arr.length > BOGO_MAX_LENGTH) {
            throw new IllegalArgumentException("bogoSort is limited to " + BOGO_MAX_LENGTH + " elements");
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        while (!isSorted(arr)) {
            for (int i = arr.length - 1; i > 0; i--) {
                ComparisonSorts.swap(arr, rnd.nextInt(i + 1), i);
            }
        }
        return arr;
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) return false;
        }
        return true;
    }
}
