# SoundSynthesizer

Package: `com.isaiahnoelpulidosalazar.inpsandroid.edu`

`SoundSynthesizer` plays decaying sine "piano" notes addressed by MIDI note number (`60` = middle C, `69` = A4 = `440.0 Hz`). Track synthesis, allocation, and playback scheduling run off the calling thread on a dedicated single-thread executor, caching up to 16 `AudioTrack` instances (LRU eviction) and enabling `AudioTrack.PERFORMANCE_MODE_LOW_LATENCY` on API 26+.

## API Reference

| Method | Description |
|---|---|
| `SoundSynthesizer.frequencyOf(int midiNote)` | Returns equal-temperament frequency in Hz (`A4 = 440.0`). |
| `prepare(int... notes)` | Pre-builds and caches `AudioTrack` instances asynchronously for MIDI notes `0..127`. |
| `playNote(int midiNote)` | Synthesizes (if needed) and plays a MIDI note `0..127` asynchronously, restarting if already sounding. |
| `release()` / `close()` | Releases all native `AudioTrack` instances and shuts down the executor (`AutoCloseable`). Subsequent calls are no-ops. |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.edu.SoundSynthesizer;

SoundSynthesizer synth = new SoundSynthesizer();
synth.prepare(60, 64, 67, 72); // C major chord

synth.playNote(60); // Middle C
double hz = SoundSynthesizer.frequencyOf(69); // 440.0

// In onDestroy()
synth.release();
```
