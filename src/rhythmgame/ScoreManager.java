package rhythmgame;

public class ScoreManager {
    // ゲームのスコアを管理・計算するクラス

    private final int MaxScore = 10000000; // 最大スコア
    private int gameScore; // ゲームに依存するスコア
    private int maxCombo = 100; // 最大コンボ数

    // すべてのノーツをパーフェクトで取ったときのスコアがMaxScoreとなるように各スコア単位を設定
    private int score_perfect;
    private int score_great;
    private int score_good;
    private int score_bad;

    // コンストラクタ
    public ScoreManager(int maxCombo) {
        this.maxCombo = maxCombo;
        if (this.maxCombo <= 0) {
            // 最大コンボ数が0以下の場合は100に設定
            System.out.println("最大コンボ数が0以下です。100に設定します。");
            this.maxCombo = 100;
        }
        updateScoreRate(maxCombo);
        gameScore = 0;
        System.out.println("perfect:" + score_perfect + " great:" + score_great + " good:" + score_good + " bad:" + score_bad);
    }

    /////////////////////////////////////////////////
    /// スコア関連メソッド

    // スコア取得メソッド
    public int getGameScore() {
        return gameScore;
    }

    // スコア設定メソッド
    public void setGameScore(int score) {
        gameScore = score;
    }

    // スコアレート更新用の内部メソッド
    private void updateScoreRate(int maxCombo) {
        score_perfect = (int) (MaxScore / maxCombo);
        score_great = (int) (score_perfect / 2);
        score_good = (int) (score_perfect / 4);
    }

    // スコアレート更新用の外部メソッド（直接値を指定）
    public void updateScoreRate_direct(int perfect, int great, int good, int bad) {
        score_perfect = perfect;
        score_great = great;
        score_good = good;
        score_bad = bad;
        System.out.println("[Warning]スコアレートが直接設定されました。");
        if (score_bad != 0) {
            System.out.println("badスコアが0以外です。:[BadScore]" + score_bad);
        }
    }

    /////////////////////////////////////////////////

    // 最大コンボ数設定メソッド
    public void setMaxComboCount(int maxCombo) {
        this.maxCombo = maxCombo;
        updateScoreRate(maxCombo); // 最大コンボ数変更に合わせてスコアレートを更新
    }

    // 判定別にスコアを加算するメソッド
    public void addScore(String judgement) {
        switch (judgement) {
            case "perfect" ->
                gameScore += score_perfect;
            case "great" ->
                gameScore += score_great;
            case "good" ->
                gameScore += score_good;
            case "bad" ->
                gameScore += score_bad;
            case "miss" ->
                gameScore += score_bad;
            default -> {
            }
        }

        // 正規化（スコアが0未満または最大スコアを超えないようにする）
        if (gameScore < 0) {
            gameScore = 0;
        } else if (gameScore > MaxScore) {
            gameScore = MaxScore;
        }
    }

    // スコアリセットメソッド
    public void resetGameScore() {
        gameScore = 0;
    }

}
