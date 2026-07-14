package top.ajitech.carpetajiaddition.mixin.recipe;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionExtension;
import top.ajitech.carpetajiaddition.recipe.RecipeManager;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(net.minecraft.world.item.crafting.RecipeManager.class)
public abstract class RecipeManagerMixin {
    @ModifyVariable(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Map<ResourceLocation, JsonElement> apply(Map<ResourceLocation, JsonElement> map) {
        RecipeManager recipeManager = CarpetAjiAdditionExtension.INSTANCE.getRecipeManager();
        if (recipeManager != null) {
            recipeManager.registerRecipe(map);
        }
        return map;
    }
}
