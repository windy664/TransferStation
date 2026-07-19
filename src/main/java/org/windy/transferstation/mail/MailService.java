package org.windy.transferstation.mail;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sabafly.mailbox.api.IMailBox;
import net.sabafly.mailbox.api.mail.PluginUser;
import net.sabafly.mailbox.api.mail.Template;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.windy.transferstation.TransferStation;
import org.windy.transferstation.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class MailService {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final IMailBox mailBox;
    private PluginUser pluginUser;

    public MailService() {
        this.mailBox = IMailBox.getInstance();
    }

    public void initPluginUser() {
        if (pluginUser != null) return;
        if (mailBox == null) return;

        TransferStation plugin = TransferStation.getInstance();
        try {
            this.pluginUser = mailBox.registerPluginUser(plugin, "transferstation");
        } catch (Exception e) {
            // 数据库已有记录（重启后重复注册），尝试获取已有用户
            try {
                this.pluginUser = mailBox.getPluginUser(plugin, "transferstation");
            } catch (Exception e2) {
                plugin.getLogger().warning("MailBox plugin user unavailable: " + e2.getMessage());
            }
        }
    }

    public PluginUser getPluginUser() {
        return pluginUser;
    }

    /**
     * 批量发送中转邮件——所有物品打包成一封邮件
     */
    public boolean sendBatchTransferMail(Player player, List<ItemStack> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        if (pluginUser == null) {
            initPluginUser();
            if (pluginUser == null) {
                TransferStation.getInstance().getLogger().warning("MailBox is not available!");
                return false;
            }
        }

        TransferStation plugin = TransferStation.getInstance();

        // 构建物品清单（带颜色码）
        StringBuilder itemList = new StringBuilder();
        int totalCount = 0;
        for (ItemStack item : items) {
            totalCount += item.getAmount();
            if (itemList.length() > 0) itemList.append("\n");
            itemList.append("&8  ● &7").append(getItemName(item)).append(" &8×&f").append(item.getAmount());
        }
        String firstName = items.size() == 1 ? getItemName(items.get(0)) : items.size() + "种物品";

        String title = MessageUtil.replacePlaceholders(player,
                plugin.getPluginConfig().mailTitleTemplate
                        .replace("%item%", firstName)
                        .replace("%amount%", String.valueOf(totalCount)));

        List<String> contentLines = new ArrayList<>();
        for (String line : plugin.getPluginConfig().mailContentTemplate) {
            String formatted = line
                    .replace("%items_list%", itemList.toString())
                    .replace("%item%", firstName)
                    .replace("%amount%", String.valueOf(totalCount));
            contentLines.add(MessageUtil.replacePlaceholders(player, formatted));
        }
        // 先拼接，再统一做 &→MiniMessage 颜色转换
        String content = MessageUtil.convertLegacyToMiniMessage(String.join("\n", contentLines));
        String convertedTitle = MessageUtil.convertLegacyToMiniMessage(title);

        try {
            net.sabafly.mailbox.api.mail.User targetUser = mailBox.getUser(player);

            // 一次性打包所有附件
            List<ItemStack> attachments = new ArrayList<>();
            for (ItemStack item : items) {
                attachments.add(item.clone());
            }

            Template template = pluginUser.createTemplate(builder -> {
                builder.subject(convertedTitle)
                       .content(content)
                       .sender(pluginUser)
                       .attachments(attachmentFactory -> {
                           List<net.sabafly.mailbox.api.mail.attachments.Attachment<?>> result = new ArrayList<>();
                           for (ItemStack attachItem : attachments) {
                               result.add(attachmentFactory.item(attachItem).build());
                           }
                           return result;
                       });
            });

            template.send(targetUser);
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send batch mail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getItemName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return miniMessage.serialize(item.getItemMeta().displayName());
        }
        return item.getType().name();
    }
}
