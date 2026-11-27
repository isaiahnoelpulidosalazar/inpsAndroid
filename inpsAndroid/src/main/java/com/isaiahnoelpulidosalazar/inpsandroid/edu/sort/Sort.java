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

    /** Sorts {@code arr} in place. */
    public static int[] bubbleSort(int[] arr) {
        return ComparisonSorts.bubbleSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Sorts {@code arr} in place. */
    public static int[] cocktailShakerSort(int[] arr) {
        return ComparisonSorts.cocktailShakerSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Sorts {@code arr} in place. */
    public static int[] oddEvenSort(int[] arr) {
        return ComparisonSorts.oddEvenSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Sorts {@code arr} in place. */
    public static int[] selectionSort(int[] arr) {
        return ComparisonSorts.selectionSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Sorts {@code arr} in place. */
    public static int[] insertionSort(int[] arr) {
        return ComparisonSorts.insertionSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Shell sort with Knuth's gap sequence. Sorts {@code arr} in place. */
    public static int[] shellsort(int[] arr) {
        return ComparisonSorts.shellsort(Objects.requireNonNull(arr, "arr"));
    }

    /** In-place heap sort, O(1) extra space. */
    public static int[] heapsort(int[] arr) {
        return ComparisonSorts.heapsort(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- O(n log n) copying family

    /** Quicksort (Hoare partition, middle pivot). Returns a new sorted array. */
    public static int[] quicksort(int[] arr) {
        return ComparisonSorts.quicksort(Objects.requireNonNull(arr, "arr"));
    }

    /** Top-down merge sort. Returns a new sorted array. */
    public static int[] mergeSort(int[] arr) {
        return ComparisonSorts.mergeSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Introsort: quicksort that falls back to heap sort past 2*log2(n) depth. Returns a new sorted array. */
    public static int[] introsort(int[] arr) {
        return ComparisonSorts.introsort(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- non-comparison copying family

    /**
     * Histogram-based counting sort supporting negative integers (offset by minimum); value range
     * ({@code max - min + 1}) must not exceed 10,000,000. Returns a new sorted array.
     */
    public static int[] countingSort(int[] arr) {
        return NonComparisonSorts.countingSort(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Bucket sort for doubles in [0.0, 1.0); NaN is rejected. Returns a new sorted array.
     */
    public static double[] bucketSortUniform(double[] arr) {
        return NonComparisonSorts.bucketSortUniform(Objects.requireNonNull(arr, "arr"));
    }

    /**
     * Bead (gravity) sort for non-negative integers up to 10,000,000. Returns a new sorted array.
     */
    public static int[] beadSort(int[] arr) {
        return NonComparisonSorts.beadSort(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- structure-based copying family

    /** Tree sort with an unbalanced BST (iterative insert/traverse). Returns a new sorted array. */
    public static int[] treeSort(int[] arr) {
        return StructureSorts.treeSort(Objects.requireNonNull(arr, "arr"));
    }

    /** Patience sorting. Returns a new sorted array. */
    public static int[] patienceSorting(int[] arr) {
        return StructureSorts.patienceSorting(Objects.requireNonNull(arr, "arr"));
    }

    // ---------------------------------------------------------------- joke sort (in-place)

    /**
     * Bogo sort (random shuffle until sorted), in place. Average O(n * n!).
     *
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
