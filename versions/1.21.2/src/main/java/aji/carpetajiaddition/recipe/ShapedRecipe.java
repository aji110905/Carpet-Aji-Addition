package aji.carpetajiaddition.recipe;

import aji.carpetajiaddition.exception.RecipeBuildException;
import aji.carpetajiaddition.util.ResourceLocationUtil;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipe extends Recipe {
    private static final String TYPE = "minecraft:crafting_shaped";

    private final ResourceLocation recipeId;
    private final String[][] pattern;
    private final Map<Character, String> ingredients;

    private ShapedRecipe(ResourceLocation recipeId, String[][] pattern, Map<Character, String> ingredients, String resultItem, int resultCount) {
        super(resultItem, resultCount);
        this.recipeId = recipeId;
        this.pattern = pattern;
        this.ingredients = ingredients;
    }

    @Override
    public void addToRecipeMap(Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> recipeMap, HolderLookup.Provider provider) {
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
            jsonKey.addProperty(entry.getKey().toString(), entry.getValue());
        }
        json.add("key", jsonKey);
        JsonObject jsonResult = new JsonObject();
        jsonResult.addProperty("id", resultItem);
        jsonResult.addProperty("count", resultCount);
        json.add("result", jsonResult);
        recipeMap.put(
                recipeId,
                net.minecraft.world.item.crafting.Recipe.CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new)
        );
    }

    public static Builder builder(boolean enabled, String recipeName) {
        return new Builder(enabled, recipeName);
    }

    public static class Builder extends RecipeBuilder<ShapedRecipe>{
        private final List<String> patternRows = new ArrayList<>();
        private final Map<Character, Item> ingredients = new HashMap<>();

        private Builder(boolean enabled, String recipeName) {
            super(enabled, recipeName);
        }


        public Builder pattern(String row) {
            if (row.length() > 3 || patternRows.size() > 3) {
                throw new RecipeBuildException("Pattern rows cannot be longer than 3 characters");
            }
            patternRows.add(row);
            return this;
        }

        public Builder define(char symbol, Item item) {
            ingredients.put(symbol, item);
            return this;
        }

        @Override
        public Recipe build() {
            if (!isSetResult) {
                throw new RecipeBuildException("You must set the result item");
            }
            if (!enabled) {
                return Recipe.empty();
            }
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
