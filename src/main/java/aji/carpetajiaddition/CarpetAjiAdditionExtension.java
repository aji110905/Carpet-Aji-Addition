package aji.carpetajiaddition;

import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslations;
import aji.carpetajiaddition.translations.getTranslationsMap;
import aji.carpetajiaddition.utils.DataPackUtil;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class CarpetAjiAdditionExtension implements CarpetExtension {

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionSettings.class);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        DataPackUtil.initializationDataPack();
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        server.getDataPackManager().scanPacks();
        server.reloadResources(server.getDataPackManager().getEnabledIds());
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        DataPackUtil.cleanDataPack();
    }

    @Override
    public String version() {
        return CarpetAjiAdditionMod.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        CarpetAjiAdditionTranslations.readLanguageFiles(lang);
        return getTranslationsMap.getFabricCarpetTranslations(lang);
    }

}