package aji.carpetajiaddition.recipe;

import com.google.gson.JsonElement;
//#if MC >= 12102
//$$ import net.minecraft.core.HolderLookup;
//#endif
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public abstract class Recipe {
    protected final String resultItem;
    protected final int resultCount;

    protected Recipe(String resultItem, int resultCount) {
        this.resultItem = resultItem;
        this.resultCount = resultCount;
    }

    public abstract void addToRecipeMap(Map<ResourceLocation, JsonElement> recipeMap);

    public static Recipe empty() {
        return new Recipe("", 0) {
            @Override
            public void addToRecipeMap(Map<ResourceLocation, JsonElement> recipeMap) {
            }
        };
    }
}
