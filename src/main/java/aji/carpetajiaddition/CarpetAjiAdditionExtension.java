package aji.carpetajiaddition;

import aji.carpetajiaddition.commands.FollowCommand;
import aji.carpetajiaddition.commands.ModsCommand;
import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.recipe.RecipeManager;
import aji.carpetajiaddition.util.translations.TranslationUtil;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import java.util.Map;

public class CarpetAjiAdditionExtension implements CarpetExtension {
    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionSettings.class);
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        CarpetAjiAdditionSettings.minecraftServer = server;
        CarpetAjiAdditionSettings.data = new DataManager(CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.ROOT).getParent().resolve("data/" + CarpetAjiAdditionSettings.MOD_ID + ".dat"));
        FollowCommand.init();
        RecipeManager.needReloadServerResources();
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
        ModsCommand.register(dispatcher, commandBuildContext);
    }

    public void onSave() {
        CarpetAjiAdditionSettings.data.saveData();
    }

    public void onReload() {
        CarpetAjiAdditionSettings.data.loadData();
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        RecipeManager.onPlayerLoggedIn(player);
    }

    public void afterServerClose() {
        CarpetAjiAdditionSettings.minecraftServer = null;
        CarpetAjiAdditionSettings.data = null;
    }

    @Override
    public String version() {
        return CarpetAjiAdditionSettings.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return TranslationUtil.getFabricCarpetTranslations(lang);
    }
}