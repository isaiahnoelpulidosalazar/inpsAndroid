package com.isaiahnoelpulidosalazar.inpsandroid.edu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class SoundSynthesizerTest {

    @Test
    public void testFrequencyAndValidationAndPrepare() {
        assertEquals(440.0, SoundSynthesizer.frequencyOf(69), 1e-6);
        assertEquals(261.625565, SoundSynthesizer.frequencyOf(60), 1e-3);

        try (SoundSynthesizer synth = new SoundSynthesizer()) {
            assertThrows(IllegalArgumentException.class, () -> synth.playNote(-1));
            assertThrows(IllegalArgumentException.class, () -> synth.playNote(128));
            assertThrows(IllegalArgumentException.class, () -> synth.prepare(60, 200));
            assertThrows(NullPointerException.class, () -> synth.prepare((int[]) null));

            synth.prepare(60, 64, 67);
            synth.release();
            // Safe to call after release
            synth.playNote(60);
            synth.prepare(60);
        }
    }
}
