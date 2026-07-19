package org.windy.transferstation.tax;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TaxGroup {
    private final String name;
    private final List<Pattern> materials = new ArrayList<>();
    private final double rate;

    public TaxGroup(String name, List<String> regexList, double rate) {
        this.name = name;
        this.rate = rate;
        for (String regex : regexList) {
            this.materials.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }
    }

    public boolean matches(String materialName) {
        for (Pattern p : materials) {
            if (p.matcher(materialName).matches()) {
                return true;
            }
        }
        return false;
    }

    public double getRate() { return rate; }
    public String getName() { return name; }
}