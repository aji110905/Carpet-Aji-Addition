package aji.carpetajiaddition.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public abstract class Recipe {
    protected final String resultItem;
    protected final int resultCount;

    protected Recipe(String resultItem, int resultCount) {
        this.resultItem = resultItem;
        this.resultCount = resultCount;
    }

    public abstract void addToRecipeMap(Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> recipeMap, HolderLookup.Provider provider);

    public static Recipe empty() {
        return new Recipe("", 0) {
            @Override
            public void addToRecipeMap(Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> recipeMap, HolderLookup.Provider provider) {

            }
        };
    }
}
