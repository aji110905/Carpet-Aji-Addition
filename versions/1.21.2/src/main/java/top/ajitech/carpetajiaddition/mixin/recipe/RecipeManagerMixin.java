package top.ajitech.carpetajiaddition.mixin.recipe;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionExtension;
import top.ajitech.carpetajiaddition.recipe.RecipeManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.SortedMap;

@Mixin(net.minecraft.world.item.crafting.RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Inject(
            method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/ArrayList;<init>(I)V"
            )
    )
    private void prepare(CallbackInfoReturnable<RecipeMap> cir, @Local SortedMap<ResourceLocation, Recipe<?>> map) {
        RecipeManager recipeManager = CarpetAjiAdditionExtension.INSTANCE.getRecipeManager();
        if (recipeManager != null) {
            recipeManager.registerRecipe(map, ((RecipeManagerAccessor) this).getRegistries());
        }
    }
}
