package rhythmgame.frame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import rhythmgame.view.*;
import rhythmgame.model.GameStatus;

/**
 * タイトル/メニュー画面を示すサンプルフレーム。 難易度選択や曲選択を行い、「スタート」ボタンで LaneFrame を開く。
 */
public class ResultFrame extends JFrame implements KeyListener {

    private ResultPanel backgroundPanel;

    public ResultFrame(int score, GameStatus gameStatus) {
        setTitle("Rhythm Game Result");
        setSize(1600, 1200);

        //setExtendedState(JFrame.MAXIMIZED_BOTH); //最大化

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int[] judgmentCount = gameStatus.getJudgmentCount();

        int perfect = judgmentCount[0]; // 0: perfect
        int great = judgmentCount[1]; // 1: great
        int miss = judgmentCount[2] + judgmentCount[3] + judgmentCount[4]; // otherwise

        // 背景画像を設定するパネル
        backgroundPanel = new ResultPanel("images/result_bg.png", score, perfect, great, miss);
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        addKeyListener(this);
        setFocusable(true);

        // リサイズ時に再配置
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                backgroundPanel.repaint();
            }
        });

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // エンターキーで開始、上下キーで選択
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER -> {
                SelectionFrame titleFrame = new SelectionFrame();
                titleFrame.setVisible(true);
                dispose();
            }
            case KeyEvent.VK_UP -> {
            }
            case KeyEvent.VK_DOWN -> {
            }
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}

final class ResultPanel extends JPanel {

    private final Image backgroundImage;
    private Image[] scoreDigitImages;
    private Image[] countDigitImages;
    private final Image rankImage;
    private final int score;
    private final int perfect;
    private final int great;
    private final int miss;

    public ResultPanel(String filePath, int score, int perfect, int great, int miss) {
        this.score = score;
        this.perfect = perfect;
        this.great = great;
        this.miss = miss;
        this.backgroundImage = new ImageIcon(filePath).getImage();
        setLayout(new BorderLayout());

        scoreDigitImages = new Image[10];
        countDigitImages = new Image[10];

        // 通常時の画像を読み込み（例："images/score/0.png", "images/score/1.png", ...）
        for (int i = 0; i < 10; i++) {
            scoreDigitImages[i] = new ImageIcon("images/score/big_" + i + ".png").getImage();
        }
        // 変化時の画像を読み込み（例："images/score/f0.png", "images/score/f1.png", ...）
        for (int i = 0; i < 10; i++) {
            countDigitImages[i] = new ImageIcon("images/score/small_" + i + ".png").getImage();
        }
        if (score >= 1400000) {
            rankImage = new ImageIcon("images/S.png").getImage();
        } else if (score >= 1000000) {
            rankImage = new ImageIcon("images/A.png").getImage();
        } else if (score >= 100000) {
            rankImage = new ImageIcon("images/B.png").getImage();
        } else {
            rankImage = new ImageIcon("images/B.png").getImage();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        int rankWidth = (rankImage != null) ? (int) (rankImage.getWidth(this) * 0.9) : 0;
        int rankHeight = (rankImage != null) ? (int) (rankImage.getHeight(this) * 0.9) : 0;
        System.out.println(this.getWidth() + " " + this.getHeight());
        g.drawImage(rankImage, 1270 * this.getWidth() / 1586, (190 * this.getHeight() / 941)+15, rankWidth * this.getWidth() / 1586, rankHeight * this.getHeight() / 1180, this);

        int scoreWidth = (scoreDigitImages[0] != null) ? scoreDigitImages[0].getWidth(this) : 0;
        int scoreHeight = (scoreDigitImages[0] != null) ? scoreDigitImages[0].getHeight(this) : 0;
        int x = 990 * this.getWidth() / 1586;
        int y = 450 * this.getHeight() / 941;
        int padding = 2;
        String scoreStr = String.format("%08d", score);
        for (int i = 0; i < scoreStr.length(); i++) {
            char c = scoreStr.charAt(i);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                Image digitImage = scoreDigitImages[digit];
                if (digitImage != null) {
                    g.drawImage(digitImage, x, y, scoreWidth, scoreHeight, this);
                }
                x += scoreWidth + padding;
            }
        }

        int countWidth = (countDigitImages[0] != null) ? (int) (countDigitImages[0].getWidth(this) * 0.85 * this.getWidth() / 1586) : 0;
        int countHeight = (countDigitImages[0] != null) ? (int) (countDigitImages[0].getHeight(this) * 0.85 * this.getHeight() / 1180) : 0;
        x = 1460 * this.getWidth() / 1586;
        y = (578 * this.getHeight() / 941)+5;
        padding = 1;
        String countPerfectStr = String.format("%04d", perfect);
        for (int i = 0; i < countPerfectStr.length(); i++) {
            char c = countPerfectStr.charAt(i);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                Image digitImage = countDigitImages[digit];
                if (digitImage != null) {
                    g.drawImage(digitImage, x, y, countWidth, countHeight, this);
                }
                x += countWidth + padding;
            }
        }
        x -= (padding + countWidth) * countPerfectStr.length();
        y += 40 * this.getHeight() / 941;
        String countGreatStr = String.format("%04d", great);
        for (int i = 0; i < countGreatStr.length(); i++) {
            char c = countGreatStr.charAt(i);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                Image digitImage = countDigitImages[digit];
                if (digitImage != null) {
                    g.drawImage(digitImage, x, y, countWidth, countHeight, this);
                }
                x += countWidth + padding;
            }
        }
        x -= (padding + countWidth) * countPerfectStr.length();
        y += 40 * this.getHeight() / 941;
        String countMissStr = String.format("%04d", miss);
        for (int i = 0; i < countMissStr.length(); i++) {
            char c = countMissStr.charAt(i);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                Image digitImage = countDigitImages[digit];
                if (digitImage != null) {
                    g.drawImage(digitImage, x, y, countWidth, countHeight, this);
                }
                x += countWidth + padding;
            }
        }

    }
}
