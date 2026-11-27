package com.isaiahnoelpulidosalazar.inpsandroid.edu.sort;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

/** Package-private data-structure-based sorting implementations. */
final class StructureSorts {

    private StructureSorts() {
        throw new UnsupportedOperationException("No instances");
    }

    /** Inserts all elements into an unbalanced binary search tree and reads them iteratively in-order. */
    static int[] treeSort(int[] arr) {
        if (arr.length <= 1) return arr.clone();
        Node root = null;
        for (int x : arr) root = insert(root, x);

        int[] res = new int[arr.length];
        int idx = 0;
        Deque<Node> stack = new ArrayDeque<>();
        Node cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
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

    /** Partitions elements into ordered card piles via binary search, then k-way merges with a min-heap. */
    static int[] patienceSorting(int[] arr) {
        int n = arr.length;
        if (n <= 1) return arr.clone();

        int[] top = new int[n];
        int[] prev = new int[n];
        int piles = 0;
        for (int i = 0; i < n; i++) {
            int x = arr[i];
            int lo = 0;
            int hi = piles;
            while (lo < hi) {
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
}
