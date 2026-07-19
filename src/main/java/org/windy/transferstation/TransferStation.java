package org.windy.transferstation;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.windy.transferstation.command.TransferCommand;
import org.windy.transferstation.config.PluginConfig;
import org.windy.transferstation.gui.TransferGuiListener;
import org.windy.transferstation.limit.DailyLimitManager;
import org.windy.transferstation.log.TransferLogger;
import org.windy.transferstation.mail.MailService;
import org.windy.transferstation.papi.TransferExpansion;
import org.windy.transferstation.storage.PendingStorage;
import org.windy.transferstation.tax.TaxCalculator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class TransferStation extends JavaPlugin {
    private static TransferStation instance;

    private PluginConfig pluginConfig;
    private TaxCalculator taxCalculator;
    private DailyLimitManager dailyLimitManager;
    private PendingStorage pendingStorage;
    private TransferLogger transferLogger;
    private MailService mailService;

    @Override
    public void onEnable() {
        instance = this;

        // 基础服务初始化
        this.pluginConfig = new PluginConfig(this);
        this.taxCalculator = new TaxCalculator(this);
        this.dailyLimitManager = new DailyLimitManager(this);
        this.pendingStorage = new PendingStorage(this);
        this.pendingStorage.loadData();
        this.transferLogger = new TransferLogger(this);

        // 延迟 1 tick 初始化 MailBox，确保 MailBox 数据库已就绪
        this.mailService = new MailService();
        getServer().getScheduler().runTaskLater(this, () -> {
            if (this.mailService.getPluginUser() == null) {
                this.mailService.initPluginUser();
            }
        }, 1L);

        getServer().getPluginManager().registerEvents(new TransferGuiListener(this), this);

        // Paper插件命令注册（不设顶层权限，由TransferCommand内部检查）
        TransferCommand transferCmd = new TransferCommand(this);
        registerCommand("transfer", new io.papermc.paper.command.brigadier.BasicCommand() {
            @Override
            public void execute(@NotNull io.papermc.paper.command.brigadier.CommandSourceStack stack, @NotNull String[] args) {
                transferCmd.onCommand(stack.getSender(), null, "transfer", args);
            }

            @Override
            public @NotNull Collection<String> suggest(@NotNull io.papermc.paper.command.brigadier.CommandSourceStack stack, @NotNull String[] args) {
                return transferCmd.onTabComplete(stack.getSender(), null, "transfer", args);
            }

            @Override
            public boolean canUse(@NotNull CommandSender sender) {
                return true; // 权限由TransferCommand内部检查
            }
        });

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TransferExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (pendingStorage != null) {
            pendingStorage.saveData();
        }
    }

    public void reload() {
        this.pluginConfig.reload();
        this.transferLogger.reload();
    }

    public static TransferStation getInstance() { return instance; }
    public PluginConfig getPluginConfig() { return pluginConfig; }
    public TaxCalculator getTaxCalculator() { return taxCalculator; }
    public DailyLimitManager getDailyLimitManager() { return dailyLimitManager; }
    public PendingStorage getPendingStorage() { return pendingStorage; }
    public TransferLogger getTransferLogger() { return transferLogger; }
    public MailService getMailService() { return mailService; }
}