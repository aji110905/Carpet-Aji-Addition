package top.ajitech.carpetajiaddition.setting;

import carpet.api.settings.InvalidRuleValueException;

import java.util.Collection;
import java.util.List;

public class BooleanRule extends Rule<Boolean> {
    public BooleanRule(String name, Collection<String> categories, Boolean defaultValue, boolean mustSetDefault) {
        super(name, categories, defaultValue, mustSetDefault);
    }

    @Override
    protected Boolean parse(String value) throws InvalidRuleValueException {
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return false;
        }
        throw new InvalidRuleValueException();
    }

    @Override
    public Collection<String> suggestions() {
        return List.of("true", "false");
    }

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }
}
