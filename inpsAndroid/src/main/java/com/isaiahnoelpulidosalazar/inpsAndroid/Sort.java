package com.isaiahnoelpulidosalazar.inpsAndroid;

import java.util.*;

/**
 * A utility class providing a comprehensive collection of sorting algorithm implementations,
 * all operating on {@code int[]} arrays (or {@code double[]} for bucket sort).
 *
 * <p>All methods sort in <b>ascending</b> order. Most methods sort the input array in-place
 * and return it; a few (e.g., {@link #quicksort}, {@link #mergeSort}, {@link #introsort},
 * {@link #timsort}) return a new array.</p>
 */
public class Sort {

    /**
     * Sorts an array using bubble sort.
     * Includes an early-exit optimization if the array becomes sorted mid-process.
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] bubbleSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return arr;
    }

    /**
     * Sorts an array using cocktail shaker sort (bidirectional bubble sort).
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] cocktailShakerSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        boolean swapped = true;
        int start = 0;
        int end = arr.length - 1;
        while (swapped) {
            swapped = false;
            for (int i = start; i < end; i++) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;

            swapped = false;
            end--;
            for (int i = end - 1; i >= start; i--) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    swapped = true;
                }
            }
            start++;
        }
        return arr;
    }

    /**
     * Sorts an array using odd-even sort.
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] oddEvenSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        boolean isSorted = false;
        while (!isSorted) {
            isSorted = true;
            for (int i = 1; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    isSorted = false;
                }
            }
            for (int i = 0; i < arr.length - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    isSorted = false;
                }
            }
        }
        return arr;
    }

    /**
     * Sorts an array using selection sort.
     * Prevents self-swapping operations.
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] selectionSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
        }
        return arr;
    }

    /**
     * Sorts an array using insertion sort.
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] insertionSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
        return arr;
    }

    /**
     * Sorts an array using Shell sort with Knuth's gap sequence.
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] shellsort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        int n = arr.length;
        int h = 1;
        while (h < n / 3) {
            h = 3 * h + 1; // Knuth's gap sequence
        }
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

    /**
     * Sorts an array using quicksort.
     * Optimized to run in-place on a copy of the original array.
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] quicksort(int[] arr) {
        if (arr == null) return null;
        int[] copy = arr.clone();
        if (copy.length <= 1) return copy;
        quicksortHelper(copy, 0, copy.length - 1);
        return copy;
    }

    private static void quicksortHelper(int[] arr, int lo, int hi) {
        if (lo < hi) {
            int p = partition(arr, lo, hi);
            quicksortHelper(arr, lo, p);
            quicksortHelper(arr, p + 1, hi);
        }
    }

    private static int partition(int[] arr, int lo, int hi) {
        int pivot = arr[lo + (hi - lo) / 2];
        int i = lo - 1;
        int j = hi + 1;
        while (true) {
            do { i++; } while (arr[i] < pivot);
            do { j--; } while (arr[j] > pivot);
            if (i >= j) return j;
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    /**
     * Sorts an array using merge sort.
     * Allocates a temporary helper array only once.
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] mergeSort(int[] arr) {
        if (arr == null) return null;
        int[] copy = arr.clone();
        if (copy.length <= 1) return copy;
        int[] temp = new int[copy.length];
        mergeSortHelper(copy, temp, 0, copy.length - 1);
        return copy;
    }

    private static void mergeSortHelper(int[] arr, int[] temp, int lo, int hi) {
        if (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            mergeSortHelper(arr, temp, lo, mid);
            mergeSortHelper(arr, temp, mid + 1, hi);
            merge(arr, temp, lo, mid, hi);
        }
    }

    private static void merge(int[] arr, int[] temp, int lo, int mid, int hi) {
        System.arraycopy(arr, lo, temp, lo, hi - lo + 1);
        int i = lo;
        int j = mid + 1;
        int k = lo;
        while (i <= mid && j <= hi) {
            if (temp[i] <= temp[j]) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        while (i <= mid) {
            arr[k++] = temp[i++];
        }
    }

    /**
     * Sorts an array using heap sort.
     * Performs a true in-place sort using O(1) auxiliary space.
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] heapsort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(arr, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            siftDown(arr, i, 0);
        }
        return arr;
    }

    private static void siftDown(int[] arr, int n, int i) {
        int root = i;
        while (root * 2 + 1 < n) {
            int child = root * 2 + 1;
            int swap = root;
            if (arr[swap] < arr[child]) {
                swap = child;
            }
            if (child + 1 < n && arr[swap] < arr[child + 1]) {
                swap = child + 1;
            }
            if (swap == root) {
                return;
            } else {
                int temp = arr[root];
                arr[root] = arr[swap];
                arr[swap] = temp;
                root = swap;
            }
        }
    }

    /**
     * Sorts an array using introsort.
     * Prevents stack overflow by switching to Heap Sort when recursion exceeds depth limit.
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] introsort(int[] arr) {
        if (arr == null) return null;
        int[] copy = arr.clone();
        if (copy.length <= 1) return copy;
        // 31 - Integer.numberOfLeadingZeros(length) represents floor(log2(length))
        int maxDepth = 2 * (31 - Integer.numberOfLeadingZeros(copy.length));
        introsortHelper(copy, 0, copy.length - 1, maxDepth);
        return copy;
    }

    private static void introsortHelper(int[] arr, int lo, int hi, int depthLimit) {
        if (lo < hi) {
            if (depthLimit == 0) {
                heapsortHelper(arr, lo, hi);
                return;
            }
            int p = partition(arr, lo, hi);
            introsortHelper(arr, lo, p, depthLimit - 1);
            introsortHelper(arr, p + 1, hi, depthLimit - 1);
        }
    }

    private static void heapsortHelper(int[] arr, int lo, int hi) {
        int n = hi - lo + 1;
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDownSubarray(arr, n, i, lo);
        }
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[lo];
            arr[lo] = arr[lo + i];
            arr[lo + i] = temp;
            siftDownSubarray(arr, i, 0, lo);
        }
    }

    private static void siftDownSubarray(int[] arr, int n, int i, int lo) {
        int root = i;
        while (root * 2 + 1 < n) {
            int child = root * 2 + 1;
            int swap = root;
            if (arr[lo + swap] < arr[lo + child]) {
                swap = child;
            }
            if (child + 1 < n && arr[lo + swap] < arr[lo + child + 1]) {
                swap = child + 1;
            }
            if (swap == root) {
                return;
            } else {
                int temp = arr[lo + root];
                arr[lo + root] = arr[lo + swap];
                arr[lo + swap] = temp;
                root = swap;
            }
        }
    }

    /**
     * Sorts an array using timsort.
     * Uses box/unbox mapping to guarantee stable Timsort for primitive types.
     *
     * @param arr the array to sort
     * @return a new sorted copy of the input
     */
    public static int[] timsort(int[] arr) {
        if (arr == null) return null;
        Integer[] boxed = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            boxed[i] = arr[i];
        }
        Arrays.sort(boxed); // Uses Timsort
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = boxed[i];
        }
        return res;
    }

    /**
     * Sorts an array of non-negative integers using counting sort.
     * Max-range validation included to prevent memory errors.
     *
     * @param arr the array of non-negative integers to sort (not modified)
     * @return a new sorted array, or a copy if it is empty
     */
    public static int[] countingSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length == 0) return arr.clone();

        // Perform validations first, even for single-element arrays
        int maxVal = arr[0];
        for (int x : arr) {
            if (x < 0) {
                throw new IllegalArgumentException("Counting sort only supports non-negative integers.");
            }
            if (x > maxVal) {
                maxVal = x;
            }
        }

        if (maxVal > 10_000_000) {
            throw new IllegalArgumentException("Maximum value in counting sort is too large (" + maxVal + ").");
        }

        // Return early after validating single-element input
        if (arr.length == 1) return arr.clone();

        int[] counts = new int[maxVal + 1];
        for (int x : arr) {
            counts[x]++;
        }

        int[] res = new int[arr.length];
        int idx = 0;
        for (int i = 0; i <= maxVal; i++) {
            int count = counts[i];
            for (int j = 0; j < count; j++) {
                res[idx++] = i;
            }
        }
        return res;
    }

    /**
     * Sorts an array of {@code double} values in [0, 1) using bucket sort.
     * Includes validations to prevent out-of-bounds access.
     *
     * @param arr the array of doubles in [0.0, 1.0) to sort (modified in-place)
     * @return the sorted array
     */
    public static double[] bucketSortUniform(double[] arr) {
        if (arr == null) return null;
        if (arr.length == 0) return arr;

        // Perform validations first, even for single-element arrays
        for (double x : arr) {
            if (x < 0.0 || x >= 1.0) {
                throw new IllegalArgumentException("Bucket sort elements must be in the range [0.0, 1.0).");
            }
        }

        // Return early after validating single-element input
        if (arr.length == 1) return arr;

        int n = arr.length;
        List<List<Double>> buckets = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            buckets.add(new ArrayList<>());
        }
        for (double x : arr) {
            int index = (int) (x * n);
            if (index >= n) index = n - 1;
            buckets.get(index).add(x);
        }
        int idx = 0;
        for (List<Double> b : buckets) {
            Collections.sort(b);
            for (double x : b) {
                arr[idx++] = x;
            }
        }
        return arr;
    }

    /**
     * Sorts an array of integers using pigeonhole sort.
     *
     * @param arr the array to sort; may contain negative values (not modified)
     * @return a new sorted array, or a copy if empty
     */
    public static int[] pigeonholeSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr == null ? null : arr.clone();
        int minVal = arr[0];
        int maxVal = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < minVal) minVal = arr[i];
            else if (arr[i] > maxVal) maxVal = arr[i];
        }

        long range = (long) maxVal - minVal + 1;
        if (range > 10_000_000) {
            throw new IllegalArgumentException("Range of elements is too large for pigeonhole sort: " + range);
        }

        int size = (int) range;
        int[] holes = new int[size];

        for (int x : arr) {
            holes[x - minVal]++;
        }

        int[] res = new int[arr.length];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            int count = holes[i];
            int val = i + minVal;
            for (int j = 0; j < count; j++) {
                res[idx++] = val;
            }
        }
        return res;
    }

    /**
     * Internal binary search tree node used by {@link #treeSort(int[])}.
     */
    static class Node {
        int v;
        Node l, r;
        Node(int v) { this.v = v; }
    }

    /**
     * Sorts an array using tree sort.
     * Uses iterative BST insertion and iterative traversal to prevent stack overflows.
     *
     * @param arr the array to sort
     * @return a new sorted array, or a copy if empty
     */
    public static int[] treeSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length <= 1) return arr.clone();
        Node root = null;
        for (int x : arr) {
            root = insertIterative(root, x);
        }
        return traverseIterative(root, arr.length);
    }

    private static Node insertIterative(Node root, int val) {
        Node newNode = new Node(val);
        if (root == null) return newNode;
        Node curr = root;
        while (true) {
            if (val < curr.v) {
                if (curr.l == null) {
                    curr.l = newNode;
                    break;
                }
                curr = curr.l;
            } else {
                if (curr.r == null) {
                    curr.r = newNode;
                    break;
                }
                curr = curr.r;
            }
        }
        return root;
    }

    private static int[] traverseIterative(Node root, int length) {
        int[] res = new int[length];
        int idx = 0;
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = root;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.l;
            }
            curr = stack.pop();
            res[idx++] = curr.v;
            curr = curr.r;
        }
        return res;
    }

    static class PileState implements Comparable<PileState> {
        final int value;
        final int pileIdx;
        final int itemIdx;

        PileState(int value, int pileIdx, int itemIdx) {
            this.value = value;
            this.pileIdx = pileIdx;
            this.itemIdx = itemIdx;
        }

        @Override
        public int compareTo(PileState o) {
            return Integer.compare(this.value, o.value);
        }
    }

    /**
     * Sorts an array using patience sorting.
     * Uses a specific {@link PileState} model to optimize memory usage in heap operations.
     *
     * @param arr the array to sort
     * @return a new sorted array
     */
    public static int[] patienceSorting(int[] arr) {
        if (arr == null) return null;
        if (arr.length <= 1) return arr.clone();

        List<List<Integer>> piles = new ArrayList<>();
        for (int x : arr) {
            int left = 0, right = piles.size();
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (piles.get(mid).get(piles.get(mid).size() - 1) >= x) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            if (left < piles.size()) {
                piles.get(left).add(x);
            } else {
                List<Integer> newPile = new ArrayList<>();
                newPile.add(x);
                piles.add(newPile);
            }
        }

        PriorityQueue<PileState> pq = new PriorityQueue<>();
        for (int i = 0; i < piles.size(); i++) {
            List<Integer> pile = piles.get(i);
            pq.add(new PileState(pile.get(pile.size() - 1), i, pile.size() - 1));
        }

        int[] res = new int[arr.length];
        int idx = 0;
        while (!pq.isEmpty()) {
            PileState curr = pq.poll();
            res[idx++] = curr.value;
            int pileIdx = curr.pileIdx;
            int itemIdx = curr.itemIdx - 1;
            if (itemIdx >= 0) {
                pq.add(new PileState(piles.get(pileIdx).get(itemIdx), pileIdx, itemIdx));
            }
        }
        return res;
    }

    /**
     * Sorts an array using bogo sort (random shuffle sort).
     *
     * @param arr the array to sort (modified in-place)
     * @return the sorted array
     */
    public static int[] bogoSort(int[] arr) {
        if (arr == null || arr.length <= 1) return arr;
        Random rand = new Random();
        int n = arr.length;
        while (!isSorted(arr)) {
            for (int i = n - 1; i > 0; i--) {
                int index = rand.nextInt(i + 1);
                int a = arr[index];
                arr[index] = arr[i];
                arr[i] = a;
            }
        }
        return arr;
    }

    private static boolean isSorted(int[] arr) {
        if (arr == null) return true;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) return false;
        }
        return true;
    }

    /**
     * Sorts an array of non-negative integers using bead sort.
     * Uses optimized 1D bead accumulation tracking to avoid O(N * maxVal) matrix allocation.
     *
     * @param arr the array of non-negative integers to sort
     * @return a new sorted array
     * @throws IllegalArgumentException if any element is negative
     */
    public static int[] beadSort(int[] arr) {
        if (arr == null) return null;
        if (arr.length == 0) return arr.clone();

        // Perform validations first, even for single-element arrays
        int maxVal = arr[0];
        for (int x : arr) {
            if (x < 0) {
                throw new IllegalArgumentException("Bead sort for non-negative integers only");
            }
            if (x > maxVal) {
                maxVal = x;
            }
        }

        // Return early after validating single-element input
        if (arr.length == 1) return arr.clone();
        if (maxVal == 0) return new int[arr.length];

        // Track bead counts on each vertical rod
        int[] beadCount = new int[maxVal];
        for (int x : arr) {
            for (int j = 0; j < x; j++) {
                beadCount[j]++;
            }
        }

        // Reconstruct the array based on rod accumulation heights
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int target = arr.length - i;
            int rowSum = 0;
            for (int j = 0; j < maxVal; j++) {
                if (beadCount[j] >= target) {
                    rowSum++;
                }
            }
            res[i] = rowSum;
        }
        return res;
    }
}