package aji.carpetajiaddition;

import aji.carpetajiaddition.constant.RuleCategory;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import net.minecraft.commands.CommandSourceStack;

public class CarpetAjiAdditionRuleObserve implements SettingsManager.RuleObserver{
    @Override
    public void ruleChanged(CommandSourceStack source, CarpetRule<?> changedRule, String userInput) {
        if (changedRule.categories().contains(RuleCategory.RECIPE)){
            CarpetAjiAdditionExtension.INSTANCE.getRecipeManager().onRecipeRuleValueChanged();
        }
    }
}
