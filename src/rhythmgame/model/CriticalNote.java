package rhythmgame.model;

import java.awt.Graphics;

/**
 * 得点2倍ノート(CriticalNote)の実装クラス
 */
public class CriticalNote extends Note {

    public CriticalNote(double x, double y) {
        super(x, y);
        setImage("images/critical_note.png");
    }

    public CriticalNote() {
        super(75, -1000);
        setImage("images/critical_note.png");
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            int drawX = (int) (centerX - width / 2);
            int drawY = (int) (centerY - height / 2);
            g.drawImage(image, drawX, drawY, width, height, null);
        } else {
            // デバッグ用: 画像が読み込めなかった場合
            g.fillOval((int) centerX, (int) centerY, (int) radius * 2, (int) radius * 2);
        }
    }
}