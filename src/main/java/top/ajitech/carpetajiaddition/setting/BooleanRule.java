package top.ajitech.carpetajiaddition.setting;

import java.util.Collection;
import java.util.List;

public class BooleanRule extends EnumRule<Boolean> {
    public BooleanRule(String name, Collection<String> categories, Boolean defaultValue, boolean mustSetDefault) {
        super(name, categories, defaultValue, mustSetDefault);
    }

    @Override
    protected Collection<Boolean> allowedValues() {
        return List.of(Boolean.TRUE, Boolean.FALSE);
    }

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }
}
