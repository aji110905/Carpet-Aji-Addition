package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.constant.RuleCategory;
import carpet.api.settings.Rule;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.lang.reflect.Field;
import java.util.*;

import static net.minecraft.world.item.Items.*;

public class RecipeManager {
    private final MinecraftServer server;

    public RecipeManager(MinecraftServer server) {
        this.server = server;
    }

    public void registerRecipe(Map<ResourceLocation, JsonElement> map) {
        ShapedRecipe.builder(CarpetAjiAdditionSettings.dragonEggRecipe, "dragon_egg")
                .pattern("&#&")
                .pattern("^*^")
                .pattern("$$$")
                .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                .output(DRAGON_EGG, 1)
                .build().addToRecipeMap(map);
        ShapedRecipe.builder(CarpetAjiAdditionSettings.dragonBreathRecipe, "dragon_breath")
                .pattern("#")
                .pattern("*")
                .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                .output(DRAGON_BREATH, 1)
                .build().addToRecipeMap(map);
    }

    public void onRuleValueChanged(){
        server.execute(() -> {
            reloadResourcesIfRecipeRuleEnabled();
            for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
                if (!recipe.id().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID)) {
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
            if (recipe.id().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                player.awardRecipes(List.of(recipe));
            }
        }
    }

    public void reloadResourcesIfRecipeRuleEnabled(){
        Field[] fields = CarpetAjiAdditionSettings.class.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Rule.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                if (Arrays.asList(field.getAnnotation(Rule.class).categories()).contains(RuleCategory.RECIPE) && field.getBoolean(null)) {
                    server.reloadResources(server.getPackRepository().getSelectedIds());
                    return;
                }
            } catch (IllegalAccessException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Failed to get rule value", e);
            }
        }
    }
}
