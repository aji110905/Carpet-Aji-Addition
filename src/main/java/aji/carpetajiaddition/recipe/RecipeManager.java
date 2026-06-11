package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionRules;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

import static aji.carpetajiaddition.constant.ModConstants.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class RecipeManager {
    private final MinecraftServer server;

    public RecipeManager(MinecraftServer server) {
        this.server = server;
    }

    public void registerRecipe(Map<ResourceLocation, JsonElement> map) {
        if (CarpetAjiAdditionRules.dragonEggRecipe) {
            ShapedRecipe.builder("dragon_egg", DRAGON_EGG, 1)
                    .pattern("&#&")
                    .pattern("^*^")
                    .pattern("$$$")
                    .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                    .build().addToRecipeMap(map);
        }
       if (CarpetAjiAdditionRules.dragonBreathRecipe) {
            ShapedRecipe.builder("dragon_breath", DRAGON_BREATH, 1)
                    .pattern("#")
                    .pattern("*")
                    .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                    .build().addToRecipeMap(map);
       }
    }

    public void onRecipeRuleValueChanged(){
        if (!CarpetAjiAdditionRules.hasEnabledRecipeRule()) {
            return;
        }
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
        if (!CarpetAjiAdditionRules.hasEnabledRecipeRule()){
            return;
        }
        for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
            if (recipe.id().getNamespace().equals(MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                player.awardRecipes(List.of(recipe));
            }
        }
    }
}
