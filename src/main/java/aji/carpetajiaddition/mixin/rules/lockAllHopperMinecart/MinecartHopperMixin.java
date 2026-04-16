package aji.carpetajiaddition.mixin.rules.lockAllHopperMinecart;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartHopper.class)
public abstract class MinecartHopperMixin{
    @Inject(method = "isEnabled", at = @At("HEAD"), cancellable = true)
    private void isEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetAjiAdditionSettings.lockAllHopperMinecart) cir.setReturnValue(false);
    }
}
