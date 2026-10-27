package top.ajitech.carpetajiaddition.recipe;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

import java.util.*;

import static top.ajitech.carpetajiaddition.CarpetAjiAdditionMod.MOD_ID;

public class RecipeManager {
    private final MinecraftServer server;

    public RecipeManager(MinecraftServer server) {
        this.server = server;
    }

    public void registerRecipe(Map<ResourceLocation, JsonElement> map) {
        CarpetAjiAdditionRules.RECIPE_RULES.forEach(rule -> rule.addToRecipeMap(map));
    }

    public void onRecipeRuleValueChanged(){
        server.execute(() -> {
            server.reloadResources(server.getPackRepository().getSelectedIds());
            for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
                if (!recipe.id().getNamespace().equals(MOD_ID)) {
                    continue;
                }
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.getRecipeBook().contains(recipe.id())) {
                        player.awardRecipes(List.of(recipe));
                    }
                }
            }
        });
    }

    public void onPlayerLoggedIn(ServerPlayer player){
        for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
            if (recipe.id().getNamespace().equals(MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                player.awardRecipes(List.of(recipe));
            }
        }
    }
}
