package aji.carpetajiaddition.mixin.carpet;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.setting.validators.RecipeRuleValidator;
import carpet.CarpetServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarpetServer.class)
public abstract class CarpetServerMixin {
    @Inject(method = "onServerLoaded", at = @At("HEAD"), remap = false)
    private static void onServerLoaded(MinecraftServer server, CallbackInfo ci) {
        CarpetAjiAdditionSettings.minecraftServer = server;
        RecipeRuleValidator.initializationDataPack();
    }
}
