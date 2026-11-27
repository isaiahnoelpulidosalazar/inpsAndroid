# SoundSynthesizer

`SoundSynthesizer` plays decaying sine "piano" notes addressed by MIDI note number (`60` = middle C, `69` = A4 = 440 Hz).

## Purpose & Scope

The purpose of this component is to provide:
- Instant tone playback with no audio assets
- Lazy synthesis: each note's `AudioTrack` is created on first use, with at most 16 cached (least-recently-used tracks are released)
- Thread-safe playback and explicit resource release

Each note lasts 500 ms with an exponential decay and a short fade-out to avoid clicks. The class is `final` and implements `AutoCloseable`.

## API Reference

### static double frequencyOf(int midiNote)

Returns the frequency in Hz of a MIDI note (equal temperament, A4 = 440 Hz).

```java
double hz = SoundSynthesizer.frequencyOf(69); // 440.0
```

### void playNote(int midiNote)

Plays a note (0-127), restarting it if already sounding. Safe to call from any thread. Throws `IllegalArgumentException` if out of range, and does nothing after `release()`.

```java
SoundSynthesizer synth = new SoundSynthesizer();
synth.playNote(60);
```

### void playTone(int noteIndex)

**Deprecated.** Plays a note using the v1 index table (0-83 = octaves 1-7 as `C D E F G A B C# D# F# G# A#`, 84 = C8). Out-of-range indices are ignored. v1 index `0` (C1) equals MIDI `24`.

```java
synth.playTone(0); // same as playNote(24)
```

### void release()

Releases every native track. Further `playNote` calls do nothing.

```java
synth.release();
```

### void close()

Equivalent to `release()`, enabling try-with-resources.

```java
try (SoundSynthesizer s = new SoundSynthesizer()) {
    s.playNote(72);
}
```
