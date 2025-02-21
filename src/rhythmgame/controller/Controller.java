package rhythmgame.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import rhythmgame.model.NotesModel;
import rhythmgame.sound.*;
import rhythmgame.view.*;

/**
 * キー入力に応じてモデルやビューを更新するコントローラ。 スペースキーでゲーム開始、d/f/j/k でノート判定などを行う。
 */
public class Controller implements KeyListener {

    private final ArrayList<NotesModel> models;
    private final ArrayList<ViewLane> views;
    private final ViewUpdate viewUpdater;
    protected int dragStartX, dragStartY;
    protected BackgroundPanel backgroundPanel;

    private final GameAudioController gameAudioController;

    // コンストラクタでモデルとビューのリストを受け取る
    public Controller(ArrayList<NotesModel> models, ArrayList<ViewLane> views, ViewUpdate vu, GameAudioController gameAudioController, BackgroundPanel backgroundPanel) {
        this.models = models;
        this.views = views; // views を初期化
        this.viewUpdater = vu;

        // SE・BGMの初期化
        this.gameAudioController = gameAudioController;

        this.backgroundPanel = backgroundPanel;
    }

    public void startGame() {
        double currentTime = System.currentTimeMillis();
        for (NotesModel m : models) {
            m.gameStart(currentTime);
        }
        viewUpdater.start();
        gameAudioController.startMusic();
        backgroundPanel.startAnimation();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // スペースキーで開始
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        }

        // 押下されたキーに応じてViewを更新
        char c = e.getKeyChar();
        switch (c) {
            case 'd' ->
                views.get(0).setKeyPressed(true);
            case 'f' ->
                views.get(1).setKeyPressed(true);
            case 'j' ->
                views.get(2).setKeyPressed(true);
            case 'k' ->
                views.get(3).setKeyPressed(true);
            default -> {
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        switch (c) {
            case 'd' -> {
                models.get(0).tapJudge();
                views.get(0).startEffect();
            }
            case 'f' -> {
                models.get(1).tapJudge();
                views.get(1).startEffect();
            }
            case 'j' -> {
                models.get(2).tapJudge();
                views.get(2).startEffect();
            }
            case 'k' -> {
                models.get(3).tapJudge();
                views.get(3).startEffect();
            }
            default -> {
            }
        }
    }

    public void playTapSE() {
        gameAudioController.startTapNoteSound();
    }

    public void playCriticalSE() {
        gameAudioController.startCriticalNoteSound();
    }

    public void playMissSE() {
        gameAudioController.startMissSound();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        switch (c) {
            case 'd' ->
                views.get(0).setKeyPressed(false);
            case 'f' ->
                views.get(1).setKeyPressed(false);
            case 'j' ->
                views.get(2).setKeyPressed(false);
            case 'k' ->
                views.get(3).setKeyPressed(false);
            default -> {
            }
        }
    }
}
