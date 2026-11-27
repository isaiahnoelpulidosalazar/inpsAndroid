package com.isaiahnoelpulidosalazar.inpsandroid.edu.sort;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;

public class SortTest {

    private static final int[] UNSORTED_NON_NEGATIVE = {42, 7, 19, 0, 7, 3, 25, 1, 99, 12, 8, 5, 17, 64, 2, 11, 33, 6};
    private static final int[] EXPECTED_NON_NEGATIVE = {0, 1, 2, 3, 5, 6, 7, 7, 8, 11, 12, 17, 19, 25, 33, 42, 64, 99};

    private static final int[] UNSORTED_WITH_NEGATIVES = {10, -5, 3, 0, -12, 7, 3, -1, 15, -5, 2, 8, 1, 4, -3, 9, 6, -2};
    private static final int[] EXPECTED_WITH_NEGATIVES = {-12, -5, -5, -3, -2, -1, 0, 1, 2, 3, 3, 4, 6, 7, 8, 9, 10, 15};

    @Test
    public void testInPlaceComparisonSorts() {
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.bubbleSort(UNSORTED_WITH_NEGATIVES.clone()));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.cocktailShakerSort(UNSORTED_WITH_NEGATIVES.clone()));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.oddEvenSort(UNSORTED_WITH_NEGATIVES.clone()));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.selectionSort(UNSORTED_WITH_NEGATIVES.clone()));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.insertionSort(UNSORTED_WITH_NEGATIVES.clone()));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.shellsort(UNSORTED_WITH_NEGATIVES.clone()));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.heapsort(UNSORTED_WITH_NEGATIVES.clone()));
    }

    @Test
    public void testCopyingSorts() {
        int[] original = UNSORTED_WITH_NEGATIVES.clone();

        int[] quick = Sort.quicksort(original);
        assertNotSame(original, quick);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, quick);

        int[] merge = Sort.mergeSort(original);
        assertNotSame(original, merge);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, merge);

        int[] intro = Sort.introsort(original);
        assertNotSame(original, intro);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, intro);

        int[] tree = Sort.treeSort(original);
        assertNotSame(original, tree);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, tree);

        int[] patience = Sort.patienceSorting(original);
        assertNotSame(original, patience);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, patience);

        assertArrayEquals(UNSORTED_WITH_NEGATIVES, original);
    }

    @Test
    public void testNonComparisonSorts() {
        // Merged countingSort supports negatives and does not mutate input
        int[] countingInput = UNSORTED_WITH_NEGATIVES.clone();
        int[] countingSorted = Sort.countingSort(countingInput);
        assertNotSame(countingInput, countingSorted);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, countingSorted);
        assertArrayEquals(UNSORTED_WITH_NEGATIVES, countingInput);

        int[] beadInput = UNSORTED_NON_NEGATIVE.clone();
        int[] beadSorted = Sort.beadSort(beadInput);
        assertNotSame(beadInput, beadSorted);
        assertArrayEquals(EXPECTED_NON_NEGATIVE, beadSorted);
        assertArrayEquals(new int[]{0, 0, 0}, Sort.beadSort(new int[]{0, 0, 0}));

        double[] doubles = {0.78, 0.17, 0.39, 0.26, 0.72, 0.94, 0.21, 0.12, 0.23, 0.68};
        double[] originalDoubles = doubles.clone();
        double[] expected = doubles.clone();
        Arrays.sort(expected);
        double[] bucketSorted = Sort.bucketSortUniform(doubles);
        assertNotSame(doubles, bucketSorted);
        assertArrayEquals(expected, bucketSorted, 1e-9);
        assertArrayEquals(originalDoubles, doubles, 1e-9);
    }

    @Test
    public void testBogoSortSmallInput() {
        int[] small = {3, 1, 2, 4};
        assertArrayEquals(new int[]{1, 2, 3, 4}, Sort.bogoSort(small));
    }

    @Test
    public void testNullThrowsNullPointerAndEmptyCases() {
        assertThrows(NullPointerException.class, () -> Sort.bubbleSort(null));
        assertThrows(NullPointerException.class, () -> Sort.cocktailShakerSort(null));
        assertThrows(NullPointerException.class, () -> Sort.oddEvenSort(null));
        assertThrows(NullPointerException.class, () -> Sort.selectionSort(null));
        assertThrows(NullPointerException.class, () -> Sort.insertionSort(null));
        assertThrows(NullPointerException.class, () -> Sort.shellsort(null));
        assertThrows(NullPointerException.class, () -> Sort.quicksort(null));
        assertThrows(NullPointerException.class, () -> Sort.mergeSort(null));
        assertThrows(NullPointerException.class, () -> Sort.heapsort(null));
        assertThrows(NullPointerException.class, () -> Sort.introsort(null));
        assertThrows(NullPointerException.class, () -> Sort.countingSort(null));
        assertThrows(NullPointerException.class, () -> Sort.bucketSortUniform(null));
        assertThrows(NullPointerException.class, () -> Sort.beadSort(null));
        assertThrows(NullPointerException.class, () -> Sort.treeSort(null));
        assertThrows(NullPointerException.class, () -> Sort.patienceSorting(null));
        assertThrows(NullPointerException.class, () -> Sort.bogoSort(null));

        assertArrayEquals(new int[0], Sort.quicksort(new int[0]));
        assertArrayEquals(new int[]{7}, Sort.mergeSort(new int[]{7}));
        assertArrayEquals(new int[0], Sort.countingSort(new int[0]));
        assertArrayEquals(new int[0], Sort.beadSort(new int[0]));
        assertArrayEquals(new int[0], Sort.patienceSorting(new int[0]));
    }

    @Test
    public void testArgumentValidation() {
        assertThrows(IllegalArgumentException.class, () -> Sort.countingSort(new int[]{0, 10_000_001}));
        assertThrows(IllegalArgumentException.class, () -> Sort.countingSort(new int[]{-5_000_001, 5_000_001}));
        assertThrows(IllegalArgumentException.class, () -> Sort.beadSort(new int[]{-2}));
        assertThrows(IllegalArgumentException.class, () -> Sort.beadSort(new int[]{10_000_001}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{1.0}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{-0.1}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{Double.NaN}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bogoSort(new int[13]));
    }
}
