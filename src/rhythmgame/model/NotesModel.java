package rhythmgame.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Timer;

import rhythmgame.controller.*;
import rhythmgame.view.ViewLane;

/**
 * 1つのレーンに対応するモデル。 - ノート一覧の管理 - ノートの判定 - ノート座標の更新
 */
public class NotesModel implements ActionListener {

    private final ArrayList<Note> notes = new ArrayList<>();
    private Timer timer;
    private double beginTime;
    private ViewLane view;
    private Controller controller;
    private double noteSpeedFactor = 1.2;

    GameStatus gameStatus;

    public NotesModel(int laneIndex, ViewLane view, GameStatus gameStatus) {
        this.view = view; // 正しいViewLaneを渡す
        this.gameStatus = gameStatus;
    }

    public void setView(ViewLane view) {
        this.view = view;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    /**
     * ノートを追加
     *
     * @param time ミリ秒での到着予定時刻
     * @param type 0=TapNote, 1=TraceNote
     */
    public void addNote(double time, int type) {
        Note n;
        switch (type) {
            case 0 ->
                n = new TapNote();
            case 1 ->
                n = new CriticalNote();
            case 2 ->
                n = new TraceNote();
            default -> {
                return;
            }
        }
        n.setTime(time);
        notes.add(n);
    }

    /**
     * ゲームスタート時刻を設定し、タイマー起動
     */
    public void gameStart(double t) {
        beginTime = t;
        timer = new Timer(16, this);
        timer.start();
        gameStatus.setGameModeStart();
    }

    /**
     * ゲームストップ時、タイマー停止
     */
    public void gameStop() {
        if (timer != null) {
            timer.stop();
        }
        gameStatus.setGameModeStart();
    }

    /**
     * ノート位置の更新。currentTime - beginTime で経過時間を算出。
     *
     * @param currentTime 現在のシステム時刻(ms)
     */
    public void noteUpdate(double currentTime) {
        for (Note n : notes) {
            double nextOffset = n.time - (currentTime - beginTime);
            n.setOffset(nextOffset); // ノーツのoffsetを更新
            int judgmentLineY = view.getJudgmentLineY();
            int laneHeight = view.getHeight();

            // Y座標の計算
            double newY = judgmentLineY - laneHeight * noteSpeedFactor * nextOffset / 1000;
            n.setY(newY);
        }
    }

    /**
     * タップ時判定処理。 判定ウィンドウ(例: 50ms以内→Perfect, 120ms以内→Great, 300ms以内→Miss)など。
     */
    public void tapJudge() {
        Iterator<Note> itr = notes.iterator();
        while (itr.hasNext()) {
            Note n = itr.next();
            double diff = Math.abs(n.getOffset());
            if (diff <= 50) {
                itr.remove();
                System.out.println("Perfect!!!!!!");
                gameStatus.updateStatus("perfect");
                if (n instanceof TapNote) {
                    controller.playTapSE();
                    view.showJudgmentEffect("normal");
                } else {
                    controller.playCriticalSE();
                    view.showJudgmentEffect("critical");
                }
                return;
            } else if (diff <= 120) {
                itr.remove();
                System.out.println("Great!!!!");
                // Great を Good に変える場合
                gameStatus.updateStatus("good");
                if (n instanceof TapNote) {
                    controller.playTapSE();
                    view.showJudgmentEffect("normal");
                } else {
                    controller.playCriticalSE();
                    view.showJudgmentEffect("critical");
                }
                return;
            } else if (diff <= 300) {
                itr.remove();
                System.out.println("Miss....");
                gameStatus.updateStatus("miss");
                if (n instanceof TapNote) {
                    controller.playTapSE();
                } else {
                    controller.playCriticalSE();
                }
                // MISS 表示をするならここで用意（例: showJudgmentEffect("MISS")）
                return;
            }
        }
        // ノートがなかった or 判定Window外ならMiss
        controller.playMissSE();
    }

    /**
     * Timerのコールバック(16msごと)。 現在は未使用。描画タイミングはViewUpdateに任せている。
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // ここで定期的に処理してもOKだが、ViewUpdate側でノート更新しているため空にしている。
    }
}
