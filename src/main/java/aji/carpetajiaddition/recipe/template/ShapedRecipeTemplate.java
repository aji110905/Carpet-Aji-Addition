package aji.carpetajiaddition.recipe.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ShapedRecipeTemplate implements RecipeTemplateInterface{
    private static final String TYPE = "minecraft:crafting_shaped";

    private final ResourceLocation recipeId;
    private final String[][] pattern;
    private final Map<Character, String> ingredients;
    private final String resultItem;
    private final int resultCount;

    public ShapedRecipeTemplate(ResourceLocation recipeId, String[][] pattern, Map<Character, String> ingredients, String resultItem, int resultCount) {
        this.recipeId = recipeId;
        this.pattern = pattern;
        this.ingredients = ingredients;
        this.resultItem = resultItem;
        this.resultCount = resultCount;
    }

    @Override
    public void addToRecipeMap(Map<ResourceLocation, JsonElement> recipeMap) {
        JsonObject json = new JsonObject();
        json.add("type", new JsonPrimitive(TYPE));
        JsonArray jsonPattern = new JsonArray();
        for (String[] row : pattern) {
            StringBuilder rowBuilder = new StringBuilder();
            for (String cell : row) {
                rowBuilder.append(cell);
            }
            jsonPattern.add(rowBuilder.toString());
        }
        json.add("pattern", jsonPattern);
        JsonObject jsonKey = new JsonObject();
        //#if MC < 12102
        for (Map.Entry<Character, String> entry : ingredients.entrySet()) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("item", entry.getValue());
            jsonKey.add(entry.getKey().toString(), itemJson);
        }
        //#else
        //$$ for (Map.Entry<Character, String> entry : ingredients.entrySet()) {
        //$$     jsonKey.addProperty(entry.getKey().toString(), entry.getValue());
        //$$ }
        //#endif
        json.add("key", jsonKey);
        JsonObject jsonResult = new JsonObject();
        jsonResult.addProperty("id", resultItem);
        jsonResult.addProperty("count", resultCount);
        json.add("result", jsonResult);
        recipeMap.put(recipeId, json);
    }
}
