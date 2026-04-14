package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.recipe.builder.ShapedRecipeBuilder;
import aji.carpetajiaddition.recipe.template.ShapedRecipeTemplate;
import aji.carpetajiaddition.settings.RuleCategory;
import carpet.api.settings.Rule;
//#if MC < 12102
import com.google.gson.JsonElement;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
//#if MC >= 12102
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.world.item.crafting.Recipe;
//#endif

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.Items.*;

public class RecipeManager {
    private static final List<ShapedRecipeTemplate> shapedRecipeList = new ArrayList<>();

    public static void addShapedRecipe(String id, String[][] pattern, Map<Character, String> ingredients, String result, int count) {
        shapedRecipeList.add(new ShapedRecipeTemplate(ResourceLocation.fromNamespaceAndPath(CarpetAjiAdditionSettings.MOD_ID, id), pattern, ingredients, result, count));
    }

    //#if MC < 12102
    public static void registerRecipes(Map<ResourceLocation, JsonElement> recipeMap) {
        shapedRecipeList.forEach(recipe -> recipe.addToRecipeMap(recipeMap));
    //#else
    //$$ public static void registerRecipes(Map<ResourceLocation, Recipe<?>> recipeMap, HolderLookup.Provider provider) {
    //$$     shapedRecipeList.forEach(recipe -> recipe.addToRecipeMap(recipeMap, provider));
    //#endif
    }

    public static void clearRecipeListMemory() {
        shapedRecipeList.clear();
    }

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

    public static void onRuleValueChanged(MinecraftServer server){
        clearRecipeListMemory();
        buildRecipes();
        server.execute(() -> {
            needReloadServerResources(server);
            for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
                //#if MC < 12102
                if (recipe.id().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID)) {
                //#else
                //$$ if (recipe.id().location().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID)) {
                //#endif
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (!player.getRecipeBook().contains(recipe.id())) {
                            player.awardRecipes(List.of(recipe));
                        }
                    }
                }
            }
        });
    }

    public static void onPlayerLoggedIn(ServerPlayer player){
        //#if MC < 12109
        MinecraftServer server = player.getServer();
        if (hasActiveRecipeRule() && server != null) {
        //#else
        //$$ MinecraftServer server = player.level().getServer();
        //$$ if (hasActiveRecipeRule()) {
        //#endif
            for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipes()) {
                //#if MC < 12102
                if (recipe.id().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                //#else
                //$$ if (recipe.id().location().getNamespace().equals(CarpetAjiAdditionSettings.MOD_ID) && !player.getRecipeBook().contains(recipe.id())) {
                //#endif
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

    public static void needReloadServerResources(MinecraftServer server) {
        if (hasActiveRecipeRule()) {
            server.reloadResources(server.getPackRepository().getSelectedIds());
        }
    }
}
