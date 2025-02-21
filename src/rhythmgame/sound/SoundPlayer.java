package rhythmgame.sound;

import java.io.File;
import javax.sound.sampled.*;

/**
 * サウンド再生用の基底クラス
 */
public class SoundPlayer {
    protected Clip currentClip;
    protected File file;

    public SoundPlayer(String path) {
        file = new File(path);
    }
    //ファイル名を取得
    public String getFileAsString() {
        return file.toString();
    }
}