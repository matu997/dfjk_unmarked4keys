package rhythmgame.frame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * タイトル/メニュー画面を示すサンプルフレーム。 難易度選択や曲選択を行い、「スタート」ボタンで LaneFrame を開く。
 */
public class TitleFrame extends JFrame implements KeyListener {

    private TitlePanel backgroundPanel;

    private int nowSongIndex = 0;

    public TitleFrame() {
        setTitle("Rhythm Game Title");
        setSize(1600, 1200);
        setLocationRelativeTo(null);

        //setExtendedState(JFrame.MAXIMIZED_BOTH); //最大化

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 背景画像を設定するパネル
        backgroundPanel = new TitlePanel("images/title_bg.png");
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

        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {

        // エンターキーで開始、上下キーで選択
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER -> {
                SelectionFrame selectionFrame = new SelectionFrame();
                selectionFrame.setVisible(true);
                dispose();
            }
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}

class TitlePanel extends JPanel {

    private final Image backgroundImage;

    public TitlePanel(String filePath) {
        this.backgroundImage = new ImageIcon(filePath).getImage();
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
