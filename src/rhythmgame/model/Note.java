package rhythmgame.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * ノートを表す抽象クラス。
 * TapNote / TraceNote などがこれを継承し、それぞれの描画ロジックや挙動をオーバーライドする。
 */
public abstract class Note {
    // 描画用の変数
    protected double centerX, centerY;
    protected double radius = 10; 
    protected Color color;
    protected Image image;
    protected int width = 150;
    protected int height = 50;

    // ゲーム用の変数
    protected double time;   // 判定ライン到達までの時間(ms)
    protected double offset; // 残り時間(ms)、随時更新

    public Note(double x, double y) {
        this.centerX = x;
        this.centerY = y;
    }

    public Note() {
        this(50, -1000);
    }

    public void setX(double x) {
        this.centerX = x;
    }

    public void setY(double y) {
        this.centerY = y;
    }

    public void setRadius(double r) {
        this.radius = r;
    }

    public void setTime(double t) {
        this.time = t;
    }

    public void setOffset(double o) {
        this.offset = o;
    }

    public double getOffset() {
        return offset;
    }

    public final void setImage(String path) {
        this.image = new ImageIcon(path).getImage();
    }

    public void setPosition(double x, double y) {
        this.centerX = x;
        this.centerY = y;
    }

    /**
     * ノートの描画処理。
     * @param g Graphicsオブジェクト
     */
    public abstract void draw(Graphics g);
}