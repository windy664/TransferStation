package org.windy.transferstation.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.windy.transferstation.TransferStation;
import org.windy.transferstation.gui.TransferGuiListener;
import org.windy.transferstation.util.MessageUtil;
import java.util.Collections;
import java.util.List;

public class TransferCommand implements CommandExecutor, TabCompleter {
    private final TransferStation plugin;

    public TransferCommand(TransferStation plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("transfer.admin")) {
                MessageUtil.sendMessage(sender, "no-permission");
                return true;
            }
            plugin.reload();
            MessageUtil.sendMessage(sender, "reload-success");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can open the Transfer Station GUI.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("transfer.use")) {
            MessageUtil.sendMessage(player, "no-permission");
            return true;
        }

        TransferGuiListener.openGui(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("transfer.admin")) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}