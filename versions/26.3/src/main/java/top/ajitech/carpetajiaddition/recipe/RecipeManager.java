package top.ajitech.carpetajiaddition.recipe;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.crafting.RecipeHolder;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRecipes;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

import java.util.List;
import java.util.Map;

import static top.ajitech.carpetajiaddition.CarpetAjiAdditionMod.MOD_ID;

public class RecipeManager {
    private final MinecraftServer server;

    public RecipeManager(MinecraftServer server) {
        this.server = server;
    }

    public void registerRecipe(Map<Identifier, Resource> map, FileToIdConverter converter, PackResources packResources) {
        CarpetAjiAdditionRecipes.INSTANCE.getRecipes().forEach(recipe -> recipe.addToRecipeMap(packResources, map, converter));
    }

    public void onRecipeRuleValueChanged(){
        if (!CarpetAjiAdditionRules.hasEnabledRecipeRule()) {
            return;
        }
        server.execute(() -> {
            server.reloadResources(server.getPackRepository().getSelectedIds());
            for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
                if (!recipe.id().identifier().getNamespace().equals(MOD_ID)) {
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
            if (recipe.id().identifier().getNamespace().equals(MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                player.awardRecipes(List.of(recipe));
            }
        }
    }
}
