package rhythmgame.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.LineBorder;
import rhythmgame.controller.Controller;
import rhythmgame.model.Note;
import rhythmgame.model.NotesModel;

/**
 * レーン描画パネル本体。
 * - 背景・キー画像
 * - ノート描画
 * 
 * エフェクトはすべて EffectPanel に追加してもらう。
 */
@SuppressWarnings("deprecation")
public class ViewLane extends JPanel implements Observer {

    private final NotesModel model;
    private EffectPanel effectPanel; // ★ 背景ではなく「エフェクト専用パネル」

    private Image laneImage;
    private Image defaultKeyImage;
    private Image pressedKeyImage;
    private boolean keyPressed = false;

    private int judgmentLineY = 410;

    //デバック用fps計測
    private long lastTime = System.currentTimeMillis();
    private int paintCount = 0;

    public ViewLane(NotesModel model, Controller controller, ViewUpdate viewUpdate,
                    EffectPanel effectPanel) {
        super(true);
        this.model = model;
        this.effectPanel = effectPanel;

        setBackground(new Color(0, 0, 0, 0));
        setPreferredSize(new Dimension(120, 500));

        addKeyListener(controller);
        viewUpdate.addObserver(this);
    }

    public void setLaneImage(String imagePath) {
        laneImage = new ImageIcon(imagePath).getImage();
    }

    public void setKeyImages(String defaultImagePath, String pressedImagePath) {
        defaultKeyImage = new ImageIcon(defaultImagePath).getImage();
        pressedKeyImage = new ImageIcon(pressedImagePath).getImage();
    }

    public void setKeyPressed(boolean pressed) {
        keyPressed = pressed;
        repaint();
    }

    /** ヒットエフェクト (ノートを叩いた瞬間) */
    public void startEffect() {
        // 連番画像
        ArrayList<Image> frames = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            String filename = String.format("effects/effect_%02d.png", i);
            frames.add(Toolkit.getDefaultToolkit().getImage(filename));
        }
        HitEffectAnimation effect = new HitEffectAnimation(frames);

        // レーン中央付近
        int x = getX() + (getWidth() / 2) - 75;
        int y = judgmentLineY - 1000;

        // effectPanelに追加
        effectPanel.addHitEffect(effect, x, y);
    }

    /** 判定エフェクト (GOOD / PERFECTなど) */
    public void showJudgmentEffect(String judgmentType) {
        JudgmentEffectAnimation effect = new JudgmentEffectAnimation();

        // GOOD用
        ArrayList<Image> goodFrames = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            String filename = String.format("effects/effect_hit_normal/effect_%02d.png", i);
            goodFrames.add(new ImageIcon(filename).getImage());
        }
        effect.addJudgmentFrames("normal", goodFrames);

        // PERFECT用
        ArrayList<Image> perfectFrames = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            String filename = String.format("effects/effect_hit_critical/hit_critical_%02d.png", i);
            perfectFrames.add(new ImageIcon(filename).getImage());
        }
        effect.addJudgmentFrames("critical", perfectFrames);

        // 座標
        int x = getX() + (getWidth() / 2) - 150; 
        int y = 850;

        // effectPanelに追加
        effectPanel.addJudgmentEffect(effect, judgmentType, x, y);
    }

    public void updateJudgmentLine(int panelHeight) {
        judgmentLineY = panelHeight - 200;
    }

    public int getJudgmentLineY() {
        return judgmentLineY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //デバック用fps計測
        paintCount++;
        long now = System.currentTimeMillis();
        if (now - lastTime >= 1000) {
            System.out.println("Paint calls in last second: " + paintCount);
            paintCount = 0;
            lastTime = now;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        if (laneImage != null) {
            float alpha = 0.7f;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(laneImage, 0, 0, getWidth(), getHeight(), null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        for (Note note : model.getNotes()) {
            note.draw(g2d);
        }

        // キー画像
        Image keyImage = keyPressed ? pressedKeyImage : defaultKeyImage;
        if (keyImage != null) {
            int keyWidth = 150;
            int keyHeight = 200;
            int keyX = (getWidth() - keyWidth) / 2;
            int keyY = getHeight() - 200;
            g2d.drawImage(keyImage, keyX, keyY, keyWidth, keyHeight, null);
        }

        // 判定ライン
        g2d.setColor(Color.BLACK);
        g2d.drawLine(0, judgmentLineY, getWidth(), judgmentLineY);

        g2d.dispose();
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        //デバック用fps計測
        repaint();

    }
}
