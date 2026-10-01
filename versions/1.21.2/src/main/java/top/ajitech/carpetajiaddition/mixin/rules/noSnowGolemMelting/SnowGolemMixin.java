package top.ajitech.carpetajiaddition.mixin.rules.noSnowGolemMelting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.SnowGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

@Mixin(SnowGolem.class)
public class SnowGolemMixin {
    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/SnowGolem;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean aiStep(SnowGolem instance, ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Boolean> original) {
        if (CarpetAjiAdditionRules.noSnowGolemMelting) {
            return false;
        }
        return original.call(instance, serverLevel, damageSource, f);
    }
}
