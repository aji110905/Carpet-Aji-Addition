package aji.carpetajiaddition.mixin.rules.recipeRule;
import aji.carpetajiaddition.recipe.CustomRecipes;
import aji.carpetajiaddition.recipe.RecipeManager;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(net.minecraft.world.item.crafting.RecipeManager.class)
public abstract class RecipeManagerMixin {
    @ModifyVariable(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), argsOnly = true)
    private Map<ResourceLocation, JsonElement> registerCustomRecipes(Map<ResourceLocation, JsonElement> map) {
        RecipeManager.clearRecipeListMemory();
        CustomRecipes.buildRecipes();
        RecipeManager.registerRecipes(map);
        return map;
    }
}
