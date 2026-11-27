package com.isaiahnoelpulidosalazar.inpsandroid.edu.sort;

/** Package-private comparison-based sorting implementations. */
final class ComparisonSorts {

    private static final int INSERTION_CUTOFF = 16;

    private ComparisonSorts() {
        throw new UnsupportedOperationException("No instances");
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    static int[] bubbleSort(int[] arr) {
        for (int end = arr.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int j = 0; j < end; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return arr;
    }

    static int[] cocktailShakerSort(int[] arr) {
        int start = 0;
        int end = arr.length - 1;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = start; i < end; i++) {
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
            swapped = false;
            end--;
            for (int i = end - 1; i >= start; i--) {
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                    swapped = true;
                }
            }
            start++;
        }
        return arr;
    }

    static int[] oddEvenSort(int[] arr) {
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int phase = 1; phase >= 0; phase--) {
                for (int i = phase; i < arr.length - 1; i += 2) {
                    if (arr[i] > arr[i + 1]) {
                        swap(arr, i, i + 1);
                        sorted = false;
                    }
                }
            }
        }
        return arr;
    }

    static int[] selectionSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) min = j;
            }
            if (min != i) swap(arr, i, min);
        }
        return arr;
    }

    static int[] insertionSort(int[] arr) {
        if (arr.length > 1) insertionRange(arr, 0, arr.length - 1);
        return arr;
    }

    static void insertionRange(int[] arr, int lo, int hi) {
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

    static int[] shellsort(int[] arr) {
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

    static int[] quicksort(int[] arr) {
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
        int i = lo - 1;
        int j = hi + 1;
        while (true) {
            do { i++; } while (arr[i] < pivot);
            do { j--; } while (arr[j] > pivot);
            if (i >= j) return j;
            swap(arr, i, j);
        }
    }

    static int[] mergeSort(int[] arr) {
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
        int i = lo;
        int j = mid + 1;
        int k = lo;
        while (i <= mid && j <= hi) {
            arr[k++] = temp[i] <= temp[j] ? temp[i++] : temp[j++];
        }
        while (i <= mid) arr[k++] = temp[i++];
    }

    static int[] heapsort(int[] arr) {
        if (arr.length > 1) heapsortRange(arr, 0, arr.length - 1);
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

    static int[] introsort(int[] arr) {
        int[] copy = arr.clone();
        if (copy.length > 1) {
            int maxDepth = 2 * (31 - Integer.numberOfLeadingZeros(copy.length));
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
}
