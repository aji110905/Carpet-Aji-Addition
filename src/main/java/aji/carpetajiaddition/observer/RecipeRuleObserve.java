package aji.carpetajiaddition.observer;

import aji.carpetajiaddition.recipe.RecipeManager;
import carpet.api.settings.CarpetRule;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public class RecipeRuleObserve extends RuleObserve<Boolean> {

    @Override
    public void onRuleValueChanged(@Nullable CommandSourceStack source, CarpetRule<Boolean> changingRule, Boolean newValue, String userInput) {
        if (source != null) {
            RecipeManager.onRuleValueChanged(source.getServer());
        }
    }
}
