package aji.carpetajiaddition;

import aji.carpetajiaddition.command.FollowCommand;
import aji.carpetajiaddition.command.ModsCommand;
import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.recipe.RecipeManager;
import aji.carpetajiaddition.translate.TranslateManager;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;

public class CarpetAjiAdditionExtension implements CarpetExtension {
    public static final CarpetAjiAdditionExtension INSTANCE = new CarpetAjiAdditionExtension();

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionSettings.class);
    }

    public void onServerCreated(MinecraftServer server){
        CarpetAjiAdditionSettings.recipeManager = new RecipeManager(server);
        FollowCommand.init(server);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        CarpetAjiAdditionSettings.dataManager = new DataManager(server);
        CarpetAjiAdditionSettings.recipeManager.reloadResourcesIfRecipeRuleEnabled();
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
        ModsCommand.register(dispatcher, commandBuildContext);
    }

    public void onSave(MinecraftServer server) {
        CarpetAjiAdditionSettings.dataManager.saveData();
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        CarpetAjiAdditionSettings.recipeManager.onPlayerLoggedIn(player);
    }

    @Override
    public void onReload(MinecraftServer server) {
        CarpetAjiAdditionSettings.dataManager.loadData();
    }

    public void afterServerClose(MinecraftServer server) {
        CarpetAjiAdditionSettings.dataManager = null;
    }

    @Override
    public String version() {
        return CarpetAjiAdditionSettings.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        TranslateManager translateManager = TranslateManager.getInstance();
        translateManager.updateTranslations(lang);
        return translateManager.getFabricCarpetTranslationMap();
    }
}