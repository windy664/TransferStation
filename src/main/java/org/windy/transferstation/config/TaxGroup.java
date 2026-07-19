package org.windy.transferstation.config;

import org.bukkit.Material;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 税率分组
 */
public class TaxGroup {

    private final String name;
    private final List<Pattern> materialPatterns;
    private final double rate;

    public TaxGroup(String name, List<Pattern> materialPatterns, double rate) {
        this.name = name;
        this.materialPatterns = materialPatterns;
        this.rate = rate;
    }

    /**
     * 检查材质是否匹配此分组
     */
    public boolean matches(Material material) {
        String matName = material.name();
        for (Pattern pattern : materialPatterns) {
            if (pattern.matcher(matName).matches()) {
                return true;
            }
        }
        return false;
    }

    public String getName() { return name; }
    public double getRate() { return rate; }
}
