package top.ajitech.carpetajiaddition.recipe;

import com.google.gson.JsonElement;
//#if MC >= 12102
//$$ import net.minecraft.core.HolderLookup;
//#endif
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

@FunctionalInterface
public interface Recipe {
    void addToRecipeMap(Map<ResourceLocation, JsonElement> recipeMap);
}
