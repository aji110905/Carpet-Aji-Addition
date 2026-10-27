package top.ajitech.carpetajiaddition;

import carpet.CarpetExtension;
import top.ajitech.carpetajiaddition.translate.TranslateManager;

import java.util.Map;

public class CarpetAjiAdditionExtension implements CarpetExtension {
    public static final CarpetAjiAdditionExtension INSTANCE = new CarpetAjiAdditionExtension();

    @Override
    public void onGameStarted() {
        CarpetAjiAdditionRules.register();
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