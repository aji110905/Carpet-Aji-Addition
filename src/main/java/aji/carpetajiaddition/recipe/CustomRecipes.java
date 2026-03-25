package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.settings.RuleCategory;
import aji.carpetajiaddition.util.MinecraftServerUtil;
import aji.carpetajiaddition.recipe.builder.ShapedRecipeBuilder;
import carpet.api.settings.Rule;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.world.item.Items.*;

public class CustomRecipes {
    public static void buildRecipes(){
        ShapedRecipeBuilder.create(CarpetAjiAdditionSettings.dragonEggRecipe, "dragon_egg")
                .pattern("&#&")
                .pattern("^*^")
                .pattern("$$$")
                .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                .output(DRAGON_EGG, 1).build();

        ShapedRecipeBuilder.create(CarpetAjiAdditionSettings.dragonBreathRecipe, "dragon_breath")
                .pattern("#")
                .pattern("*")
                .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                .output(DRAGON_BREATH, 1).build();
    }

    public static void onRuleValueChanged(){
        if (MinecraftServerUtil.serverIsRunning()) {
            RecipeManager.clearRecipeListMemory();
            CustomRecipes.buildRecipes();
            CarpetAjiAdditionSettings.minecraftServer.execute(() -> {
                needReloadServerResources();
                for (RecipeHolder<?> recipe : CarpetAjiAdditionSettings.minecraftServer.getRecipeManager().getRecipes()) {
                    if (recipe.id().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID)) {
                        for (ServerPlayer player : CarpetAjiAdditionSettings.minecraftServer.getPlayerList().getPlayers()) {
                            if (!player.getRecipeBook().contains(recipe.id())) {
                                player.awardRecipes(List.of(recipe));
                            }
                        }
                    }
                }
            });
        }
    }

    public static void onPlayerLoggedIn(ServerPlayer player){
        if (MinecraftServerUtil.serverIsRunning() && hasActiveRecipeRule()) {
            for (RecipeHolder<?> recipe : CarpetAjiAdditionSettings.minecraftServer.getRecipeManager().getRecipes()) {
                if (recipe.id().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                    player.awardRecipes(List.of(recipe));
                }
            }
        }
    }

    private static boolean hasActiveRecipeRule() {
        Field[] fields = CarpetAjiAdditionSettings.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Rule.class)) {
                try {
                    field.setAccessible(true);
                    if (Arrays.asList(field.getAnnotation(Rule.class).categories()).contains(RuleCategory.RECIPE) && field.getBoolean(null)) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    CarpetAjiAdditionSettings.LOGGER.error("Failed to get rule value", e);
                }
            }
        }
        return false;
    }

    public static void needReloadServerResources() {
        if (MinecraftServerUtil.serverIsRunning() && hasActiveRecipeRule()) {
            CarpetAjiAdditionSettings.minecraftServer.reloadResources(CarpetAjiAdditionSettings.minecraftServer.getPackRepository().getSelectedIds());
        }
    }
}
