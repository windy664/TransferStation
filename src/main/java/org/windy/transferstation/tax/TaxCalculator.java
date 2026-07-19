package org.windy.transferstation.tax;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.windy.transferstation.TransferStation;
import java.util.ArrayList;
import java.util.List;

public class TaxCalculator {
    private final TransferStation plugin;
    private final List<TaxGroup> groups = new ArrayList<>();

    public TaxCalculator(TransferStation plugin) {
        this.plugin = plugin;
        loadGroups();
    }

    public void loadGroups() {
        groups.clear();
        ConfigurationSection section = plugin.getPluginConfig().getConfig().getConfigurationSection("tax.groups");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            List<String> mats = section.getStringList(key + ".materials");
            double rate = section.getDouble(key + ".rate");
            groups.add(new TaxGroup(key, mats, rate));
        }
    }

    public double getRate(ItemStack item) {
        String matName = item.getType().name();
        for (TaxGroup group : groups) {
            if (group.matches(matName)) return group.getRate();
        }
        return plugin.getPluginConfig().defaultTaxRate;
    }

    public TaxResult calculate(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return new TaxResult(false, null, 0, 0, "invalid-item");
        }
        int amount = item.getAmount();
        double rate = getRate(item);
        int tax = (int) Math.floor(amount * rate / 100.0);

        TaxInsufficientAction action;
        try {
            action = TaxInsufficientAction.valueOf(plugin.getPluginConfig().taxInsufficientAction.toUpperCase());
        } catch (IllegalArgumentException e) {
            action = TaxInsufficientAction.REFUSE;
        }

        if (tax == 0 && rate > 0) {
            if (action == TaxInsufficientAction.REFUSE && amount < 2) {
                return new TaxResult(false, null, 0, 0, "tax-refuse");
            } else if (action == TaxInsufficientAction.ROUND_UP) {
                tax = 1;
            } else if (action == TaxInsufficientAction.FLOOR) {
                tax = 0;
            }
        }

        int remaining = amount - tax;
        if (remaining < 1) {
            return new TaxResult(false, null, 0, 0, "remaining-insufficient");
        }

        ItemStack cloned = item.clone();
        cloned.setAmount(remaining);
        return new TaxResult(true, cloned, tax, remaining, null);
    }
}