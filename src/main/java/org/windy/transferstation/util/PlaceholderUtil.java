package org.windy.transferstation.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * PAPI变量处理工具
 */
public class PlaceholderUtil {

    private static boolean papiEnabled = false;

    static {
        papiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * 设置PAPI变量
     */
    public static String setPlaceholders(Player player, String text) {
        if (!papiEnabled || player == null) return text;

        try {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        } catch (Exception e) {
            return text;
        }
    }
}
