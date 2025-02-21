package rhythmgame.sound;

import java.io.*;
import javax.sound.sampled.*;
import javax.sound.sampled.LineEvent.Type;

/**
 * BGMや長尺の音源再生に適したクラス。 （現在はSoundPlayerを継承して時間管理に使う変数を増やしただけの例）
 */
public class MusicPlayer extends SoundPlayer {

    private SourceDataLine s;
    private AudioInputStream ais;
    private Thread playbackThread;
    private volatile boolean running = false, paused = false;
    private LineListener lineListener;

    public MusicPlayer(String path) {
        super(path);
        createMusic(file);
    }

    private void createMusic(File file) {
        try (FileInputStream audioStream = new FileInputStream(file)) {
            ais = AudioSystem.getAudioInputStream(file);
            AudioFormat af = ais.getFormat();
            DataLine.Info dataLine = new DataLine.Info(SourceDataLine.class, af);
            s = (SourceDataLine) AudioSystem.getLine(dataLine);
            s.addLineListener(event -> {
                if (event.getType() == Type.STOP && lineListener != null) {
                    lineListener.update(event);
                }
            });
        } catch (Exception e) {
        }
    }

    public void startSound() {
        try {
            s.open();
            s.start();

            playbackThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                running = true;
                try {
                    while (running && (bytesRead = ais.read(buffer)) != -1) {
                        synchronized (this) {
                            while (paused) {
                                wait();
                            }
                        }
                        s.write(buffer, 0, bytesRead);
                    }
                } catch (IOException | InterruptedException e) {
                } finally {
                    cleanup();
                }

            });
            playbackThread.start();
        } catch (Exception e) {
        }
    }

    public synchronized void pauseMusic() {
        if (!paused && running) {
            paused = true;
        }
    }

    public synchronized void resumeMusic() {
        if (paused) {
            paused = false;
        }
    }

    private void cleanup() {
        if (s != null) {
            s.stop();
            s.close();
        }
        playbackThread = null;
        running = false;
        paused = false;
    }

    public void stopSound() {
        running = false;
        if (paused) {
            resumeMusic();
        }
        cleanup();
    }

    private synchronized void setVolume(float volume) {
        if (s != null) {
            FloatControl control = (FloatControl) s.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(20f * (float) Math.log10(volume));
        }
    }

    public void stopSound_withFadeOut(float fadeOutSpeed) {
        for (float i = 1.0f; i >= 0.0f; i -= fadeOutSpeed) {
            setVolume(i);
            System.out.println("Volume: " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        stopSound();
    }

    public void setLineListener(LineListener listener) {
        this.lineListener = listener;
    }
}
