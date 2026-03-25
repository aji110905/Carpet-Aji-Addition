package aji.carpetajiaddition.recipe.builder;

import aji.carpetajiaddition.recipe.RecipeManager;
import aji.carpetajiaddition.util.ResourceLocationUtil;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipeBuilder extends AbstractRecipeBuilder{
    private final List<String> patternRows = new ArrayList<>();
    private final Map<Character, Item> ingredients = new HashMap<>();

    private ShapedRecipeBuilder(boolean enabled, String recipeName) {
        super(enabled, recipeName);
    }

    public static ShapedRecipeBuilder create(boolean enabled, String recipeName) {
        return new ShapedRecipeBuilder(enabled, recipeName);
    }

    public ShapedRecipeBuilder pattern(String row) {
        if (row.length() > 3 || patternRows.size() > 3) {
            throw new IllegalArgumentException("Pattern rows cannot be longer than 3 characters");
        }
        patternRows.add(row);
        return this;
    }

    public ShapedRecipeBuilder define(char symbol, Item item) {
        ingredients.put(symbol, item);
        return this;
    }

    @Override
    public void build() {
        if (!enabled || resultItem == null) {
            return;
        }
        String[][] pattern = new String[patternRows.size()][];
        for (int i = 0; i < patternRows.size(); i++) {
            pattern[i] = patternRows.get(i).split("");
        }
        Map<Character, String> ingredientMap = new HashMap<>();
        ingredients.forEach((k, v) -> ingredientMap.put(k, ResourceLocationUtil.getItemRegistryName(v)));
        RecipeManager.addShapedRecipe(recipeName, pattern, ingredientMap, ResourceLocationUtil.getItemRegistryName(resultItem), resultCount);
    }
}
