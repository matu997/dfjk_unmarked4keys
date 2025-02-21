package rhythmgame.model;

import java.awt.Graphics;

/**
 * ホールド/スライド系ノート(TraceNote)の実装クラス
 */
public class TraceNote extends Note {

    public TraceNote(double x, double y) {
        super(x, y);
        setImage("images/trace_note.png");
    }

    public TraceNote() {
        super(75, -1000);
        setImage("images/trace_note.png");
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