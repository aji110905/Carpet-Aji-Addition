package aji.carpetajiaddition;

import aji.carpetajiaddition.commands.FollowCommand;
import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.setting.validators.RecipeRuleValidator;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import aji.carpetajiaddition.translations.getTranslationsMap;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.util.Map;

public class CarpetAjiAddition implements CarpetExtension {
    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionSettings.class);
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        CarpetAjiAdditionMod.data = new DataManager(server.getSavePath(WorldSavePath.ROOT));
        FollowCommand.init();
        CarpetAjiAdditionMod.minecraftServer.getDataPackManager().scanPacks();
        CarpetAjiAdditionMod.minecraftServer.getDataPackManager().enable("file/CarpetAjiAdditionData");
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        RecipeRuleValidator.cleanDataPack();
        CarpetAjiAdditionMod.minecraftServer.getScoreboard().removeTeam(CarpetAjiAdditionMod.minecraftServer.getScoreboard().getTeam("followItems"));
    }

    @Override
    public String version() {
        return CarpetAjiAdditionMod.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        CarpetAjiAdditionTranslation.readLanguageFiles(lang);
        return getTranslationsMap.getFabricCarpetTranslations(lang);
    }

}