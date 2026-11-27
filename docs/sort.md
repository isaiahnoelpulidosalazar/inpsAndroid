# Sort

Package: `com.isaiahnoelpulidosalazar.inpsandroid.edu.sort`

`Sort` is an educational collection of ascending sorting algorithms over `int[]` (and `double[]` for `bucketSortUniform`). Every method validates non-null input with `Objects.requireNonNull` and throws `NullPointerException` on `null`.

> For production code, prefer `Arrays.sort`.

## Algorithm Families

| Family | Behavior | Methods |
|---|---|---|
| Comparison (in-place) | Mutates and returns input array | `bubbleSort`, `cocktailShakerSort`, `oddEvenSort`, `selectionSort`, `insertionSort`, `shellsort`, `heapsort`, `bogoSort` (max 12 elements) |
| Comparison (copying) | Leaves input untouched, returns new sorted array | `quicksort`, `mergeSort`, `introsort` |
| Non-comparison (copying) | Leaves input untouched, returns new sorted array | `countingSort` (supports negative integers, `10,000,000` range cap), `bucketSortUniform(double[])` (`[0.0, 1.0)`, rejects `NaN`), `beadSort` (non-negative integers up to `10,000,000`) |
| Structure-based (copying) | Leaves input untouched, returns new sorted array | `treeSort`, `patienceSorting` |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.edu.sort.Sort;

int[] data = {42, -7, 19, 3, 25};
int[] sortedCopy = Sort.countingSort(data); // [-7, 3, 19, 25, 42]
Sort.heapsort(data);                        // sorts in place
```
