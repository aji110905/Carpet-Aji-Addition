package aji.carpetajiaddition.recipe.template;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Map;

public interface RecipeTemplateInterface {
    void addToRecipeMap(Map<ResourceLocation, Recipe<?>> recipeMap, HolderLookup.Provider provider);
}
