package rhythmgame.frame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * メモリ使用量を定期的に取得し、画面表示とCSV出力を行うパネル
 */
public class MemoryUsagePanel extends JPanel {
    private final JLabel memoryUsageLabel;
    private final Timer timer;
    private PrintWriter csvWriter;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ロガーの設定（任意）
    private static final Logger logger = Logger.getLogger(MemoryUsagePanel.class.getName());
    static {
        logger.setLevel(Level.INFO);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        logger.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler("memory_usage.log", true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.severe("FileHandlerの初期化に失敗しました: " + e.getMessage());
        }
    }

    public MemoryUsagePanel() {
        // パネル設定
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        memoryUsageLabel = new JLabel();
        memoryUsageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        memoryUsageLabel.setForeground(Color.WHITE);
        add(memoryUsageLabel);

        // CSVファイルの初期化（"memory_usage.csv" に出力。既存ファイルがなければヘッダーを書き出す）
        try {
            File csvFile = new File("memory_usage.csv");
            boolean fileExists = csvFile.exists();
            csvWriter = new PrintWriter(new FileWriter(csvFile, true));
            if (!fileExists) {
                csvWriter.println("Timestamp,UsedMemory(MB),TotalMemory(MB)");
                csvWriter.flush();
            }
        } catch (IOException e) {
            logger.severe("CSVファイルの初期化に失敗しました: " + e.getMessage());
        }

        // 1秒ごとにメモリ使用量を更新
        timer = new Timer(1000, e -> updateMemoryUsage());
        timer.start();
    }

    private void updateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        double usedMB = usedMemory / 1024.0 / 1024.0;
        double totalMB = totalMemory / 1024.0 / 1024.0;

        String text = String.format("Memory: %.2f MB / %.2f MB", usedMB, totalMB);
        memoryUsageLabel.setText(text);

        // ログ出力（任意）
        logger.info(text);

        // CSVに出力
        if (csvWriter != null) {
            String timestamp = dateFormat.format(new Date());
            csvWriter.printf("%s,%.2f,%.2f%n", timestamp, usedMB, totalMB);
            csvWriter.flush();
        }
    }

    /**
     * 監視を停止し、リソースを解放します。
     */
    public void stopMonitoring() {
        if (timer.isRunning()) {
            timer.stop();
        }
        if (csvWriter != null) {
            csvWriter.close();
        }
    }
}
