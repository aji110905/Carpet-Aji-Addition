package aji.carpetajiaddition;

import aji.carpetajiaddition.commands.FollowCommand;
import aji.carpetajiaddition.config.ConfigManager;
import aji.carpetajiaddition.config.NoticeConfig;
import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.settings.RecipeRule;
import aji.carpetajiaddition.settings.validators.RecipeRuleValidator;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import aji.carpetajiaddition.translations.getTranslationsMap;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.util.Map;

public class CarpetAjiAddition implements CarpetExtension {
    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionSettings.class);
        RecipeRule.addRecipeRulesToSettingManager();
        NoticeConfig.registerNoticeElements();
        CarpetAjiAdditionSettings.config = new ConfigManager(new File(FabricLoader.getInstance().getConfigDir().toFile(), CarpetAjiAdditionSettings.MOD_ID + ".json").toPath());
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        CarpetAjiAdditionSettings.data = new DataManager(server.getSavePath(WorldSavePath.ROOT).getParent().resolve("data/carpetajiaddition.dat.json"));
        FollowCommand.init();
        CarpetAjiAdditionSettings.minecraftServer.getDataPackManager().scanPacks();
        CarpetAjiAdditionSettings.minecraftServer.getDataPackManager().enable("file/CarpetAjiAdditionData");
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        RecipeRuleValidator.cleanDataPack();
        CarpetAjiAdditionSettings.minecraftServer.getScoreboard().removeTeam(CarpetAjiAdditionSettings.minecraftServer.getScoreboard().getTeam("followItems"));
        CarpetAjiAdditionSettings.minecraftServer = null;
        CarpetAjiAdditionSettings.data = null;
    }

    @Override
    public String version() {
        return CarpetAjiAdditionSettings.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        CarpetAjiAdditionTranslation.readLanguageFiles(lang);
        return getTranslationsMap.getFabricCarpetTranslations(lang);
    }

}