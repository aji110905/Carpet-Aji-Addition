package top.ajitech.carpetajiaddition.mixin.rules.alwaysFullBeacon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I"
            )
    )
    private static int alwaysFullBeacon(Level level, int x, int y, int z, Operation<Integer> original) {
        if (CarpetAjiAdditionRules.ALWAYS_FULL_BEACON.value()) {
            return 4;
        }
        return original.call(level, x, y, z);
    }
}
