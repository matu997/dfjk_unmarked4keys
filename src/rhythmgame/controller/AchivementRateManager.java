package rhythmgame.controller;

public class AchivementRateManager {
    // ノルマ達成率を管理するクラス
    private double achievementRate; // ノルマ達成率
    private int maxComboCount; // 最大コンボ数

    private float coefficient_Perfect; // パーフェクト時の係数
    private float coefficient_Great; // グレイト時の係数
    private float coefficient_Good; // グッド時の係数
    private float coefficient_Bad; // バッド時の係数
    private float coefficient_Miss; // ミス時の係数


    // コンストラクタ
    public AchivementRateManager(int maxCombo) {
        this.maxComboCount = maxCombo;
        achievementRate = 0.0;
        setCoefficient(1.0f, 0.8f, 0.0f, -0.5f, -2.0f); // デフォルトの係数を設定
    }

    // ノルマ達成率取得メソッド
    public double getAchievementRate() {
        return achievementRate;
    }

    // ノルマ達成率設定メソッド
    public void setAchievementRate(double rate) {
        achievementRate = rate;
    }

    // ノルマ達成率計算用係数設定メソッド
    public void setCoefficient_Perfect(float coefficient) {
        coefficient_Perfect = coefficient;
    }

    public void setCoefficient_Great(float coefficient) {
        coefficient_Great = coefficient;
    }

    public void setCoefficient_Good(float coefficient) {
        coefficient_Good = coefficient;
    }

    public void setCoefficient_Bad(float coefficient) {
        coefficient_Bad = coefficient;
    }

    public void setCoefficient_Miss(float coefficient) {
        coefficient_Miss = coefficient;
    }

    public void setCoefficient(float perfect, float great, float good, float bad, float miss) {
        setCoefficient_Perfect(perfect);
        setCoefficient_Great(great);
        setCoefficient_Good(good);
        setCoefficient_Bad(bad);
        setCoefficient_Miss(miss);
    }


    // ノルマ達成率計算下請けメソッド
    private void calculateAchievementRate(float coefficient) {
        achievementRate += (2.0 / maxComboCount) * coefficient;
    }

    // ノルマ達成率更新
    public void updateAchievementRate(String judgement) {
        // 判定に応じてノルマ達成率を更新
        switch (judgement) {
            case "perfect" -> calculateAchievementRate(coefficient_Perfect);
            case "great" -> calculateAchievementRate(coefficient_Great);
            case "good" -> calculateAchievementRate(coefficient_Good);
            case "bad" -> calculateAchievementRate(coefficient_Bad);
            case "miss" -> calculateAchievementRate(coefficient_Miss);
            default -> {
            }
        }

        // 正規化（ノルマ達成率が0%未満または100%を超えないようにする）
        if (achievementRate < 0.0) {
            achievementRate = 0.0;
        } else if (achievementRate > 1.0) {
            achievementRate = 1.0;
        }
    }

    // ノルマ達成率リセットメソッド
    public void resetAchievementRate() {
        achievementRate = 0.0;
    }
}