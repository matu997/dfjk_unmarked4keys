package rhythmgame.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import rhythmgame.model.NotesModel;

/**
 * 各レーンのノート更新を定期的に行い、Viewの再描画を促すためのクラス。 Observer/Observableを使ってViewLaneへ通知している。
 */
@SuppressWarnings("deprecation")
public class ViewUpdate extends Observable implements ActionListener {

    private ArrayList<NotesModel> models;
    private Timer timer;

    public ViewUpdate(ArrayList<NotesModel> models) {
        this.models = models;
        // n fps に設定するなら、1000/n で設定する
        timer = new Timer(1000 / 120, this);
    }

    /**
     * ゲーム開始などのタイミングで呼び出す
     */
    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    /**
     * 定期更新
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        double currentTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(models.size());
        for (NotesModel m : models) {
            executor.submit(() -> m.noteUpdate(currentTime));
        }
        executor.shutdown();
        setChanged();
        notifyObservers();
    }
}
