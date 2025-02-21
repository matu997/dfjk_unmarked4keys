package rhythmgame.view;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * 背景アニメーションを表示するパネル。
 * 連番画像を一定間隔で切り替えて描画。
 */
public class BackgroundPanel extends JPanel {

    private ArrayList<Image> backgroundImages;
    private int currentBackgroundFrame = 0;
    private Timer animationTimer;

    public BackgroundPanel(ArrayList<Image> backgroundImages) {
        this.backgroundImages = backgroundImages;

        int fps = 30;
        int interval = 1000 / fps;
        animationTimer = new Timer(interval, e -> {
            if (!this.backgroundImages.isEmpty()) {
                currentBackgroundFrame = (currentBackgroundFrame + 1) % this.backgroundImages.size();
                repaint();
            }
        });
    }

    /** 背景アニメ開始 */
    public void startAnimation() {
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!backgroundImages.isEmpty()) {
            Image frame = backgroundImages.get(currentBackgroundFrame);
            g.drawImage(frame, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
