package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.recipe.template.ShapedRecipeTemplate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeManager {
    private static final List<ShapedRecipeTemplate> shapedRecipeList = new ArrayList<>();

    public static void addShapedRecipe(String id, String[][] pattern, Map<Character, String> ingredients, String result, int count) {
        shapedRecipeList.add(new ShapedRecipeTemplate(ResourceLocation.fromNamespaceAndPath(CarpetAjiAdditionSettings.MOD_ID, id), pattern, ingredients, result, count));
    }

    public static void registerRecipes(Map<ResourceLocation, Recipe<?>> recipeMap, HolderLookup.Provider provider) {
        shapedRecipeList.forEach(recipe -> recipe.addToRecipeMap(recipeMap, provider));
    }

    public static void clearRecipeListMemory() {
        shapedRecipeList.clear();
    }
}
