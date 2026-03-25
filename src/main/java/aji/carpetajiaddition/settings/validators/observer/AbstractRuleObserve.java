package aji.carpetajiaddition.settings.validators.observer;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRuleObserve<T> extends Validator<T> {
    @Override
    public T validate(@Nullable CommandSourceStack source, CarpetRule<T> changingRule, T newValue, String userInput) {
        onRuleValueChanged(source, changingRule, newValue, userInput);
        return newValue;
    }

    public abstract void onRuleValueChanged(@Nullable CommandSourceStack source, CarpetRule<T> changingRule, T newValue, String userInput);
}
