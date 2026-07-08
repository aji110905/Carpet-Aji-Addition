package top.ajitech.carpetajiaddition;

import top.ajitech.carpetajiaddition.command.FollowCommand;
import top.ajitech.carpetajiaddition.command.ModsCommand;
import top.ajitech.carpetajiaddition.constant.ModConstants;
import top.ajitech.carpetajiaddition.data.DataManager;
import top.ajitech.carpetajiaddition.recipe.RecipeManager;
import top.ajitech.carpetajiaddition.translate.TranslateManager;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;

public class CarpetAjiAdditionExtension implements CarpetExtension {
    public static final CarpetAjiAdditionExtension INSTANCE = new CarpetAjiAdditionExtension();

    private final TranslateManager translateManager = new TranslateManager();

    private DataManager dataManager = null;
    private RecipeManager recipeManager = null;


    @Override
    public void onGameStarted() {
        SettingsManager settingsManager = CarpetServer.settingsManager;
        settingsManager.parseSettingsClass(CarpetAjiAdditionRules.class);
        settingsManager.registerRuleObserver(new CarpetAjiAdditionRuleObserve());
    }

    public void onServerCreated(MinecraftServer server){
        recipeManager = new RecipeManager(server);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        server.reloadResources(server.getPackRepository().getSelectedIds());
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        dataManager = new DataManager(server);
        FollowCommand.init(server);
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
        ModsCommand.register(dispatcher, commandBuildContext);
    }

    public void onSave(MinecraftServer server) {
        dataManager.saveData();
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        recipeManager.onPlayerLoggedIn(player);
    }

    @Override
    public void onReload(MinecraftServer server) {
        dataManager.loadData();
    }

    public void afterServerClose(MinecraftServer server) {
        dataManager = null;
        recipeManager = null;
    }

    @Override
    public String version() {
        return ModConstants.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        translateManager.updateTranslations(lang);
        return translateManager.getFabricCarpetTranslationMap();
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public TranslateManager getTranslateManager() {
        return translateManager;
    }
}