package com.isaiahnoelpulidosalazar.inpsAndroid.crypto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ClassicalCiphersTest {

    @Test
    public void testTransposition() {
        assertEquals("HLOEL", ClassicalCiphers.transposition("Hello"));
        assertEquals("ACEBDF", ClassicalCiphers.transposition("a b c d e f"));
    }

    @Test
    public void testCaesar() {
        assertEquals("KHOOR, ZRUOG!", ClassicalCiphers.caesar("Hello, World!", 3));
        assertEquals("HELLO, WORLD!", ClassicalCiphers.caesar("KHOOR, ZRUOG!", -3));
        assertEquals("KHOOR", ClassicalCiphers.caesar("Hello", 29));
        assertEquals("123 !?", ClassicalCiphers.caesar("123 !?", 5));
    }

    @Test
    public void testKeyword() {
        // Keyword "ZEBRA" -> alphabet Z E B R A C D F G H I J K L M N O P Q S T U V W X Y
        // A->Z, B->E, C->B, D->R, E->A
        assertEquals("ZEBRAC", ClassicalCiphers.keyword("abcdef", "Zebra!"));
    }

    @Test
    public void testGiovanni() {
        String plain = "ATTACK AT DAWN";
        // Key letter 'A' (shift 0) matches keyword cipher
        assertEquals(ClassicalCiphers.keyword(plain, "SECRET"),
                ClassicalCiphers.giovanni(plain, "SECRET", "a"));

        // Non-zero rotation differs from shift 0
        String rotated = ClassicalCiphers.giovanni("ABC", "SECRET", "D");
        assertEquals(3, rotated.length());

        assertThrows(NullPointerException.class, () -> ClassicalCiphers.giovanni("ABC", "KEY", null));
        assertThrows(IllegalArgumentException.class, () -> ClassicalCiphers.giovanni("ABC", "KEY", ""));
        assertThrows(IllegalArgumentException.class, () -> ClassicalCiphers.giovanni("ABC", "KEY", "AB"));
        assertThrows(IllegalArgumentException.class, () -> ClassicalCiphers.giovanni("ABC", "KEY", "1"));
    }
}
