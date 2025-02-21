package rhythmgame;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

import rhythmgame.controller.Controller;
import rhythmgame.model.NotesModel;
import rhythmgame.sound.GameAudioController;
import rhythmgame.view.*;

public class LaneFrame extends JFrame {

    private JLayeredPane layeredPane;        
    private BackgroundPanel backgroundPanel; // レイヤー0 (最奥)
    private JPanel lanePanel;                // レイヤー1 (中段)
    private EffectPanel effectPanel;         // レイヤー2 (最前面,エフェクト専用)

    private ArrayList<Image> backgroundImages;
    private Controller controller;
    private ArrayList<NotesModel> models;
    private ArrayList<ViewLane> views;
    private ViewUpdate viewUpdater;
    private ScorePanel scorePanel;
    private ComboPanel comboPanel;

    private JProgressBar progressBar;
    private JLabel loadingLabel;

    private String music_path;
    private String csvPath;

    // ★ ローディング用の画像切り替えラベル & タイマー
    private JLabel loadingImageLabel;
    private ImageIcon[] loadingIcons;
    private Timer loadingImageTimer;
    private int loadingIconIndex = 0;

    GameAudioController gameAudioController;
    GameStatus gameStatus;

    public LaneFrame(String title, String wavPath, String csvPath) {
        super();
        setTitle(String.format("Rhythm Game - %s", title));
        music_path = wavPath;
        this.csvPath = csvPath;

        gameAudioController = new GameAudioController(music_path, this);
        gameStatus = new GameStatus();

        setSize(1600, 1200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ローディング用UIを仮で貼る
        setLayout(null);
        //色変更
        //getContentPane().setBackground(new Color(215, 231, 231));
        loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 40));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setOpaque(true);
        loadingLabel.setBackground(new Color(0, 0, 0, 150));
        loadingLabel.setBounds(600, 500, 400, 50);
        //add(loadingLabel);

        progressBar = new JProgressBar(0, 520);
        //progressBar.setStringPainted(true);
        //色変更
        progressBar.setForeground(new Color(147, 146, 231));
        progressBar.setBounds(400, 550, 800, 50);
        add(progressBar);

        loadingImageLabel = new JLabel();
        loadingImageLabel.setBounds(300, 205, 400, 480); // 好みで配置
        add(loadingImageLabel);

        loadingIcons = new ImageIcon[4];
        for (int i = 0; i < 4; i++) {
            // 例: "images/loading_anim/loading_0.png" ～ "loading_3.png"
            String filePath = String.format("images/loading/mp_risa%d.png", i);
            loadingIcons[i] = new ImageIcon(new ImageIcon(filePath).getImage().getScaledInstance(150, 210, Image.SCALE_SMOOTH));
        }
        loadingImageLabel.setIcon(loadingIcons[0]);

        loadingImageTimer = new Timer(50, e -> {

            loadingIconIndex = (loadingIconIndex + 1) % 4;
            loadingImageLabel.setIcon(loadingIcons[loadingIconIndex]);
        
            // 進捗率を取得 (0.0 ~ 1.0)
            double progressRatio = progressBar.getValue() / (double) progressBar.getMaximum();
        
            // Y座標を計算 (例: 上から100px → 下限600pxまで動かす)
            int startX = 300;       // 初期位置
            int endX = 1100;         // 最終位置
            int newX = (int) (startX + (endX - startX) * progressRatio)+30;
        
            // 画像の位置を更新
            loadingImageLabel.setLocation(newX, loadingImageLabel.getY());
        });
        loadingImageTimer.start();

        // 画像ロードを別スレッドで
        new Thread(() -> {
            backgroundImages = loadBackgroundImagesWithProgress("background/" + title + "/", "bga_" + title + "_%05d.png", 359);
            //backgroundImages = loadBackgroundImagesWithProgress("background/light/", "bga_" + "test" + "_%05d.png", 359);
            SwingUtilities.invokeLater(this::initializeGameComponents);
        }).start();
    }

    private ArrayList<Image> loadBackgroundImagesWithProgress(String basePath, String format, int frameCount) {
        ArrayList<Image> images = new ArrayList<>();
        MediaTracker tracker = new MediaTracker(this);

        for (int i = 1; i <= frameCount; i++) {
            String filename = String.format(basePath + format, i);
            Image img = new ImageIcon(filename).getImage();
            tracker.addImage(img, i);
            images.add(img);

            final int progress = i;
            SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
        }

        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return images;
    }

    private void initializeGameComponents() {
        // ローディングUIを削除
        remove(loadingLabel);
        remove(progressBar);
        repaint();

        // --- (1) JLayeredPane をメインコンテントにする ---
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        // --- (2) 背景パネル (レイヤー0, 最奥) ---
        backgroundPanel = new BackgroundPanel(backgroundImages);
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(backgroundPanel, Integer.valueOf(0)); // レイヤー0

        // --- (3) レーンパネル (レイヤー1, 中段) ---
        lanePanel = new JPanel(null);
        lanePanel.setOpaque(false);
        lanePanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(lanePanel, Integer.valueOf(1));

        // --- (4) エフェクトパネル (レイヤー2, 最前面) ---
        effectPanel = new EffectPanel();
        effectPanel.setOpaque(false);
        effectPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(effectPanel, Integer.valueOf(2));

            // MemoryUsagePanelを作成して、画面上の好きな位置に配置
        //rhythmgame.MemoryUsagePanel memPanel = new rhythmgame.MemoryUsagePanel();
        //memPanel.setBounds(20, 20, 300, 30);  // 位置・サイズはお好みで調整
        //lanePanel.add(memPanel);

        // --- モデル・ビュー・コントローラ初期化 ---
        models = new ArrayList<>();
        views = new ArrayList<>();
        viewUpdater = new ViewUpdate(models);
        controller = new Controller(models, views, viewUpdater, gameAudioController, /*旧: backgroundPanel*/ null);

        // 4レーン生成
        for (int i = 0; i < 4; i++) {
            NotesModel model = new NotesModel(i, null, gameStatus);
            // ★ ViewLane のコンストラクタに (effectPanel) を渡すよう変更する
            ViewLane viewLane = new ViewLane(model, controller, viewUpdater, effectPanel);

            model.setView(viewLane);
            model.setController(controller);

            viewLane.setLaneImage(String.format("images/lane_bg_key%d.png", i));
            viewLane.setKeyImages(String.format("images/default%d.png", i),
                                  String.format("images/pressed%d.png", i));
            viewLane.setOpaque(false);
            viewLane.setFocusable(true);

            lanePanel.add(viewLane);
            views.add(viewLane);
            models.add(model);
        }

        // スコアパネル/コンボパネルはレーンパネルの上 or さらに上のレイヤーに置く？
        // 今回はレーンと同じレイヤー1上に配置してもOK
        // あるいは別パネル(UIパネル)をレイヤー3に追加してもよい
        scorePanel = new ScorePanel(gameStatus);
        scorePanel.setImage("images/score/scoreBoard.png");
        scorePanel.setOpaque(false);
        lanePanel.add(scorePanel);

        comboPanel = new ComboPanel(gameStatus);
        comboPanel.setBackgroundImage("images/combo/comboBoard.png");
        comboPanel.setOpaque(false);
        lanePanel.add(comboPanel);

        // 配置
        updateComponentPositions();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateComponentPositions();
            }
        });

        loadNotes(csvPath);

        // フォーカス要求
        for (ViewLane v : views) {
            v.requestFocusInWindow();
        }

        // BGMとアニメーション開始
        // 旧: backgroundPanel.startAnimation(); → 背景パネルだけアニメ起動
        backgroundPanel.startAnimation();
        controller.startGame();
    }

    private void updateComponentPositions() {
        int w = getWidth();
        int h = getHeight();

        layeredPane.setBounds(0, 0, w, h);
        backgroundPanel.setBounds(0, 0, w, h);
        lanePanel.setBounds(0, 0, w, h);
        effectPanel.setBounds(0, 0, w, h);

        int laneCount = 4;
        int laneWidth = Math.max(w / 1600 * 150, 120);
        int startX = (w - laneCount * laneWidth) / 2;

        for (int i = 0; i < views.size(); i++) {
            ViewLane vl = views.get(i);
            vl.setBounds(startX + i * laneWidth, 0, laneWidth, h);
            vl.updateJudgmentLine(h);
        }

        scorePanel.setBounds(w - 465, 0, 450, 150);
        comboPanel.setBounds(w - 250, 500, 450, 150);
    }

    private void loadNotes(String path) {
        String csvFile = path;
        String line;
        int measure = 0;
        int index = 0;
        int notesAmount = 0;
        int beats = -1;
        double bpm = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int timeAddNotes = 0;
            while ((line = br.readLine()) != null) {
                String[] valuesLine = line.split(",", -1);

                if (valuesLine.length < 5) {
                    continue;
                }
                if (valuesLine[4].startsWith("#")) {
                    continue;
                }
                if (!valuesLine[4].isEmpty()) {
                    beats = Integer.parseInt(valuesLine[4].split("/")[1]);
                }
                if (valuesLine.length > 5 && !valuesLine[5].isEmpty()) {
                    bpm = Double.parseDouble(valuesLine[5]);
                }
                if (valuesLine.length > 6 && !valuesLine[6].isEmpty()) {
                    timeAddNotes += Integer.parseInt(valuesLine[6]);
                }

                index = (index + 1) % beats;
                if (index == 0) measure++;

                for (int i = 0; i < models.size(); i++) {
                    if (valuesLine.length > i && valuesLine[i].equals("1")) {
                        models.get(i).addNote(timeAddNotes, 0);
                    } else if (valuesLine.length > i && valuesLine[i].equals("2")) {
                        models.get(i).addNote(timeAddNotes, 1);
                    } else if (valuesLine.length > i && valuesLine[i].equals("3")) {
                        models.get(i).addNote(timeAddNotes, 2);
                    }
                    notesAmount++;
                }
                timeAddNotes += getInterval(bpm, beats);
            }
            gameStatus.setMaxComboCount(notesAmount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getInterval(double bpm, int beats) {
        return (int) (60000.0 / bpm * (4.0 / beats));
    }

    public void endGame() {
        viewUpdater.stop();
        ResultFrame resultFrame = new ResultFrame(gameStatus.getGameScore(), gameStatus);
        backgroundImages.forEach(Image::flush);
        resultFrame.setVisible(true);
        dispose();
    }
}
