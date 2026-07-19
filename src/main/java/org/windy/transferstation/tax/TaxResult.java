package org.windy.transferstation.tax;

import org.bukkit.inventory.ItemStack;

public class TaxResult {
    private final boolean allowed;
    private final ItemStack resultItem;
    private final int taxAmount;
    private final int remainingAmount;
    private final String denialReasonKey;

    public TaxResult(boolean allowed, ItemStack resultItem, int taxAmount, int remainingAmount, String denialReasonKey) {
        this.allowed = allowed;
        this.resultItem = resultItem;
        this.taxAmount = taxAmount;
        this.remainingAmount = remainingAmount;
        this.denialReasonKey = denialReasonKey;
    }

    public boolean isAllowed() { return allowed; }
    public ItemStack getResultItem() { return resultItem; }
    public int getTaxAmount() { return taxAmount; }
    public int getRemainingAmount() { return remainingAmount; }
    public String getDenialReasonKey() { return denialReasonKey; }
}