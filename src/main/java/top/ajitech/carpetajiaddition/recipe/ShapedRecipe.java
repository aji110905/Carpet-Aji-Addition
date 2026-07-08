package top.ajitech.carpetajiaddition.recipe;

import top.ajitech.carpetajiaddition.util.ResourceLocationUtil;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipe implements Recipe {
    private static final String TYPE = "minecraft:crafting_shaped";

    private final ResourceLocation recipeId;
    private final String[][] pattern;
    private final Map<Character, String> ingredients;
    private final String resultItem;
    private final int resultCount;

    private ShapedRecipe(ResourceLocation recipeId, String[][] pattern, Map<Character, String> ingredients, String resultItem, int resultCount) {
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
        for (Map.Entry<Character, String> entry : ingredients.entrySet()) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("item", entry.getValue());
            jsonKey.add(entry.getKey().toString(), itemJson);
        }
        json.add("key", jsonKey);
        JsonObject jsonResult = new JsonObject();
        jsonResult.addProperty("id", resultItem);
        jsonResult.addProperty("count", resultCount);
        json.add("result", jsonResult);
        recipeMap.put(recipeId, json);
    }

    public static Builder builder(String recipeName, Item resultItem, int resultCount) {
        return new Builder(recipeName, resultItem, resultCount);
    }

    public static class Builder{
        private final String recipeName;
        private final Item resultItem;
        private final int resultCount;
        private final List<String> patternRows = new ArrayList<>();
        private final Map<Character, Item> ingredients = new HashMap<>();

        private Builder(String recipeName, Item resultItem, int resultCount) {
            this.recipeName = recipeName;
            this.resultItem = resultItem;
            this.resultCount = resultCount;
        }


        public Builder pattern(String row) {
            patternRows.add(row);
            return this;
        }

        public Builder define(char symbol, Item item) {
            ingredients.put(symbol, item);
            return this;
        }

        public ShapedRecipe build() {
            String[][] pattern = new String[patternRows.size()][];
            for (int i = 0; i < patternRows.size(); i++) {
                pattern[i] = patternRows.get(i).split("");
            }
            Map<Character, String> ingredientMap = new HashMap<>();
            ingredients.forEach((k, v) -> ingredientMap.put(k, ResourceLocationUtil.getItemRegistryName(v)));
            return new ShapedRecipe(
                    ResourceLocationUtil.getResourceLocation(recipeName),
                    pattern,
                    ingredientMap,
                    ResourceLocationUtil.getItemRegistryName(resultItem),
                    resultCount
            );
        }
    }
}
