package top.ajitech.carpetajiaddition.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import top.ajitech.carpetajiaddition.setting.Rule;

import java.util.List;
import java.util.SortedMap;

import static top.ajitech.carpetajiaddition.CarpetAjiAdditionMod.MOD_ID;

public class RecipeManager {
    private final MinecraftServer server;

    public RecipeManager(MinecraftServer server) {
        this.server = server;
    }

    public void registerRecipe(SortedMap<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> map, HolderLookup.Provider provider) {
        CarpetAjiAdditionRules.RECIPE_RULES.forEach(rule -> rule.addToRecipeMap(map, provider));
    }

    public void onRecipeRuleValueChanged(){
        if (!hasEnabledRecipeRule()) {
            return;
        }
        server.execute(() -> {
            server.reloadResources(server.getPackRepository().getSelectedIds());
            for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
                if (!recipe.id().location().getNamespace().equals(MOD_ID)) {
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
        if (!hasEnabledRecipeRule()) {
            return;
        }
        for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
            if (recipe.id().location().getNamespace().equals(MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                player.awardRecipes(List.of(recipe));
            }
        }
    }

    private static boolean hasEnabledRecipeRule () {
        return CarpetAjiAdditionRules.RECIPE_RULES.stream().anyMatch(Rule::value);
    }
}
