package top.ajitech.carpetajiaddition.mixin.rules.fullBeaconRange;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import top.ajitech.carpetajiaddition.setting.IntegerRule;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
    @WrapOperation(
            method = "applyEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"
            )
    )
    private static AABB fullBeaconRange(AABB instance, double amount, Operation<AABB> original) {
        int level = (int) (amount / 10.0D) - 1;
        IntegerRule rule = CarpetAjiAdditionRules.FULL_BEACON_RANGE;
        if (level >= 4 && rule.value() != (int) amount) {
            return instance.inflate(rule.value(), amount, rule.value());
        }
        return original.call(instance, amount);
    }
}
