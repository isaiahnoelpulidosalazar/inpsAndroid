package com.isaiahnoelpulidosalazar.inpsAndroid;

import static org.junit.Assert.*;

import com.isaiahnoelpulidosalazar.inpsAndroid.AreaCalculator.Point;
import com.isaiahnoelpulidosalazar.inpsAndroid.convert.Converters;
import com.isaiahnoelpulidosalazar.inpsAndroid.crypto.ClassicalCiphers;
import com.isaiahnoelpulidosalazar.inpsAndroid.time.TimeLeft;
import com.isaiahnoelpulidosalazar.inpsAndroid.validation.EmailValidator;
import com.isaiahnoelpulidosalazar.inpsAndroid.validation.StringChecks;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

public class V2UnitTest {

    // ---- validation ------------------------------------------------------------------------

    @Test
    public void emailSplitModeRejectsSpoofedSubdomains() {
        EmailValidator v = new EmailValidator.Builder().addDomainName("gmail").addDomainExtension("com").build();
        assertTrue(v.isValid("user@gmail.com"));
        assertTrue(v.isValid("USER@GMAIL.COM"));
        assertFalse(v.isValid("user@gmail.com.evil.example")); // accepted by v1
        assertFalse(v.isValid("user@gmail."));
        assertFalse(v.isValid("user@.com"));
        assertFalse(v.isValid(null));
    }

    @Test
    public void emailFullDomainMode() {
        EmailValidator v = new EmailValidator.Builder().useFullDomain(true).addDomain("Yahoo.com").build();
        assertTrue(v.isValid("a@yahoo.com"));
        assertFalse(v.isValid("a@gmail.com"));
    }

    @Test
    public void stringChecks() {
        assertTrue(StringChecks.hasSymbols("a!b"));
        assertFalse(StringChecks.hasSymbols("abc"));
        assertTrue(StringChecks.hasNumbers("a1"));
        assertTrue(StringChecks.hasSpaces("a b"));
        assertFalse(StringChecks.hasSpaces(null));
    }

    // ---- time / convert ---------------------------------------------------------------------

    @Test
    public void timeLeft() {
        assertEquals(1000.0, TimeLeft.seconds(1_000_000L, 2_000_000L), 1e-9);
        assertEquals(0.0, TimeLeft.hours((Date) null, new Date()), 0.0);
    }

    @Test
    public void converters() {
        assertEquals("John", Converters.capitalize("john"));
        assertEquals("", Converters.capitalize(""));
        assertNull(Converters.capitalize(null));
        assertEquals(7, Converters.toInt(" 7 ", -1));
        assertEquals(-1, Converters.toInt(null, -1));
        assertEquals(-1.0, Converters.toDouble("x", -1.0), 0.0);
        assertEquals("5/4/2026", Converters.toMonthDayYear(new Date(126, 4, 4)));
        assertNull(Converters.toYearMonthDay(null));
    }

    // ---- ciphers -----------------------------------------------------------------------------

    @Test
    public void ciphers() {
        assertEquals("HLOEL", ClassicalCiphers.transposition("HELLO"));
        assertEquals("XYZ", ClassicalCiphers.caesar("abc", -3));
        assertEquals("ABC", ClassicalCiphers.caesar("ABC", 26));
        assertEquals("DEF", ClassicalCiphers.caesar("ABC", 29));
        assertEquals("FBJJN", ClassicalCiphers.keyword("HELLO", "KEY"));
        assertEquals("CYHHL", ClassicalCiphers.giovanni("HELLO", "KEY", "C"));
        assertEquals("FBJJN", ClassicalCiphers.giovanni("HELLO", "KEY", "A"));
        assertEquals("A-1 B", ClassicalCiphers.caesar("a-1 b", 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void giovanniRejectsBadKeyLetter() {
        ClassicalCiphers.giovanni("HELLO", "KEY", "7");
    }

    // ---- memory ------------------------------------------------------------------------------

    @Test
    public void memoryRemoveMissingKeepsData() {
        Memory<String> m = new Memory<>();
        m.add("A");
        m.add("B");
        assertFalse(m.remove("Z"));          // v1 silently dropped the last element
        assertEquals(2, m.count());
        m.add(null);
        assertTrue(m.contains(null));        // v1 threw NPE
        assertTrue(new Memory<String>().isEmpty());
    }

    // ---- area ----------------------------------------------------------------------------------

    @Test
    public void area() {
        assertEquals(9.0, AreaCalculator.squareArea(3), 1e-12);
        assertEquals(Math.PI, AreaCalculator.circleArea(1), 1e-12);
        assertEquals(Math.PI * (9 - 4), AreaCalculator.annulusArea(3, 2), 1e-12);
        assertEquals(Math.PI / 4, AreaCalculator.sectorArea(1, 90), 1e-12);
        assertEquals(Math.PI / 4 - 0.5, AreaCalculator.segmentArea(1, 90), 1e-12);
        assertEquals(1.0, AreaCalculator.polygonShoelaceArea(Arrays.asList(
                new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1))), 1e-12);
        assertEquals(0.0, AreaCalculator.polygonShoelaceArea(null), 0.0);
        assertEquals(6.0, AreaCalculator.cubeSurfaceArea(1), 1e-12);
        assertEquals(4 * Math.PI, AreaCalculator.sphereSurfaceArea(1), 1e-12);
    }

    // ---- sorting -------------------------------------------------------------------------------

    @Test
    public void sortAlgorithms() {
        int[] unsorted = {5, 1, 4, 2, 8, -2, 0, 4};
        int[] expected = {-2, 0, 1, 2, 4, 4, 5, 8};
        assertArrayEquals(expected, Sort.bubbleSort(unsorted.clone()));
        assertArrayEquals(expected, Sort.cocktailShakerSort(unsorted.clone()));
        assertArrayEquals(expected, Sort.oddEvenSort(unsorted.clone()));
        assertArrayEquals(expected, Sort.selectionSort(unsorted.clone()));
        assertArrayEquals(expected, Sort.insertionSort(unsorted.clone()));
        assertArrayEquals(expected, Sort.shellsort(unsorted.clone()));
        assertArrayEquals(expected, Sort.quicksort(unsorted));
        assertArrayEquals(expected, Sort.mergeSort(unsorted));
        assertArrayEquals(expected, Sort.heapsort(unsorted.clone()));
        assertArrayEquals(expected, Sort.introsort(unsorted));
        assertArrayEquals(expected, Sort.timsort(unsorted));
        assertArrayEquals(expected, Sort.pigeonholeSort(unsorted));
        assertArrayEquals(expected, Sort.treeSort(unsorted));
        assertArrayEquals(expected, Sort.patienceSorting(unsorted));
        assertArrayEquals(new int[]{5, 1, 4, 2, 8, -2, 0, 4}, unsorted); // copying sorts leave input alone

        int[] positives = {5, 1, 4, 2, 8, 0, 4};
        int[] expectedPositives = {0, 1, 2, 4, 4, 5, 8};
        assertArrayEquals(expectedPositives, Sort.countingSort(positives));
        assertArrayEquals(expectedPositives, Sort.beadSort(positives));

        double[] doubles = {0.5, 0.1, 0.4, 0.2, 0.8, 0.0, 0.4};
        assertArrayEquals(new double[]{0.0, 0.1, 0.2, 0.4, 0.4, 0.5, 0.8}, Sort.bucketSortUniform(doubles), 1e-12);
    }

    @Test
    public void sortsAgreeWithArraysSortOnLargeRandomInput() {
        java.util.Random rnd = new java.util.Random(42);
        for (int len : new int[]{0, 1, 2, 17, 18, 100, 5000}) {
            int[] data = new int[len];
            for (int i = 0; i < len; i++) data[i] = rnd.nextInt(2000) - 1000;
            int[] want = data.clone();
            Arrays.sort(want);
            assertArrayEquals(want, Sort.quicksort(data));
            assertArrayEquals(want, Sort.introsort(data));
            assertArrayEquals(want, Sort.mergeSort(data));
            assertArrayEquals(want, Sort.heapsort(data.clone()));
            assertArrayEquals(want, Sort.shellsort(data.clone()));
            assertArrayEquals(want, Sort.patienceSorting(data));
            assertArrayEquals(want, Sort.pigeonholeSort(data));
            assertArrayEquals(want, Sort.treeSort(data));
        }
    }

    @Test
    public void sortedAndReversedAndConstantInputs() {
        int n = 3000;
        int[] asc = new int[n], desc = new int[n], same = new int[n];
        for (int i = 0; i < n; i++) { asc[i] = i; desc[i] = n - 1 - i; same[i] = 7; }
        assertArrayEquals(asc, Sort.quicksort(asc));
        assertArrayEquals(asc, Sort.introsort(desc));
        assertArrayEquals(asc, Sort.quicksort(desc));
        assertArrayEquals(same, Sort.quicksort(same));
        assertArrayEquals(same, Sort.patienceSorting(same));
    }

    @Test
    public void sortEdgeCasesAndExceptions() {
        assertNull(Sort.quicksort(null));
        assertArrayEquals(new int[]{}, Sort.quicksort(new int[]{}));
        assertArrayEquals(new int[]{42}, Sort.patienceSorting(new int[]{42}));
        assertArrayEquals(new int[]{2, 2}, Sort.patienceSorting(new int[]{2, 2}));
        assertThrows(IllegalArgumentException.class, () -> Sort.countingSort(new int[]{-1, 2}));
        assertThrows(IllegalArgumentException.class, () -> Sort.countingSort(new int[]{10_000_001}));
        assertThrows(IllegalArgumentException.class, () -> Sort.beadSort(new int[]{-5, 2}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{-0.1, 0.5}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{1.0, 0.5}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bucketSortUniform(new double[]{Double.NaN}));
        assertThrows(IllegalArgumentException.class, () -> Sort.pigeonholeSort(new int[]{1, 10_000_002}));
        assertThrows(IllegalArgumentException.class, () -> Sort.bogoSort(new int[13]));
    }

    @Test
    public void bogoSortSmall() {
        assertArrayEquals(new int[]{1, 2, 3}, Sort.bogoSort(new int[]{2, 1, 3}));
    }
}
