package com.isaiahnoelpulidosalazar.inpsandroid.edu.sort;

import java.util.Arrays;

/** Package-private non-comparison sorting implementations. */
final class NonComparisonSorts {

    /** Hard cap on histogram/rod allocations to prevent out-of-memory errors on huge ranges. */
    static final int MAX_RANGE = 10_000_000;

    private NonComparisonSorts() {
        throw new UnsupportedOperationException("No instances");
    }

    /** Offset-adjusted counting sort supporting negative and positive integer values. */
    static int[] countingSort(int[] arr) {
        if (arr.length <= 1) return arr.clone();
        int min = arr[0];
        int max = arr[0];
        for (int x : arr) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        long range = (long) max - min + 1;
        if (range > MAX_RANGE) {
            throw new IllegalArgumentException("Range of elements exceeds maximum (" + MAX_RANGE + "): " + range);
        }
        int[] counts = new int[(int) range];
        for (int x : arr) counts[x - min]++;
        int[] res = new int[arr.length];
        int idx = 0;
        for (int i = 0; i < counts.length; i++) {
            for (int c = counts[i]; c > 0; c--) res[idx++] = i + min;
        }
        return res;
    }

    /** Non-mutating bucket sort for uniform doubles in {@code [0.0, 1.0)}. */
    static double[] bucketSortUniform(double[] arr) {
        for (double x : arr) {
            if (!(x >= 0.0 && x < 1.0)) {
                throw new IllegalArgumentException("Bucket sort elements must be in the range [0.0, 1.0).");
            }
        }
        int n = arr.length;
        if (n <= 1) return arr.clone();

        int[] start = new int[n + 1];
        for (double x : arr) start[(int) (x * n) + 1]++;
        for (int i = 0; i < n; i++) start[i + 1] += start[i];

        double[] res = new double[n];
        int[] next = Arrays.copyOf(start, n);
        for (double x : arr) res[next[(int) (x * n)]++] = x;

        for (int b = 0; b < n; b++) {
            for (int i = start[b] + 1; i < start[b + 1]; i++) {
                double key = res[i];
                int j = i - 1;
                while (j >= start[b] && res[j] > key) {
                    res[j + 1] = res[j];
                    j--;
                }
                res[j + 1] = key;
            }
        }
        return res;
    }

    /** Gravity (abacus) bead sort descending across abacus rods, then read in ascending order. */
    static int[] beadSort(int[] arr) {
        if (arr.length == 0) return arr.clone();
        int max = 0;
        for (int x : arr) {
            if (x < 0) throw new IllegalArgumentException("Bead sort for non-negative integers only");
            if (x > max) max = x;
        }
        if (max > MAX_RANGE) {
            throw new IllegalArgumentException("Maximum value in bead sort exceeds limit (" + MAX_RANGE + "): " + max);
        }
        if (arr.length == 1) return arr.clone();
        if (max == 0) return new int[arr.length];

        int[] rods = new int[max];
        for (int x : arr) {
            for (int j = 0; j < x; j++) rods[j]++;
        }
        int[] res = new int[arr.length];
        for (int row = 0; row < arr.length; row++) {
            int target = arr.length - row;
            int height = 0;
            for (int j = 0; j < max; j++) {
                if (rods[j] >= target) height++;
            }
            res[row] = height;
        }
        return res;
    }
}
