package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plays decaying sine "piano" notes addressed by MIDI note number (60 = middle C, 69 = A4 = 440 Hz).
 *
 * <p>v2 synthesises and allocates an {@link AudioTrack} lazily on first use of each note and keeps
 * at most {@value #MAX_CACHED_TRACKS} of them (least-recently-used are released). v1 eagerly built
 * 85 tracks in the constructor on the calling thread (several MB of PCM and 85 native tracks, past
 * the per-process track limit on many devices).</p>
 */
public final class SoundSynthesizer implements AutoCloseable {

    private static final int SAMPLE_RATE = 44_100;
    private static final int DURATION_MS = 500;
    private static final int FADE_OUT_MS = 5;
    private static final int MAX_CACHED_TRACKS = 16;
    private static final int MIN_MIDI = 0;
    private static final int MAX_MIDI = 127;

    /** Offsets used by the v1 note-index table: naturals C..B, then sharps C#, D#, F#, G#, A#. */
    private static final int[] LEGACY_BLOCK_OFFSETS = {0, 2, 4, 5, 7, 9, 11, 1, 3, 6, 8, 10};

    private final Map<Integer, AudioTrack> tracks = new LinkedHashMap<Integer, AudioTrack>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, AudioTrack> eldest) {
            if (size() > MAX_CACHED_TRACKS) {
                releaseTrack(eldest.getValue());
                return true;
            }
            return false;
        }
    };
    private boolean released;

    /** @return frequency in Hz of a MIDI note (equal temperament, A4 = 440 Hz). */
    public static double frequencyOf(int midiNote) {
        return 440.0 * Math.pow(2.0, (midiNote - 69) / 12.0);
    }

    /**
     * Plays a note, restarting it if already sounding. Safe to call from any thread.
     *
     * @param midiNote 0-127
     * @throws IllegalArgumentException if out of range
     */
    public synchronized void playNote(int midiNote) {
        if (midiNote < MIN_MIDI || midiNote > MAX_MIDI) {
            throw new IllegalArgumentException("MIDI note must be 0-127: " + midiNote);
        }
        if (released) return;
        AudioTrack track = tracks.get(midiNote);
        if (track == null) {
            track = createTrack(midiNote);
            tracks.put(midiNote, track);
        }
        track.stop();
        track.reloadStaticData();
        track.play();
    }

    /**
     * Plays a note using the v1 index table (0-83 = octaves 1-7 as C D E F G A B C# D# F# G# A#,
     * 84 = C8). Out-of-range indices are ignored, as in v1.
     *
     * @deprecated Use {@link #playNote(int)} with a MIDI note number (v1 index 0 = C1 = MIDI 24).
     */
    @Deprecated
    public void playTone(int noteIndex) {
        if (noteIndex < 0 || noteIndex > 84) return;
        int octave = noteIndex / 12 + 1;
        int midi = 12 * (octave + 1) + LEGACY_BLOCK_OFFSETS[noteIndex % 12];
        playNote(midi);
    }

    /** Releases every native track. Further {@code playNote} calls do nothing. */
    public synchronized void release() {
        released = true;
        for (AudioTrack track : tracks.values()) {
            releaseTrack(track);
        }
        tracks.clear();
    }

    @Override
    public void close() {
        release();
    }

    private static void releaseTrack(AudioTrack track) {
        try {
            track.stop();
        } catch (IllegalStateException ignored) {
            // already stopped/uninitialised
        }
        track.release();
    }

    private static AudioTrack createTrack(int midiNote) {
        int samples = SAMPLE_RATE * DURATION_MS / 1000;
        int fadeSamples = SAMPLE_RATE * FADE_OUT_MS / 1000;
        double angularStep = 2.0 * Math.PI * frequencyOf(midiNote) / SAMPLE_RATE;
        short[] buffer = new short[samples];
        for (int i = 0; i < samples; i++) {
            double envelope = Math.exp(-3.5 * i / samples);            // exponential decay
            double fade = Math.min(1.0, (samples - i) / (double) fadeSamples); // avoids an end-of-note click
            buffer[i] = (short) (Math.sin(angularStep * i) * envelope * fade * Short.MAX_VALUE);
        }

        AudioTrack track = new AudioTrack.Builder()
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
        track.write(buffer, 0, buffer.length);
        return track;
    }
}
