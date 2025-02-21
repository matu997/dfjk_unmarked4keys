package rhythmgame.view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import rhythmgame.model.GameStatus;

@SuppressWarnings("deprecation")
public class ScorePanel extends JPanel implements Observer {

    private GameStatus gameStatus;
    private Image backgroundImage;         // 背景画像用フィールド
    private Image[] digitImages;             // 通常時の数字画像
    private Image[] change_digitImages;      // スコア更新時の数字画像
    private String previousScoreStr;         // 前回のスコア（8桁の文字列）
    private boolean scoreChanged;            // 全体が変化しているかどうかのフラグ
    private static final int CHANGE_DURATION = 100; // 変化状態を維持する時間（ミリ秒）

    public ScorePanel(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        // GameStatus のオブザーバーとして自身を登録
        this.gameStatus.addObserver(this);

        // パネルの背景を透過に設定
        setOpaque(false);

        // 背景画像は初期状態では null。後から setImage() で設定可能。
        backgroundImage = null;

        // 数字画像の配列を生成
        digitImages = new Image[10];
        change_digitImages = new Image[10];

        // 通常時の画像を読み込み（例："images/score/0.png", "images/score/1.png", ...）
        for (int i = 0; i < 10; i++) {
            digitImages[i] = new ImageIcon("images/score/" + i + ".png").getImage();
        }
        // 変化時の画像を読み込み（例："images/score/f0.png", "images/score/f1.png", ...）
        for (int i = 0; i < 10; i++) {
            change_digitImages[i] = new ImageIcon("images/score/f" + i + ".png").getImage();
        }

        // 初期スコアを GameStatus から取得し8桁文字列に整形
        int score = gameStatus.getGameScore();
        previousScoreStr = String.format("%08d", score);
        scoreChanged = false;
    }

    /**
     * 背景画像を設定するためのメソッド 例: scorePanel.setImage("images/score/scoreBoard.png");
     */
    public void setImage(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景画像をパネル全体に描画
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // GameStatus から最新のスコアを取得し、8桁の文字列に整形
        int score = gameStatus.getGameScore();
        String scoreStr = String.format("%08d", score);

        // 数字画像の描画位置・間隔の設定
        int x = 20;           // 左からの描画開始位置（適宜調整してください）
        int padding = 5;      // 数字画像同士の間隔
        int digitWidth = (digitImages[0] != null) ? digitImages[0].getWidth(this) : 0;
        int digitHeight = (digitImages[0] != null) ? digitImages[0].getHeight(this) : 0;
        int y = (getHeight() - digitHeight) / 2;  // 垂直方向は中央に配置

        // 全体の表示が変化中なら change_digitImages を使用、それ以外は通常画像を使用
        for (int i = 0; i < scoreStr.length(); i++) {
            char c = scoreStr.charAt(i);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                Image digitImage = scoreChanged ? change_digitImages[digit] : digitImages[digit];
                if (digitImage != null) {
                    g.drawImage(digitImage, x, y, digitWidth, digitHeight, this);
                }
                x += digitWidth + padding;
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // 最新のスコアを取得し8桁文字列に変換
        int score = gameStatus.getGameScore();
        String newScoreStr = String.format("%08d", score);

        // 前回のスコアと比較して変化があれば、全体表示を変更する
        if (!newScoreStr.equals(previousScoreStr)) {
            scoreChanged = true;
            // CHANGE_DURATION 経過後に元の画像に戻す Timer を起動
            Timer timer = new Timer(CHANGE_DURATION, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    scoreChanged = false;
                    repaint();
                }
            });
            timer.setRepeats(false);
            timer.start();
            previousScoreStr = newScoreStr;
        }
        repaint();
    }
}
