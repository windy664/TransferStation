package org.windy.transferstation.log;

import org.windy.transferstation.TransferStation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransferLogger {
    private final TransferStation plugin;
    private File logFile;
    private final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TransferLogger(TransferStation plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (plugin.getPluginConfig().logEnabled) {
            this.logFile = new File(plugin.getDataFolder(), plugin.getPluginConfig().logFileName);
            if (!logFile.exists()) {
                try {
                    logFile.getParentFile().mkdirs();
                    logFile.createNewFile();
                } catch (IOException ignored) {}
            }
        }
    }

    public void log(String playerName, String uuid, String material, int amount, int tax, int remaining) {
        if (!plugin.getPluginConfig().logEnabled || logFile == null) return;

        String time = LocalDateTime.now().format(formatter);
        String logLine = String.format("[%s] %s(%s) | %s x%d | 税收: %d | 剩余: %d",
                time, playerName, uuid, material, amount, tax, remaining);

        logExecutor.submit(() -> {
            try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
                out.println(logLine);
            } catch (IOException ignored) {}
        });
    }
}