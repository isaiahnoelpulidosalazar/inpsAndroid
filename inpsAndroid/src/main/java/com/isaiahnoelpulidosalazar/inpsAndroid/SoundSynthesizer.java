package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

public class SoundSynthesizer {
    private static final int SAMPLE_RATE = 44100;
    private static final int DURATION_MS = 500;

    private static final double[] FREQUENCIES = {
        // Octave 1
        32.70,  // C1
        36.71,  // D1
        41.20,  // E1
        43.65,  // F1
        49.00,  // G1
        55.00,  // A1
        61.74,  // B1
        34.65,  // C#1
        38.89,  // D#1
        46.25,  // F#1
        51.91,  // G#1
        58.27,  // A#1

        // Octave 2
        65.41,  // C2
        73.42,  // D2
        82.41,  // E2
        87.31,  // F2
        98.00,  // G2
        110.00, // A2
        123.47, // B2
        69.30,  // C#2
        77.78,  // D#2
        92.50,  // F#2
        103.83, // G#2
        116.54, // A#2

        // Octave 3
        130.81, // C3
        146.83, // D3
        164.81, // E3
        174.61, // F3
        196.00, // G3
        220.00, // A3
        246.94, // B3
        138.59, // C#3
        155.56, // D#3
        185.00, // F#3
        207.65, // G#3
        233.08, // A#3

        // Octave 4
        261.63, // C4
        293.66, // D4
        329.63, // E4
        349.23, // F4
        392.00, // G4
        440.00, // A4
        493.88, // B4
        277.18, // C#4
        311.13, // D#4
        369.99, // F#4
        415.30, // G#4
        466.16, // A#4

        // Octave 5
        523.25, // C5
        587.33, // D5
        659.25, // E5
        698.46, // F5
        783.99, // G5
        880.00, // A5
        987.77, // B5
        554.37, // C#5
        622.25, // D#5
        739.99, // F#5
        830.61, // G#5
        932.33, // A#5

        // Octave 6
        1046.50, // C6
        1174.66, // D6
        1318.51, // E6
        1396.91, // F6
        1567.98, // G6
        1760.00, // A6
        1975.53, // B6
        1108.73, // C#6
        1244.51, // D#6
        1479.98, // F#6
        1661.22, // G#6
        1864.66, // A#6

        // Octave 7
        2093.00, // C7
        2349.32, // D7
        2637.02, // E7
        2793.83, // F7
        3135.96, // G7
        3520.00, // A7
        3951.07, // B7
        2217.46, // C#7
        2489.02, // D#7
        2959.96, // F#7
        3322.44, // G#7
        3729.31, // A#7

        // Octave 8 (End of piano range)
        4186.01  // C8
    };

    private final AudioTrack[] tracks = new AudioTrack[FREQUENCIES.length];

    public SoundSynthesizer() {
        initTracks();
    }

    private void initTracks() {
        int numSamples = (SAMPLE_RATE * DURATION_MS) / 1000;

        for (int i = 0; i < FREQUENCIES.length; i++) {
            short[] buffer = new short[numSamples];
            double freq = FREQUENCIES[i];

            for (int j = 0; j < numSamples; j++) {
                double time = (double) j / SAMPLE_RATE;
                double envelope = Math.exp(-3.5 * j / numSamples); // Exponential decay
                double sine = Math.sin(2.0 * Math.PI * freq * time);
                buffer[j] = (short) (sine * envelope * Short.MAX_VALUE);
            }

            tracks[i] = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .setBufferSizeInBytes(buffer.length * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build();

            tracks[i].write(buffer, 0, buffer.length);
        }
    }

    public void playTone(int noteIndex) {
        if (noteIndex >= 0 && noteIndex < tracks.length && tracks[noteIndex] != null) {
            AudioTrack track = tracks[noteIndex];
            track.stop();
            track.reloadStaticData();
            track.play();
        }
    }

    public void release() {
        for (AudioTrack track : tracks) {
            if (track != null) {
                track.stop();
                track.release();
            }
        }
    }
}