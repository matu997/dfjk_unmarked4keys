package rhythmgame.sound;

import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 * SE再生用クラス。複数のClipプールを持ち、同時発音に対応できるようにする。
 */
public class SEPlayer extends SoundPlayer{
    protected Clip currentClip;
    private final ArrayList<Clip> clipPool = new ArrayList<>();
    private int currentClipIndex = 0;

    /**
     * 指定したSEを用意し、いつでもstartSE()を呼んで再生できる。
     * @param path 音声ファイルの相対アドレス（wavのみ）
     * @return null
     */

    public SEPlayer(String path, int poolSize) {
        super(path);
        
        for (int i = 0; i < poolSize; i++) {
            Clip clip = createClip(); // pathで指定したオーディオのClipが作成される
            clipPool.add(clip); // プールにClipを格納していく
        }
    }
    
    protected final Clip createClip() {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
        
            // ファイルの形式取得
            AudioFormat af = ais.getFormat();

            // 単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
            DataLine.Info dataLine = new DataLine.Info(Clip.class, af);

            // 指定された Line.Info オブジェクトの記述に一致するラインを取得
            Clip c = (Clip) AudioSystem.getLine(dataLine);

            // 再生準備完了
            c.open(ais);

            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // SEの再生（clipPoolに格納されたClipを１つずつ再生する）
    public void startSound() {
        if(clipPool.isEmpty()){
            System.out.println("Clipプールが空です");
        }else{
            currentClip = clipPool.get(currentClipIndex);
            currentClip.stop();
            currentClip.flush();
            currentClip.setFramePosition(0);
            currentClip.start();
            currentClipIndex = (currentClipIndex + 1) % clipPool.size();

            //debug
            System.out.println("currentClipIndex:"+currentClipIndex);
        }
    }

    //音を止める
    public void stopSound() {
        currentClip.stop();
        currentClip.flush();
    }
}