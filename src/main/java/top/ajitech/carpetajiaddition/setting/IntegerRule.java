package top.ajitech.carpetajiaddition.setting;

import carpet.api.settings.InvalidRuleValueException;

import java.util.Collection;
import java.util.List;

public class IntegerRule extends Rule<Integer> {

    private final int miniValue;
    private final int maxValue;
    private final List<String> suggestions;

    public IntegerRule(
            String name,
            Collection<String> categories,
            Integer defaultValue,
            boolean mustSetDefault,
            int miniValue,
            int maxValue,
            Collection<String> suggestions
    ) {
        super(name, categories, defaultValue, mustSetDefault);
        this.miniValue = miniValue;
        this.maxValue = maxValue;
        this.suggestions = List.copyOf(suggestions);
    }

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    public Collection<String> suggestions() {
        return suggestions;
    }

    @Override
    protected Integer parse(String value) throws InvalidRuleValueException {
        int parsed;
        try {
            parsed = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidRuleValueException("'" + value + "' is not an integer");
        }
        if (parsed < miniValue || parsed > maxValue) {
            throw new InvalidRuleValueException("value must be in range [" + miniValue + ", " + maxValue + "]");
        }
        return parsed;
    }
}
