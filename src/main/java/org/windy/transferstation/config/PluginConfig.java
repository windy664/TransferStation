package org.windy.transferstation.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.windy.transferstation.TransferStation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig {
    private final TransferStation plugin;
    private FileConfiguration config;

    // 缓存常用项
    public String guiTitle;
    public int guiRows;

    public double defaultTaxRate;
    public String taxInsufficientAction;
    public int defaultDailyLimit;

    public String mailSender;
    public String mailTitleTemplate;
    public List<String> mailContentTemplate;
    public long mailExpireTime;

    public boolean logEnabled;
    public String logFileName;

    private final Map<String, String> messages = new HashMap<>();

    public PluginConfig(TransferStation plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.guiTitle = config.getString("gui.title", "物品中转站");
        this.guiRows = config.getInt("gui.rows", 6);

        this.defaultTaxRate = config.getDouble("tax.default-rate", 5.0);
        this.taxInsufficientAction = config.getString("tax.insufficient-action", "REFUSE");
        this.defaultDailyLimit = config.getInt("daily-limit.default", 100);

        this.mailSender = config.getString("mail.sender", "中转站系统");
        this.mailTitleTemplate = config.getString("mail.title-template", "中转物资: %item%");
        this.mailContentTemplate = config.getStringList("mail.content-template");
        this.mailExpireTime = config.getLong("mail.expire-time", 604800000L);

        this.logEnabled = config.getBoolean("log.enabled", true);
        this.logFileName = config.getString("log.file-name", "transfer.log");

        if (config.getConfigurationSection("messages") != null) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, config.getString("messages." + key));
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cUnknown message key: " + key);
    }

    public FileConfiguration getConfig() { return config; }
}
