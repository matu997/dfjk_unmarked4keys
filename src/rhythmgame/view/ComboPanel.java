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
public class ComboPanel extends JPanel implements Observer {

    private GameStatus gameStatus;
    private Image backgroundImage;          // 背景画像
    private Image[] digitImages;            // 通常時の数字画像
    private Image[] changeDigitImages;      // コンボ更新時の数字画像(エフェクト付きなど)
    private String previousComboStr;        // 前回表示したコンボ数(文字列)
    private boolean comboChanged;           // コンボ数が変化したかどうか
    private static final int CHANGE_DURATION = 100; // 変化画像を表示する時間(ミリ秒)

    public ComboPanel(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        // GameStatus のオブザーバーとして登録
        this.gameStatus.addObserver(this);

        // パネルの背景を透過に設定
        setOpaque(false);

        // 背景画像の初期値はnull(後から setBackgroundImage() で設定可能)
        backgroundImage = null;

        // 数字画像の配列を用意（ここでは0〜9の10枚）
        digitImages = new Image[10];
        changeDigitImages = new Image[10];

        // 通常数字画像を読み込み（例："images/combo/0.png", "images/combo/1.png", ...）
        for (int i = 0; i < 10; i++) {
            digitImages[i] = new ImageIcon("images/score/small_" + i + ".png").getImage();
        }
        // 変化時（点滅・エフェクト表示用）の数字画像を読み込み（例："images/combo/f0.png", ...）
        for (int i = 0; i < 10; i++) {
            changeDigitImages[i] = new ImageIcon("images/score/small_" + i + ".png").getImage();
        }

        // 初期コンボを取得して文字列化(ここでは4桁表示に例示)
        int combo = this.gameStatus.getComboCount();
        previousComboStr = String.format("%03d", combo);
        comboChanged = false;
    }

    /**
     * 背景画像を設定する(任意)
     * 例：comboPanel.setBackgroundImage("images/combo/comboBoard.png");
     */
    public void setBackgroundImage(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景画像を描画
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // GameStatus からコンボ数を取得し、4桁の文字列にフォーマット
        int combo = gameStatus.getComboCount();
        //System.out.println("combo: " + combo);
        String comboStr = String.format("%03d", combo);

        // 数字表示開始位置や間隔を適宜調整
        int x = 20;               // 左端からの描画開始位置
        int padding = 5;          // 数字同士の間隔
        int digitWidth = digitImages[0].getWidth(this);
        int digitHeight = digitImages[0].getHeight(this);
        int y = (getHeight() - digitHeight) / 2;  // 中央揃え

        // コンボ数が0のときに何も表示したくない場合は、ここで return しても良い
        if (combo == 0) return;

        // コンボが変更中（直近で変化）なら changeDigitImages を使い、そうでなければ digitImages を使う
        for (int i = 0; i < comboStr.length(); i++) {
            char c = comboStr.charAt(i);
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                Image digitImage = comboChanged ? changeDigitImages[digit] : digitImages[digit];
                g.drawImage(digitImage, x, y, digitWidth, digitHeight, this);
                x += digitWidth + padding;
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // 最新のコンボ数を文字列化
        int newCombo = gameStatus.getComboCount();
        String newComboStr = String.format("%03d", newCombo);

        // 前回の値と異なればコンボ更新を検知
        if (!newComboStr.equals(previousComboStr)) {
            comboChanged = true;  // コンボが変わったことを示す
            previousComboStr = newComboStr;

            // 一定時間後にフラグを元に戻す（変化エフェクトから通常表示へ切り替え）
            Timer timer = new Timer(CHANGE_DURATION, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    comboChanged = false;
                    repaint();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
        repaint();
    }
}