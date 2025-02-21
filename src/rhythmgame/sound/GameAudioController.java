package rhythmgame.sound;

import java.awt.event.ActionEvent;
import javax.sound.sampled.*;
import javax.swing.Timer;
import rhythmgame.LaneFrame;

public class GameAudioController {

    private SEPlayer tapNote_player;
    private SEPlayer criticalNote_player;
    private SEPlayer miss_player;

    private final String TAPNOTE_SE = "musics/se_hit_normal.wav";
    private final String CRITICALNOTE_SE = "musics/se_hit_critical.wav";
    private final String MISS_SE = "musics/se_miss.wav";

    private MusicPlayer music_player;
    public boolean isMusicPlaying = false;

    /**
     * コンストラクタ：SEとBGMのパスを指定して初期化
     */
    public GameAudioController(String music_path, LaneFrame laneFrame) {
        tapNote_player = new SEPlayer(TAPNOTE_SE, 8);
        criticalNote_player = new SEPlayer(CRITICALNOTE_SE, 8);
        miss_player = new SEPlayer(MISS_SE, 8);

        music_player = new MusicPlayer(music_path);

        music_player.setLineListener((LineEvent event) -> {
            if (event.getType() == LineEvent.Type.STOP) {
                isMusicPlaying = false;
                laneFrame.endGame();
            }
        });
    }

    // SE・BGMの設定
    public void set_tapNoteSound(String path) {
        tapNote_player = new SEPlayer(path, 8);
    }

    public void set_criticalNoteSound(String path) {
        criticalNote_player = new SEPlayer(path, 8);
    }

    public void set_music(String path) {
        music_player = new MusicPlayer(path);
    }

    public void startTapNoteSound() {
        System.out.print("TapNote   ");
        tapNote_player.startSound();
    }

    public void startCriticalNoteSound() {
        System.out.print("CriticalNote   ");
        criticalNote_player.startSound();
    }

    public void startMissSound() {
        System.out.print("Miss   ");
        miss_player.startSound();
    }

    public void startMusic() {
        music_player.startSound();
    }

    public void stopMusic() {
        music_player.stopSound();
    }

    public void stopMusic(float fadeOutSpeed) {
        music_player.stopSound_withFadeOut(fadeOutSpeed);
    }

    public void stopMusic_atTime(int time_ms) {
        Timer timer = new Timer(time_ms, (ActionEvent e) -> {
            stopMusic(0.05f);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
