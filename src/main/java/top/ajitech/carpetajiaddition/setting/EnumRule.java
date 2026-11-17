package top.ajitech.carpetajiaddition.setting;

import carpet.api.settings.InvalidRuleValueException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class EnumRule<T> extends Rule<T> {
    protected EnumRule(String name, Collection<String> categories, T defaultValue, boolean mustSetDefault) {
        super(name, categories, defaultValue, mustSetDefault);
    }

    protected abstract Collection<T> allowedValues();

    protected String valueToString(T value) {
        return String.valueOf(value);
    }

    @Override
    public final boolean strict() {
        return true;
    }

    @Override
    public final Collection<String> suggestions() {
        List<String> suggestions = new ArrayList<>();
        for (T allowed : allowedValues()) {
            suggestions.add(valueToString(allowed));
        }
        return suggestions;
    }

    @Override
    protected final T parse(String value) throws InvalidRuleValueException {
        for (T allowed : allowedValues()) {
            String canonical = valueToString(allowed);
            if (canonical.equals(value)) {
                return allowed;
            }
        }
        throw new InvalidRuleValueException();
    }
}
