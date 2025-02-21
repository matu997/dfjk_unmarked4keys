package rhythmgame.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

/**
 * エフェクト専用パネル (最前面)
 * - ヒットエフェクト/ジャッジメントエフェクトのリストを保持
 * - タイマーで定期更新してフレーム進行
 */
public class EffectPanel extends JPanel {

    private List<HitEffectAnimation> activeHitEffects = new ArrayList<>();
    private List<JudgmentEffectAnimation> activeJudgmentEffects = new ArrayList<>();

    private Timer animationTimer;

    public EffectPanel() {
        setOpaque(false);

        // 30FPS
        int fps = 30;
        int interval = 1000 / fps;
        animationTimer = new Timer(interval, e -> {
            // フレーム進行
            Iterator<HitEffectAnimation> hitItr = activeHitEffects.iterator();
            while (hitItr.hasNext()) {
                HitEffectAnimation eff = hitItr.next();
                eff.update();
                if (!eff.isActive()) {
                    hitItr.remove();
                }
            }

            Iterator<JudgmentEffectAnimation> judgItr = activeJudgmentEffects.iterator();
            while (judgItr.hasNext()) {
                JudgmentEffectAnimation eff = judgItr.next();
                eff.update();
                if (!eff.isActive()) {
                    judgItr.remove();
                }
            }
            repaint();
        });
        animationTimer.start(); // 常にアニメを回しておく
    }

    /**
     * ヒットエフェクトを追加
     */
    public void addHitEffect(HitEffectAnimation effect, int x, int y) {
        effect.setPosition(x, y);
        effect.start();
        activeHitEffects.add(effect);
    }

    /**
     * ジャッジメントエフェクトを追加
     */
    public void addJudgmentEffect(JudgmentEffectAnimation effect, String type, int x, int y) {
        effect.start(type);
        effect.setPosition(x, y);
        activeJudgmentEffects.add(effect);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // ヒットエフェクト描画
        for (HitEffectAnimation eff : activeHitEffects) {
            eff.draw(g2d);
        }

        // ジャッジメントエフェクト描画
        for (JudgmentEffectAnimation eff : activeJudgmentEffects) {
            eff.draw(g2d);
        }

        g2d.dispose();
    }
}
