package org.windy.transferstation.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.windy.transferstation.TransferStation;
import org.windy.transferstation.config.PluginConfig;
import org.windy.transferstation.tax.TaxResult;
import org.windy.transferstation.util.MessageUtil;
import java.util.ArrayList;
import java.util.List;

public class TransferGuiListener implements Listener {
    private final TransferStation plugin;
    private static final String GUI_MARK = "§9§r"; // 隐式标识标题防穿仓

    public TransferGuiListener(TransferStation plugin) {
        this.plugin = plugin;
    }

    public static void openGui(Player player) {
        TransferStation ts = TransferStation.getInstance();
        PluginConfig cfg = ts.getPluginConfig();

        String fullTitle = GUI_MARK + cfg.guiTitle;
        int slots = cfg.guiRows * 9;
        Inventory inv = Bukkit.createInventory(null, slots, fullTitle);

        // 载入暂存物资
        List<ItemStack> pending = ts.getPendingStorage().clearAndReturn(player.getUniqueId());
        for (int i = 0; i < Math.min(pending.size(), slots); i++) {
            inv.setItem(i, pending.get(i));
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(GUI_MARK)) return;
        // 允许玩家背包正常操作
        if (event.getRawSlot() >= event.getInventory().getSize()) return;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().startsWith(GUI_MARK)) return;
        // 禁止拖拽到GUI底部功能区（如果有的话）
        for (int slot : event.getRawSlots()) {
            if (slot >= event.getInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().startsWith(GUI_MARK)) return;

        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        int slots = plugin.getPluginConfig().guiRows * 9;

        // 收集物品
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && !item.getType().isAir()) {
                items.add(item);
            }
        }

        // 空仓关闭，不处理
        if (items.isEmpty()) {
            return;
        }

        // 检查每日额度
        if (!plugin.getDailyLimitManager().tryUse(player)) {
            MessageUtil.sendMessage(player, "limit-reached");
            plugin.getPendingStorage().setPending(player.getUniqueId(), items);
            return;
        }

        // 只有1个物品栈→免税
        List<ItemStack> postTaxItems = new ArrayList<>();
        if (items.size() == 1) {
            ItemStack single = items.get(0);
            postTaxItems.add(single.clone());
            plugin.getTransferLogger().log(player.getName(), player.getUniqueId().toString(),
                    single.getType().name(), single.getAmount(), 0, single.getAmount());
        } else {
            // 多物品→正常扣税
            for (ItemStack item : items) {
                TaxResult res = plugin.getTaxCalculator().calculate(item);
                if (!res.isAllowed()) {
                    String error = plugin.getPluginConfig().getMessage(res.getDenialReasonKey());
                    MessageUtil.sendRawMessage(player, error.replace("%item%", item.getType().name()));
                    continue;
                }
                postTaxItems.add(res.getResultItem());
                plugin.getTransferLogger().log(player.getName(), player.getUniqueId().toString(),
                        item.getType().name(), item.getAmount(), res.getTaxAmount(), res.getRemainingAmount());
            }
        }

        if (postTaxItems.isEmpty()) {
            return;
        }

        // 一次性打包发送
        boolean mailed = plugin.getMailService().sendBatchTransferMail(player, postTaxItems);
        if (mailed) {
            MessageUtil.sendMessage(player, "transfer-success");
        } else {
            // 发送失败，物品暂存
            plugin.getPendingStorage().setPending(player.getUniqueId(), items);
            MessageUtil.sendMessage(player, "mail-failed");
        }
    }
}
