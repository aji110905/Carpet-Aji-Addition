package top.ajitech.carpetajiaddition.setting;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.SettingsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Rule<T> implements CarpetRule<T> {
    private final String name;
    private final Collection<String> categories;
    private final T defaultValue;
    private final boolean mustSetDefault;
    private T value;

    protected Rule(String name, Collection<String> categories, T defaultValue, boolean mustSetDefault) {
        this.name = name;
        this.categories = new ArrayList<>(categories);
        this.categories.add(RuleCategory.CAA);
        this.defaultValue = defaultValue;
        this.mustSetDefault = mustSetDefault;
        value = defaultValue;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Component> extraInfo() {
        return List.of();
    }

    @Override
    public Collection<String> categories() {
        return categories;
    }

    @Override
    public SettingsManager settingsManager() {
        return CarpetServer.settingsManager;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public boolean canBeToggledClientSide() {
        return false;
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    public boolean mustSetDefault() {
        return mustSetDefault;
    }

    @Override
    public boolean strict() {
        return true;
    }

    @Override
    public void set(CommandSourceStack source, String value) throws InvalidRuleValueException {
        set(source, parse(value), value);
    }

    protected abstract T parse(String value) throws InvalidRuleValueException;

    @Override
    public void set(CommandSourceStack source, T value) throws InvalidRuleValueException {
        set(source, value, null);
    }

    private void set(CommandSourceStack source, T value, String userInput) throws InvalidRuleValueException {
        if (value == null) {
            throw new InvalidRuleValueException();
        }
        this.value = value;
        settingsManager().notifyRuleChanged(source, this, userInput);
        onValueChanged(source, value);
    }

    protected void onValueChanged(CommandSourceStack source, T value) {
    }

    @Override
    public String toString() {
        return name() + ": " + value();
    }
}
