package top.ajitech.carpetajiaddition.recipe;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionServer;
import top.ajitech.carpetajiaddition.setting.RuleCategory;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import net.minecraft.commands.CommandSourceStack;

public class RecipeRuleObserve implements SettingsManager.RuleObserver{
    @Override
    public void ruleChanged(CommandSourceStack source, CarpetRule<?> changedRule, String userInput) {
        if (changedRule.categories().contains(RuleCategory.RECIPE)){
            CarpetAjiAdditionServer.getInstance().getRecipeManager().onRecipeRuleValueChanged();
        }
    }
}
