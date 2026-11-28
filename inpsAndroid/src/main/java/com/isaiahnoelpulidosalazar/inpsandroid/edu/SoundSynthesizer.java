package com.isaiahnoelpulidosalazar.inpsandroid.edu;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Build;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Plays decaying sine "piano" notes addressed by MIDI note number (60 = middle C, 69 = A4 = 440
 * Hz).
 *
 * <p>Track synthesis, allocation, and playback scheduling run on a dedicated background
 * single-thread executor so {@link #playNote(int)} and {@link #prepare(int...)} never block the
 * calling thread. At most {@value #MAX_CACHED_TRACKS} native {@link AudioTrack} instances are
 * cached (least-recently-used tracks are released).
 */
public final class SoundSynthesizer implements AutoCloseable {

  private static final int SAMPLE_RATE = 44_100;
  private static final int DURATION_MS = 500;
  private static final int FADE_OUT_MS = 5;
  private static final int MAX_CACHED_TRACKS = 16;
  private static final int MIN_MIDI = 0;
  private static final int MAX_MIDI = 127;
  private static final double AMPLITUDE_SCALE = 0.5;

  private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
  private final Map<Integer, AudioTrack> tracks =
      new LinkedHashMap<Integer, AudioTrack>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, AudioTrack> eldest) {
          if (size() > MAX_CACHED_TRACKS) {
            releaseTrack(eldest.getValue());
            return true;
          }
          return false;
        }
      };
  private volatile boolean released;

  /**
   * Converts a MIDI note number to frequency in Hertz under 12-tone equal temperament ({@code A4 =
   * MIDI 69 = 440 Hz}).
   *
   * @param midiNote MIDI note number (typically {@code 0..127})
   * @return fundamental frequency in Hertz
   */
  public static double frequencyOf(int midiNote) {
    return 440.0 * Math.pow(2.0, (midiNote - 69) / 12.0);
  }

  /**
   * Pre-builds and caches {@link AudioTrack} instances for {@code notes} on the background
   * executor. Safe to call from the UI thread.
   *
   * @param notes MIDI note numbers in {@code 0..127}
   * @throws IllegalArgumentException if any note is outside {@code 0..127}
   * @throws NullPointerException if {@code notes} is {@code null}
   */
  public void prepare(int... notes) {
    Objects.requireNonNull(notes, "notes");
    final int[] copy = notes.clone();
    for (int note : copy) {
      validateMidiNote(note);
    }
    if (released) return;
    audioExecutor.execute(
        () -> {
          if (released) return;
          for (int note : copy) {
            if (released) return;
            getOrCreateTrack(note);
          }
        });
  }

  /**
   * Plays a note asynchronously, restarting it if already sounding. Track synthesis and playback
   * run off the calling thread on the internal single-thread executor.
   *
   * @param midiNote MIDI note number in {@code 0..127}
   * @throws IllegalArgumentException if {@code midiNote} is outside {@code 0..127}
   */
  public void playNote(int midiNote) {
    validateMidiNote(midiNote);
    if (released) return;
    audioExecutor.execute(
        () -> {
          if (released) return;
          AudioTrack track = getOrCreateTrack(midiNote);
          if (track == null) return;
          try {
            track.stop();
          } catch (IllegalStateException ignored) {
            // Not playing yet
          }
          if (track.reloadStaticData() == AudioTrack.SUCCESS) {
            track.play();
          }
        });
  }

  /**
   * Releases every cached native {@link AudioTrack} and shuts down the background executor.
   * Subsequent calls to {@link #prepare(int...)} or {@link #playNote(int)} become no-ops.
   */
  public void release() {
    if (released) return;
    released = true;
    audioExecutor.execute(
        () -> {
          for (AudioTrack track : tracks.values()) {
            releaseTrack(track);
          }
          tracks.clear();
        });
    audioExecutor.shutdown();
  }

  /** Closes this synthesizer by delegating to {@link #release()}. */
  @Override
  public void close() {
    release();
  }

  private static void validateMidiNote(int midiNote) {
    if (midiNote < MIN_MIDI || midiNote > MAX_MIDI) {
      throw new IllegalArgumentException("MIDI note must be 0-127: " + midiNote);
    }
  }

  private AudioTrack getOrCreateTrack(int midiNote) {
    AudioTrack track = tracks.get(midiNote);
    if (track == null) {
      track = createTrack(midiNote);
      if (track != null) {
        tracks.put(midiNote, track);
      }
    }
    return track;
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
      double envelope = Math.exp(-3.5 * i / samples);
      double fade = Math.min(1.0, (samples - i) / (double) fadeSamples);
      buffer[i] =
          (short) (Math.sin(angularStep * i) * envelope * fade * AMPLITUDE_SCALE * Short.MAX_VALUE);
    }

    AudioTrack.Builder builder =
        new AudioTrack.Builder()
            .setAudioAttributes(
                new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            .setAudioFormat(
                new AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
            .setBufferSizeInBytes(buffer.length * 2)
            .setTransferMode(AudioTrack.MODE_STATIC);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY);
    }

    AudioTrack track = builder.build();
    int written = track.write(buffer, 0, buffer.length);
    if (written < 0) {
      track.release();
      return null;
    }
    return track;
  }
}
