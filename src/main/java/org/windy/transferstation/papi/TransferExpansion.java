package org.windy.transferstation.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.windy.transferstation.TransferStation;
import org.windy.transferstation.limit.DailyLimitManager;

public class TransferExpansion extends PlaceholderExpansion {
    private final TransferStation plugin;

    public TransferExpansion(TransferStation plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() { return "Windy"; }
    @Override
    public @NotNull String getIdentifier() { return "transfer"; }
    @Override
    public @NotNull String getVersion() { return "1.0.0"; }
    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline()) return "";
        Player onlinePlayer = player.getPlayer();
        DailyLimitManager limitManager = plugin.getDailyLimitManager();

        int limit = limitManager.getLimit(onlinePlayer);
        int used = limitManager.getRecord(player.getUniqueId()).count;

        if (params.equalsIgnoreCase("remaining")) {
            return limit == Integer.MAX_VALUE ? "∞" : String.valueOf(Math.max(0, limit - used));
        }
        if (params.equalsIgnoreCase("limit")) {
            return limit == Integer.MAX_VALUE ? "∞" : String.valueOf(limit);
        }
        if (params.equalsIgnoreCase("used")) {
            return String.valueOf(used);
        }
        if (params.toLowerCase().startsWith("tax_rate:")) {
            String matStr = params.substring(9).toUpperCase();
            Material material = Material.matchMaterial(matStr);
            if (material != null) {
                ItemStack dummy = new ItemStack(material);
                return String.valueOf(plugin.getTaxCalculator().getRate(dummy));
            }
            return "0";
        }
        return null;
    }
}
