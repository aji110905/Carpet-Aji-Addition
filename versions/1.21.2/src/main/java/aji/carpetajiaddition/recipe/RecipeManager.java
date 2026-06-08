package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionRules;
import aji.carpetajiaddition.constant.ModConstants;
import aji.carpetajiaddition.constant.RuleCategory;
import carpet.api.settings.Rule;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

import static aji.carpetajiaddition.constant.ModConstants.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class RecipeManager {
    private final MinecraftServer server;

    public RecipeManager(MinecraftServer server) {
        this.server = server;
    }
    public void registerRecipe(SortedMap<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> map, HolderLookup.Provider provider) {
        if (CarpetAjiAdditionRules.dragonEggRecipe) {
            ShapedRecipe.builder("dragon_egg", DRAGON_EGG, 1)
                    .pattern("&#&")
                    .pattern("^*^")
                    .pattern("$$$")
                    .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                    .build().addToRecipeMap(map, provider);
        }
        if (CarpetAjiAdditionRules.dragonBreathRecipe) {
            ShapedRecipe.builder("dragon_breath", DRAGON_BREATH, 1)
                    .pattern("#")
                    .pattern("*")
                    .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                    .build().addToRecipeMap(map, provider);
        }
    }

    public void onRecipeRuleValueChanged(){
        server.execute(() -> {
            reloadResourcesIfRecipeRuleEnabled();
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
        for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
            if (recipe.id().location().getNamespace().equals(MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                player.awardRecipes(List.of(recipe));
            }
        }
    }

    public void reloadResourcesIfRecipeRuleEnabled(){
        Field[] fields = CarpetAjiAdditionRules.class.getDeclaredFields();
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
                ModConstants.LOGGER.error("Failed to get rule value", e);
            }
        }
    }
}
