package org.windy.transferstation.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.windy.transferstation.TransferStation;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PendingStorage {
    private final TransferStation plugin;
    private final File file;
    private final ConcurrentHashMap<UUID, List<ItemStack>> storage = new ConcurrentHashMap<>();

    public PendingStorage(TransferStation plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "pending.yml");
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<ItemStack> items = (List<ItemStack>) yaml.getList(key);
                if (items != null) {
                    storage.put(uuid, new ArrayList<>(items));
                }
            } catch (Exception ignored) {}
        }
    }

    public void saveData() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        // 清理旧配置
        for (String key : yaml.getKeys(false)) {
            yaml.set(key, null);
        }
        storage.forEach((uuid, items) -> {
            if (items != null && !items.isEmpty()) {
                yaml.set(uuid.toString(), items);
            }
        });
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save pending.yml!");
        }
    }

    public void setPending(UUID uuid, List<ItemStack> items) {
        items.removeIf(i -> i == null || i.getType().isAir());
        if (items.isEmpty()) {
            storage.remove(uuid);
        } else {
            storage.put(uuid, items);
        }
    }

    public List<ItemStack> clearAndReturn(UUID uuid) {
        List<ItemStack> items = storage.remove(uuid);
        return items != null ? items : new ArrayList<>();
    }
}