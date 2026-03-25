package aji.carpetajiaddition.settings.validators.observer;

import aji.carpetajiaddition.recipe.CustomRecipes;
import carpet.api.settings.CarpetRule;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

public class RecipeRuleObserve extends AbstractRuleObserve<Boolean> {

    @Override
    public void onRuleValueChanged(@Nullable CommandSourceStack source, CarpetRule<Boolean> changingRule, Boolean newValue, String userInput) {
        CustomRecipes.onRuleValueChanged();
    }
}
