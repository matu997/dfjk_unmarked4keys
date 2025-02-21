package rhythmgame.frame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import rhythmgame.sound.SEPlayer;

/**
 * タイトル/メニュー画面を示すサンプルフレーム。 難易度選択や曲選択を行い、「スタート」ボタンで LaneFrame を開く。
 */
public class SelectionFrame extends JFrame implements KeyListener {

    private OpeningPanel backgroundPanel;

    // 曲情報。追加時は全て更新すること。
    private final String[] MUSIC_TITLES = {"nokishita", "tidal"};
    private final String[] CSV_PATHS = {"nokishita.csv", "tidal_test.csv"};
    private final String[] WAV_PATHS = {"musics/nokishita.wav", "musics/tidalWanderer_short (1).wav"};

    private final SEPlayer SE_SELECT = new SEPlayer("musics/se_selection.wav", 8);
    private final SEPlayer SE_DECIDE = new SEPlayer("musics/se_decision.wav", 1);

    private final int MUSIC_NUM = MUSIC_TITLES.length;
    private int nowSongIndex = 0;

    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<Image> musicInfoImages = new ArrayList<>();

    public SelectionFrame() {
        setTitle("Rhythm Game Title");
        setSize(1600, 1200);
        setLocationRelativeTo(null);

        //setExtendedState(JFrame.MAXIMIZED_BOTH); //最大化

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        musicInfoImages.add(new ImageIcon("images/musicinfo_nokishita.png").getImage());
        musicInfoImages.add(new ImageIcon("images/musicinfo_tidal.png").getImage());

        // 背景画像を設定するパネル
        backgroundPanel = new OpeningPanel("images/selection_bg.png", musicInfoImages);
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
                SE_DECIDE.startSound();
                LaneFrame gameFrame = new LaneFrame(MUSIC_TITLES[nowSongIndex], WAV_PATHS[nowSongIndex], CSV_PATHS[nowSongIndex]);
                gameFrame.setVisible(true);
                dispose();
            }
            case KeyEvent.VK_UP -> {
                SE_SELECT.startSound();
                backgroundPanel.nextImage();
                nowSongIndex = (nowSongIndex + 1) % MUSIC_NUM;
            }
            case KeyEvent.VK_DOWN -> {
                SE_SELECT.startSound();
                backgroundPanel.prevImage();
                nowSongIndex = (nowSongIndex - 1 + MUSIC_NUM) % MUSIC_NUM;
            }
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}

class OpeningPanel extends JPanel {

    private final Image backgroundImage;
    private final ArrayList<Image> musicInfoImages;
    private int currentImageIndex = 0;

    public void nextImage() {
        currentImageIndex = (currentImageIndex + 1) % musicInfoImages.size();
        repaint();
    }

    public void prevImage() {
        currentImageIndex = (currentImageIndex - 1 + musicInfoImages.size()) % musicInfoImages.size();
        repaint();
    }

    public void setImage(int index) {
        currentImageIndex = index;
        repaint();
    }

    public OpeningPanel(String filePath, ArrayList<Image> musicInfoImages) {
        this.backgroundImage = new ImageIcon(filePath).getImage();
        this.musicInfoImages = musicInfoImages;
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // 画像を中央に配置
        g.drawImage(musicInfoImages.get(currentImageIndex), getWidth() * 5 / 8, getHeight() / 2 - 200, 600, 400, this);

    }
}
