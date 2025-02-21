// HitEffectAnimation.java
package rhythmgame.view;

import java.awt.*;
import java.util.ArrayList;

/**
 * ノート叩きエフェクト。外部から update() でフレーム進行。
 */
public class HitEffectAnimation {
    private final ArrayList<Image> frames;
    private int currentFrame = -1;
    private boolean active = false;
    private int x, y;

    public HitEffectAnimation(ArrayList<Image> frames) {
        this.frames = frames;
    }

    public void start() {
        if (frames.isEmpty()) return;
        currentFrame = 0;
        active = true;
    }

    public void update() {
        if (!active) return;
        if (currentFrame < frames.size() - 1) {
            currentFrame++;
        } else {
            active = false;
            currentFrame = -1;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active || currentFrame < 0 || currentFrame >= frames.size()) {
            return;
        }
        Image frameImage = frames.get(currentFrame);
        if (frameImage != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            int w = frameImage.getWidth(null);
            int h = frameImage.getHeight(null);
            g2d.drawImage(frameImage, x, y, w, h, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}