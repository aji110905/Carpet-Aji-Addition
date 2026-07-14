package top.ajitech.carpetajiaddition.mixin.rules.safeCreeper;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperMixin {
    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void explodeCreeper(CallbackInfo ci) {
        if (CarpetAjiAdditionRules.safeCreeper) {
            ci.cancel();
        }
    }
}
