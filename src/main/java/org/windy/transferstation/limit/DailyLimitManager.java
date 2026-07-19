package org.windy.transferstation.limit;

import org.bukkit.entity.Player;
import org.windy.transferstation.TransferStation;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DailyLimitManager {
    private final TransferStation plugin;

    public static class DailyRecord {
        public final LocalDate date;
        public int count;
        public DailyRecord(LocalDate date, int count) {
            this.date = date;
            this.count = count;
        }
    }

    private final ConcurrentHashMap<UUID, DailyRecord> records = new ConcurrentHashMap<>();

    public DailyLimitManager(TransferStation plugin) {
        this.plugin = plugin;
    }

    public int getLimit(Player player) {
        if (player.hasPermission("transfer.limit.unlimited")) {
            return Integer.MAX_VALUE;
        }
        for (int i = 100; i >= 1; i--) {
            if (player.hasPermission("transfer.limit." + i)) {
                return i;
            }
        }
        return plugin.getPluginConfig().defaultDailyLimit;
    }

    public DailyRecord getRecord(UUID uuid) {
        LocalDate today = LocalDate.now();
        return records.compute(uuid, (k, v) -> {
            if (v == null || !v.date.equals(today)) {
                return new DailyRecord(today, 0);
            }
            return v;
        });
    }

    public boolean tryUse(Player player) {
        UUID uuid = player.getUniqueId();
        int limit = getLimit(player);
        DailyRecord record = getRecord(uuid);

        synchronized (record) {
            if (record.count >= limit) {
                return false;
            }
            record.count++;
            return true;
        }
    }
}