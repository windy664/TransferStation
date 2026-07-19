package org.windy.transferstation.config;

/**
 * 税收不足时的处理方式
 */
public enum TaxInsufficientAction {
    /** 拒绝寄送 */
    REFUSE,
    /** 强制收1个 */
    ROUND_UP,
    /** 免税 */
    FLOOR
}
