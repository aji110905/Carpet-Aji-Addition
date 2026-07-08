package top.ajitech.carpetajiaddition.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface Recipe {
    void addToRecipeMap(Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> recipeMap, HolderLookup.Provider provider);
}
