package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.recipe.template.ShapedRecipeTemplate;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeManager {
    private static final List<ShapedRecipeTemplate> shapedRecipeList = new ArrayList<>();

    public static void addShapedRecipe(String id, String[][] pattern, Map<Character, String> ingredients, String result, int count) {
        shapedRecipeList.add(new ShapedRecipeTemplate(ResourceLocation.fromNamespaceAndPath(CarpetAjiAdditionSettings.MOD_ID, id), pattern, ingredients, result, count));
    }

    public static void registerRecipes(Map<ResourceLocation, JsonElement> recipeMap) {
        shapedRecipeList.forEach(recipe -> recipe.addToRecipeMap(recipeMap));
    }

    public static void clearRecipeListMemory() {
        shapedRecipeList.clear();
    }
}
