package top.ajitech.carpetajiaddition;

import top.ajitech.carpetajiaddition.recipe.RecipeRuleObserve;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import top.ajitech.carpetajiaddition.translate.TranslateManager;

import java.util.Map;

public class CarpetAjiAdditionExtension implements CarpetExtension {
    public static final CarpetAjiAdditionExtension INSTANCE = new CarpetAjiAdditionExtension();

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionRules.class);
        CarpetServer.settingsManager.registerRuleObserver(new RecipeRuleObserve());
    }

    @Override
    public String version() {
        return CarpetAjiAdditionMod.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        TranslateManager.INSTANCE.updateTranslations(lang);
        return TranslateManager.INSTANCE.getFabricCarpetTranslationMap();
    }
}