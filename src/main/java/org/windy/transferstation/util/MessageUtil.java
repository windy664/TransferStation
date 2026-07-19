package org.windy.transferstation.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.windy.transferstation.TransferStation;

public class MessageUtil {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * 将 MiniMessage 格式转换为 Component
     */
    public static Component deserialize(String message) {
        if (message == null) return Component.empty();
        // 将 & 颜色代码转换为 MiniMessage 格式
        String converted = convertLegacyToMiniMessage(message);
        return miniMessage.deserialize(converted);
    }

    /**
     * 将旧版 & 颜色代码转换为 MiniMessage 格式
     */
    public static String convertLegacyToMiniMessage(String message) {
        if (message == null) return "";
        return message
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
    }

    /**
     * 替换 PlaceholderAPI 占位符
     */
    public static String replacePlaceholders(Player player, String text) {
        if (text == null) return "";
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    /**
     * 发送配置消息给玩家
     */
    public static void sendMessage(CommandSender sender, String configKey) {
        String msg = TransferStation.getInstance().getPluginConfig().getMessage(configKey);
        sender.sendMessage(deserialize(msg));
    }

    /**
     * 发送原始消息给玩家
     */
    public static void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(deserialize(message));
    }
}
