package top.ajitech.carpetajiaddition;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import top.ajitech.carpetajiaddition.data.DataManager;
import top.ajitech.carpetajiaddition.recipe.RecipeManager;

public class CarpetAjiAdditionServer {
    private static CarpetAjiAdditionServer instance = null;

    private final MinecraftServer server;

    private DataManager dataManager = null;
    private RecipeManager recipeManager = null;

    public CarpetAjiAdditionServer(MinecraftServer server) {
        this.server = server;
    }

    public void onServerCreated(){
        recipeManager = new RecipeManager(server);
    }

    public void onLoadedLevel() {
        dataManager = new DataManager(server);
    }

    public void onSave() {
        dataManager.saveData();
    }

    public void onPlayerLoggedIn(ServerPlayer player) {
        recipeManager.onPlayerLoggedIn(player);
    }

    public void onReload() {
        dataManager.loadData();
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public static CarpetAjiAdditionServer getInstance() {
        return instance;
    }

    public static void setInstance(CarpetAjiAdditionServer instance) {
        CarpetAjiAdditionServer.instance = instance;
    }
}
