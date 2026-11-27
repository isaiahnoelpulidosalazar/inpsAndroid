package com.isaiahnoelpulidosalazar.inpsAndroid;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
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

        int[] tim = Sort.timsort(original);
        assertNotSame(original, tim);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, tim);

        int[] tree = Sort.treeSort(original);
        assertNotSame(original, tree);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, tree);

        int[] patience = Sort.patienceSorting(original);
        assertNotSame(original, patience);
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, patience);

        // Original remains untouched
        assertArrayEquals(UNSORTED_WITH_NEGATIVES, original);
    }

    @Test
    public void testNonComparisonSorts() {
        assertArrayEquals(EXPECTED_NON_NEGATIVE, Sort.countingSort(UNSORTED_NON_NEGATIVE));
        assertArrayEquals(EXPECTED_WITH_NEGATIVES, Sort.pigeonholeSort(UNSORTED_WITH_NEGATIVES));
        assertArrayEquals(EXPECTED_NON_NEGATIVE, Sort.beadSort(UNSORTED_NON_NEGATIVE));
        assertArrayEquals(new int[]{0, 0, 0}, Sort.beadSort(new int[]{0, 0, 0}));

        double[] doubles = {0.78, 0.17, 0.39, 0.26, 0.72, 0.94, 0.21, 0.12, 0.23, 0.68};
        double[] expected = doubles.clone();
        Arrays.sort(expected);
        assertArrayEquals(expected, Sort.bucketSortUniform(doubles), 1e-9);
    }

    @Test
    public void testBogoSortSmallInput() {
        int[] small = {3, 1, 2, 4};
        assertArrayEquals(new int[]{1, 2, 3, 4}, Sort.bogoSort(small));
    }

    @Test
    public void testNullAndEdgeCases() {
        assertNull(Sort.bubbleSort(null));
        assertNull(Sort.cocktailShakerSort(null));
        assertNull(Sort.oddEvenSort(null));
        assertNull(Sort.selectionSort(null));
        assertNull(Sort.insertionSort(null));
        assertNull(Sort.shellsort(null));
        assertNull(Sort.quicksort(null));
        assertNull(Sort.mergeSort(null));
        assertNull(Sort.heapsort(null));
        assertNull(Sort.introsort(null));
        assertNull(Sort.timsort(null));
        assertNull(Sort.countingSort(null));
        assertNull(Sort.bucketSortUniform(null));
        assertNull(Sort.pigeonholeSort(null));
        assertNull(Sort.beadSort(null));
        assertNull(Sort.treeSort(null));
        assertNull(Sort.patienceSorting(null));
        assertNull(Sort.bogoSort(null));

        assertArrayEquals(new int[0], Sort.quicksort(new int[0]));
        assertArrayEquals(new int[]{7}, Sort.mergeSort(new int[]{7}));
        assertArrayEquals(new int[0], Sort.countingSort(new int[0]));
        assertArrayEquals(new int[0], Sort.beadSort(new int[0]));
        assertArrayEquals(new int[0], Sort.patienceSorting(new int[0]));
    }

    @Test
    public void testArgumentValidation() {
        assertThrows(IllegalArgumentException.class, () -> Sort.countingSort(new int[]{-1}));
        assertThrows(IllegalArgumentException.class, () -> Sort.countingSort(new int[]{10_000_001}));
        assertThrows(IllegalArgumentException.class, () -> Sort.beadSort(new int[]{-2}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{1.0}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{-0.1}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{Double.NaN}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bogoSort(new int[13]));
    }
}
