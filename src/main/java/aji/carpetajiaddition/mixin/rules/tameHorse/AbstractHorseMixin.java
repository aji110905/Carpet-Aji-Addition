package aji.carpetajiaddition.mixin.rules.tameHorse;

import aji.carpetajiaddition.CarpetAjiAdditionRules;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin{
    @Inject(method = "getTemper", at = @At("HEAD"), cancellable = true)
    public void getTemper(CallbackInfoReturnable<Integer> cir) {
        if (!CarpetAjiAdditionRules.tameHorse) return;
        cir.setReturnValue(100);
    }
}
