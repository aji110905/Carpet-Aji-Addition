package aji.carpetajiaddition.recipe.template;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface RecipeTemplateInterface {
    void addToRecipeMap(Map<ResourceLocation, JsonElement> recipeMap);
}
