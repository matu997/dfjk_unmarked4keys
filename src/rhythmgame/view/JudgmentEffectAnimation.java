// JudgmentEffectAnimation.java
package rhythmgame.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * GOOD/PERFECTなどの判定エフェクトアニメ。外部から update() 呼び出しでフレーム進行。
 */
public class JudgmentEffectAnimation {

    private Map<String, ArrayList<Image>> judgmentFramesMap = new HashMap<>();
    private ArrayList<Image> currentFrames = null;
    private int currentFrameIndex = -1;
    private boolean active = false;

    private int x, y;

    public JudgmentEffectAnimation() { }

    public void addJudgmentFrames(String judgmentType, ArrayList<Image> frames) {
        judgmentFramesMap.put(judgmentType.toUpperCase(), frames);
    }

    public void start(String judgmentType) {
        active = false;
        currentFrameIndex = -1;

        ArrayList<Image> frames = judgmentFramesMap.get(judgmentType.toUpperCase());
        if (frames == null || frames.isEmpty()) {
            return;
        }
        currentFrames = frames;
        currentFrameIndex = 0;
        active = true;
    }

    public void update() {
        if (!active || currentFrames == null) return;
        if (currentFrameIndex < currentFrames.size() - 1) {
            currentFrameIndex++;
        } else {
            active = false;
            currentFrameIndex = -1;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active || currentFrames == null || currentFrameIndex < 0) {
            return;
        }
        Image frame = currentFrames.get(currentFrameIndex);
        if (frame != null) {
            int w = frame.getWidth(null);
            int h = frame.getHeight(null);
            g2d.drawImage(frame, x, y, w, h, null);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isActive() {
        return active;
    }
}
