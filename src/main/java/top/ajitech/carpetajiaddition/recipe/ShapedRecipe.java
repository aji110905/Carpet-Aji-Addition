package top.ajitech.carpetajiaddition.recipe;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getItemRegistryName;
import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getResourceLocation;

public class ShapedRecipe implements Recipe {
    private static final String TYPE = "minecraft:crafting_shaped";

    private final ResourceLocation id;
    private final String[][] pattern;
    private final Map<Character, String> ingredients;
    private final String result;
    private final int count;

    private ShapedRecipe(ResourceLocation id, String[][] pattern, Map<Character, String> ingredients, String result, int count) {
        this.id = id;
        this.pattern = pattern;
        this.ingredients = ingredients;
        this.result = result;
        this.count = count;
    }

    @Override
    public void addToRecipeMap(Map<ResourceLocation, JsonElement> map) {
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
        jsonResult.addProperty("id", result);
        jsonResult.addProperty("count", count);
        json.add("result", jsonResult);
        map.put(id, json);
    }

    public static Builder builder(String recipeName, Item resultItem, int resultCount) {
        return new Builder(recipeName, resultItem, resultCount);
    }

    public static class Builder{
        private final String name;
        private final Item result;
        private final int count;
        private final List<String> patternRows = new ArrayList<>();
        private final Map<Character, Item> ingredients = new HashMap<>();

        private Builder(String name, Item result, int count) {
            this.name = name;
            this.result = result;
            this.count = count;
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
            Map<Character, String> map = new HashMap<>();
            ingredients.forEach((k, v) -> map.put(k, getItemRegistryName(v)));
            return new ShapedRecipe(getResourceLocation(name), pattern, map, getItemRegistryName(result), count);
        }
    }
}
