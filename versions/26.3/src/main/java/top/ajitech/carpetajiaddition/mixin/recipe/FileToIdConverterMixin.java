package top.ajitech.carpetajiaddition.mixin.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionExtension;
import top.ajitech.carpetajiaddition.recipe.RecipeManager;

import java.util.Map;

@Mixin(FileToIdConverter.class)
public class FileToIdConverterMixin {
    @Inject(method = "listMatchingResources", at = @At("RETURN"), cancellable = true)
    private void addCustomRecipes(ResourceManager resourceManager, CallbackInfoReturnable<Map<Identifier, Resource>> cir) {
        FileToIdConverter converter = (FileToIdConverter) (Object) this;
        if (converter.prefix().equals(Registries.elementsDirPath(Registries.RECIPE))) {
            Map<Identifier, Resource> map = cir.getReturnValue();
            RecipeManager recipeManager = CarpetAjiAdditionExtension.INSTANCE.getRecipeManager();
            if (recipeManager != null) {
                recipeManager.registerRecipe(map, (FileToIdConverter) (Object) this, resourceManager.listPacks().findFirst().orElseThrow());
                cir.setReturnValue(map);
            }
        }
    }
}
