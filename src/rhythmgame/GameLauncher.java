package rhythmgame;

import javax.swing.SwingUtilities;

import rhythmgame.frame.TitleFrame;

/**
 * アプリケーションのエントリポイント。 タイトル画面（やメニュー画面）からゲーム画面を起動したい場合にここで制御を行う。
 */
public class GameLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //タイトル画面用のフレームを開く
            TitleFrame titleFrame = new TitleFrame();
            titleFrame.setVisible(true);
        });
    }
}
