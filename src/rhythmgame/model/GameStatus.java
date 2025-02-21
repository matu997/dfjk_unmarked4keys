package rhythmgame.model;

import java.util.Observable;
import rhythmgame.controller.ScoreManager;
import rhythmgame.controller.AchivementRateManager; // 追加

@SuppressWarnings("deprecation")
public class GameStatus extends Observable {
    // ゲームの状態、スコア、ノルマ達成率、SE、コンボ数などステータスに関わるものを一元管理するクラス

    /////////////////////////////////////////////////
    // スコア
    private ScoreManager scoreManager; // スコアを管理するクラス

    // コンボ数
    private int comboCount; // コンボ数
    private int maxComboCount = 100; // 最大コンボ数

    // 判定数
    private int[] judgmentCount = new int[5]; // 判定数
    // 0: perfect, 1: great, 2: good, 3: bad, 4: miss

    // ノルマ達成率
    private AchivementRateManager achivementRateManager; // ノルマ達成率を管理するクラス

    // ゲームの状態
    private int gameMode; // ゲームモード

    private final int GAME_MODE_TITLE = 0; // タイトル画面
    private final int GAME_MODE_PLAY = 1; // プレイ画面
    private final int GAME_MODE_RESULT = 2; // リザルト画面

    /////////////////////////////////////////////////



    // コンストラクタ
    public GameStatus() {
        scoreManager = new ScoreManager(maxComboCount); // スコアマネージャーの初期化
        achivementRateManager = new AchivementRateManager(maxComboCount); // ノルマ達成率マネージャーの初期化
        initValues(); // その他ステータスの初期化
    }

    // 初期化メソッド（コンストラクタ内で使用）
    private void initValues() {
        // 判定カウント数の初期化
        for (int i = 0; i < judgmentCount.length; i++) {
            judgmentCount[i] = 0;
        }
        comboCount = 0;
        gameMode = GAME_MODE_TITLE;
    }

    /////////////////////////////////////////////////
    /// getter

    // スコア取得
    public int getGameScore() {
        return scoreManager.getGameScore();
    }

    // コンボ数取得
    public int getComboCount() {
        return comboCount;
    }

    // 判定数取得
    public int[] getJudgmentCount() {
        return judgmentCount;
    }

    // ノルマ達成率取得
    public double getAchievementRate() {
        return achivementRateManager.getAchievementRate();
    }

    // ゲーム開始状態取得
    public boolean isGameStarted() {
        return gameMode == GAME_MODE_PLAY;
    }

    // ゲーム終了状態取得
    public boolean isGameEnded() {
        return gameMode == GAME_MODE_RESULT;
    }

    /////////////////////////////////////////////////
    //// setter

    // スコア設定
    public void setGameScore(int score) {
        scoreManager.setGameScore(score);
    }

    // コンボ数設定
    public void setComboCount(int combo) {
        comboCount = combo;
    }

    // 最大コンボ数設定
    public void setMaxComboCount(int maxCombo) {
        maxComboCount = maxCombo;
        scoreManager.setMaxComboCount(maxComboCount);
    }

    // 判定数設定
    public void setJudgmentCount(int[] count) {
        judgmentCount = count;
    }

    // ノルマ達成率設定
    public void setAchievementRate(double rate) {
        achivementRateManager.setAchievementRate(rate);
    }

    // ゲーム開始状態設定
    public void setGameModeStart() {
        gameMode = GAME_MODE_PLAY;
    }

    // ゲーム終了状態設定
    public void setGameModeEnd() {
        gameMode = GAME_MODE_RESULT;
    }

    /////////////////////////////////////////////////

    // スコア・達成率・判定履歴の確認（デバッグ用）
    private void printCurrentStatus() {
        // System.out.println("<Game Status Debug>");
        // System.out.println("Score:" + getGameScore());
        // System.out.println("Achievement:" + getAchievementRate());

        System.out.print("Judgement:");
        for (int i = 0; i < judgmentCount.length; i++) {
            System.out.print("[" + judgmentCount[i] + "]");
        }
        //コンボ数
        System.out.println("Combo:" + comboCount);
        System.out.println("");
        System.out.println("---------");
    }

    // ノーツ処理時のステータス更新
    public void updateStatus(String judgement) {
        if (gameMode != GAME_MODE_PLAY) {
            System.out.println("ゲーム開始状態でないので gamestatus が更新できません。");
            return;
        }

        // スコア更新
        scoreManager.addScore(judgement);
        // 達成率更新
        achivementRateManager.updateAchievementRate(judgement);
        // コンボ数・判定数更新
        switch (judgement) {
            case "perfect" -> {
                judgmentCount[0]++;
                comboCount++;
            }
            case "great" -> {
                judgmentCount[1]++;
                comboCount++;
            }
            case "good" -> {
                judgmentCount[2]++;
                comboCount++;
            }
            case "bad" -> {
                judgmentCount[3]++;
                comboCount = 0;
            }
            case "miss" -> {
                judgmentCount[4]++;
                comboCount = 0;
            }
            default -> {
            }
        }
        // debug
        printCurrentStatus();

        // ゲームステータスの更新をObserverオブジェクトに通知、update()を呼び出す
        setChanged();
        notifyObservers();
    }
}
