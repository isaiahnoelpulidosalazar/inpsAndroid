package com.isaiahnoelpulidosalazar.inpsAndroid;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Educational collection of sorting algorithms over {@code int[]} (and {@code double[]} for
 * bucket sort), all ascending.
 *
 * <p><b>In place</b> (mutate and return the argument): bubble, cocktailShaker, oddEven, selection,
 * insertion, shell, heap, bogo, bucketSortUniform.<br>
 * <b>Copying</b> (argument untouched, new array returned): quick, merge, intro, tim, counting,
 * pigeonhole, tree, patience, bead.</p>
 *
 * <p>For production code prefer {@link Arrays#sort(int[])}; these exist for teaching and demos.</p>
 */
public final class Sort {

    /** Ranges at or below this size are finished with insertion sort in quick/intro sort. */
    private static final int INSERTION_CUTOFF = 16;
    private static final int MAX_COUNTING_RANGE = 10_000_000;
    /** bogoSort is O(n * n!) on average; larger inputs effectively never finish. */
    private static final int BOGO_MAX_LENGTH = 12;

    private Sort() {
        throw new UnsupportedOperationException("Sort cannot be instantiated");
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // ---------------------------------------------------------------- O(n^2) family

    public static int[] bubbleSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        for (int end = arr.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int j = 0; j < end; j++) {
                if (arr[j] > arr[j + 1]) { swap(arr, j, j + 1); swapped = true; }
            }
            if (!swapped) break;
        }
        return arr;
    }

    public static int[] cocktailShakerSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        int start = 0, end = arr.length - 1;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = start; i < end; i++) {
                if (arr[i] > arr[i + 1]) { swap(arr, i, i + 1); swapped = true; }
            }
            if (!swapped) break;
            swapped = false;
            end--;
            for (int i = end - 1; i >= start; i--) {
                if (arr[i] > arr[i + 1]) { swap(arr, i, i + 1); swapped = true; }
            }
            start++;
        }
        return arr;
    }

    public static int[] oddEvenSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int phase = 1; phase >= 0; phase--) {
                for (int i = phase; i < arr.length - 1; i += 2) {
                    if (arr[i] > arr[i + 1]) { swap(arr, i, i + 1); sorted = false; }
                }
            }
        }
        return arr;
    }

    public static int[] selectionSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) min = j;
            }
            if (min != i) swap(arr, i, min);
        }
        return arr;
    }

    public static int[] insertionSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        insertionRange(arr, 0, arr.length - 1);
        return arr;
    }

    /** Insertion sort of {@code arr[lo..hi]} inclusive. */
    private static void insertionRange(int[] arr, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= lo && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    /** Shell sort with Knuth's gap sequence. */
    public static int[] shellsort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        int n = arr.length;
        int h = 1;
        while (h < n / 3) h = 3 * h + 1;
        for (int gap = h; gap > 0; gap /= 3) {
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j = i;
                while (j >= gap && arr[j - gap] > temp) {
                    arr[j] = arr[j - gap];
                    j -= gap;
                }
                arr[j] = temp;
            }
        }
        return arr;
    }

    // ---------------------------------------------------------------- O(n log n) family

    /** Quicksort (Hoare partition, middle pivot). Recurses on the smaller side so stack depth is O(log n). */
    public static int[] quicksort(int[] arr) {
        if (arr == null) return null;
        int[] copy = arr.clone();
        if (copy.length > 1) quicksortRange(copy, 0, copy.length - 1);
        return copy;
    }

    private static void quicksortRange(int[] arr, int lo, int hi) {
        while (lo < hi) {
            if (hi - lo < INSERTION_CUTOFF) {
                insertionRange(arr, lo, hi);
                return;
            }
            int p = partition(arr, lo, hi);
            if (p - lo < hi - p) {
                quicksortRange(arr, lo, p);
                lo = p + 1;
            } else {
                quicksortRange(arr, p + 1, hi);
                hi = p;
            }
        }
    }

    private static int partition(int[] arr, int lo, int hi) {
        int pivot = arr[lo + (hi - lo) / 2];
        int i = lo - 1, j = hi + 1;
        while (true) {
            do { i++; } while (arr[i] < pivot);
            do { j--; } while (arr[j] > pivot);
            if (i >= j) return j;
            swap(arr, i, j);
        }
    }

    /** Top-down merge sort with a single scratch buffer; already-ordered halves skip the merge. */
    public static int[] mergeSort(int[] arr) {
        if (arr == null) return null;
        int[] copy = arr.clone();
        if (copy.length > 1) mergeSortRange(copy, new int[copy.length], 0, copy.length - 1);
        return copy;
    }

    private static void mergeSortRange(int[] arr, int[] temp, int lo, int hi) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSortRange(arr, temp, lo, mid);
        mergeSortRange(arr, temp, mid + 1, hi);
        if (arr[mid] > arr[mid + 1]) merge(arr, temp, lo, mid, hi);
    }

    private static void merge(int[] arr, int[] temp, int lo, int mid, int hi) {
        System.arraycopy(arr, lo, temp, lo, hi - lo + 1);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            arr[k++] = temp[i] <= temp[j] ? temp[i++] : temp[j++];
        }
        while (i <= mid) arr[k++] = temp[i++];
        // remaining right-half elements are already in place
    }

    /** In-place heap sort, O(1) extra space. */
    public static int[] heapsort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        heapsortRange(arr, 0, arr.length - 1);
        return arr;
    }

    private static void heapsortRange(int[] arr, int lo, int hi) {
        int n = hi - lo + 1;
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(arr, lo, n, i);
        for (int end = n - 1; end > 0; end--) {
            swap(arr, lo, lo + end);
            siftDown(arr, lo, end, 0);
        }
    }

    private static void siftDown(int[] arr, int lo, int n, int root) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= n) return;
            int largest = root;
            if (arr[lo + largest] < arr[lo + child]) largest = child;
            if (child + 1 < n && arr[lo + largest] < arr[lo + child + 1]) largest = child + 1;
            if (largest == root) return;
            swap(arr, lo + root, lo + largest);
            root = largest;
        }
    }

    /** Introsort: quicksort that falls back to heap sort past 2*log2(n) depth. */
    public static int[] introsort(int[] arr) {
        if (arr == null) return null;
        int[] copy = arr.clone();
        if (copy.length > 1) {
            int maxDepth = 2 * (31 - Integer.numberOfLeadingZeros(copy.length)); // 2 * floor(log2 n)
            introsortRange(copy, 0, copy.length - 1, maxDepth);
        }
        return copy;
    }

    private static void introsortRange(int[] arr, int lo, int hi, int depth) {
        while (hi - lo >= INSERTION_CUTOFF) {
            if (depth-- == 0) {
                heapsortRange(arr, lo, hi);
                return;
            }
            int p = partition(arr, lo, hi);
            introsortRange(arr, p + 1, hi, depth);
            hi = p;
        }
        insertionRange(arr, lo, hi);
    }

    /**
     * Sorts via {@link Arrays#sort(Object[])} (TimSort) on boxed values. Boxing costs O(n) extra
     * allocations; stability is unobservable for primitives, so this is mainly educational.
     */
    public static int[] timsort(int[] arr) {
        if (arr == null) return null;
        Integer[] boxed = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) boxed[i] = arr[i];
        Arrays.sort(boxed);
        int[] res = new int[arr.length];
        for (int i = 0; i < res.length; i++) res[i] = boxed[i];
        return res;
    }

    // ---------------------------------------------------------------- non-comparison sorts

    /** Counting sort for values in [0, 10,000,000]. */
    public static int[] countingSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length == 0) return arr.clone();
        int max = 0;
        for (int x : arr) {
            if (x < 0) throw new IllegalArgumentException("Counting sort only supports non-negative integers.");
            if (x > max) max = x;
        }
        if (max > MAX_COUNTING_RANGE) {
            throw new IllegalArgumentException("Maximum value in counting sort is too large (" + max + ").");
        }
        if (arr.length == 1) return arr.clone();

        int[] counts = new int[max + 1];
        for (int x : arr) counts[x]++;
        int[] res = new int[arr.length];
        int idx = 0;
        for (int v = 0; v <= max; v++) {
            for (int c = counts[v]; c > 0; c--) res[idx++] = v;
        }
        return res;
    }

    /** Bucket sort for doubles in [0, 1); uses flat arrays (no per-bucket boxed lists). NaN is rejected. */
    public static double[] bucketSortUniform(double[] arr) {
        if (arr == null) return null;
        for (double x : arr) {
            if (!(x >= 0.0 && x < 1.0)) {
                throw new IllegalArgumentException("Bucket sort elements must be in the range [0.0, 1.0).");
            }
        }
        int n = arr.length;
        if (n <= 1) return arr;

        int[] start = new int[n + 1];
        for (double x : arr) start[(int) (x * n) + 1]++;
        for (int i = 0; i < n; i++) start[i + 1] += start[i];

        double[] tmp = new double[n];
        int[] next = Arrays.copyOf(start, n);
        for (double x : arr) tmp[next[(int) (x * n)]++] = x;

        for (int b = 0; b < n; b++) {
            for (int i = start[b] + 1; i < start[b + 1]; i++) { // insertion sort inside each bucket
                double key = tmp[i];
                int j = i - 1;
                while (j >= start[b] && tmp[j] > key) { tmp[j + 1] = tmp[j]; j--; }
                tmp[j + 1] = key;
            }
        }
        System.arraycopy(tmp, 0, arr, 0, n);
        return arr;
    }

    /** Pigeonhole sort; value range (max - min + 1) must not exceed 10,000,000. Negatives allowed. */
    public static int[] pigeonholeSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length <= 1) return arr.clone();
        int min = arr[0], max = arr[0];
        for (int x : arr) {
            if (x < min) min = x;
            if (x > max) max = x;
        }
        long range = (long) max - min + 1;
        if (range > MAX_COUNTING_RANGE) {
            throw new IllegalArgumentException("Range of elements is too large for pigeonhole sort: " + range);
        }
        int[] holes = new int[(int) range];
        for (int x : arr) holes[x - min]++;
        int[] res = new int[arr.length];
        int idx = 0;
        for (int i = 0; i < holes.length; i++) {
            for (int c = holes[i]; c > 0; c--) res[idx++] = i + min;
        }
        return res;
    }

    /** Bead (gravity) sort for non-negative values. O(n * max): educational only. */
    public static int[] beadSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length == 0) return arr.clone();
        int max = 0;
        for (int x : arr) {
            if (x < 0) throw new IllegalArgumentException("Bead sort for non-negative integers only");
            if (x > max) max = x;
        }
        if (arr.length == 1) return arr.clone();
        if (max == 0) return new int[arr.length];

        int[] rods = new int[max]; // rods[j] = number of values greater than j
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

    // ---------------------------------------------------------------- structure-based sorts

    /**
     * Tree sort with an unbalanced BST (iterative insert/traverse, so no stack overflow).
     * Degrades to O(n^2) time on already-sorted input.
     */
    public static int[] treeSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length <= 1) return arr.clone();
        Node root = null;
        for (int x : arr) root = insert(root, x);

        int[] res = new int[arr.length];
        int idx = 0;
        Deque<Node> stack = new ArrayDeque<>();
        Node cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { stack.push(cur); cur = cur.left; }
            cur = stack.pop();
            res[idx++] = cur.value;
            cur = cur.right;
        }
        return res;
    }

    private static final class Node {
        final int value;
        Node left, right;
        Node(int value) { this.value = value; }
    }

    private static Node insert(Node root, int val) {
        Node node = new Node(val);
        if (root == null) return node;
        Node cur = root;
        while (true) {
            if (val < cur.value) {
                if (cur.left == null) { cur.left = node; return root; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = node; return root; }
                cur = cur.right;
            }
        }
    }

    /**
     * Patience sorting. Piles are stored as index chains in primitive arrays (no boxed lists);
     * only the final k-way merge uses a heap of pile ids.
     */
    public static int[] patienceSorting(int[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        if (n <= 1) return arr.clone();

        int[] top = new int[n];   // top[p]  = index (into arr) of pile p's current top
        int[] prev = new int[n];  // prev[i] = index of the card beneath arr[i], or -1
        int piles = 0;
        for (int i = 0; i < n; i++) {
            int x = arr[i];
            int lo = 0, hi = piles;
            while (lo < hi) { // leftmost pile whose top >= x
                int mid = (lo + hi) >>> 1;
                if (arr[top[mid]] >= x) hi = mid; else lo = mid + 1;
            }
            prev[i] = lo < piles ? top[lo] : -1;
            top[lo] = i;
            if (lo == piles) piles++;
        }

        final int[] cur = Arrays.copyOf(top, piles);
        PriorityQueue<Integer> heap = new PriorityQueue<>(piles, (a, b) -> Integer.compare(arr[cur[a]], arr[cur[b]]));
        for (int p = 0; p < piles; p++) heap.add(p);

        int[] res = new int[n];
        int idx = 0;
        while (!heap.isEmpty()) {
            int p = heap.poll();
            res[idx++] = arr[cur[p]];
            cur[p] = prev[cur[p]];
            if (cur[p] != -1) heap.add(p);
        }
        return res;
    }

    // ---------------------------------------------------------------- joke sort

    /**
     * Bogo sort (random shuffle until sorted). Average O(n * n!).
     *
     * @throws IllegalArgumentException if {@code arr.length > 12}
     */
    public static int[] bogoSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        if (arr.length > BOGO_MAX_LENGTH) {
            throw new IllegalArgumentException("bogoSort is limited to " + BOGO_MAX_LENGTH + " elements");
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        while (!isSorted(arr)) {
            for (int i = arr.length - 1; i > 0; i--) swap(arr, rnd.nextInt(i + 1), i);
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
