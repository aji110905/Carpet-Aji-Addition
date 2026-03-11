package aji.carpetajiaddition.mixin.rules.tameHorse;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin{
    @Inject(method = "getTemper", at = @At("HEAD"), cancellable = true)
    public void getTemper(CallbackInfoReturnable<Integer> cir) {
        if (!CarpetAjiAdditionSettings.tameHorse) return;
        cir.setReturnValue(100);
    }
}
