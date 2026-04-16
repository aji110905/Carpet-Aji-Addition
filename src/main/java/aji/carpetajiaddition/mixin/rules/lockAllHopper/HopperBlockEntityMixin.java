package aji.carpetajiaddition.mixin.rules.lockAllHopper;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin{
    @Inject(method = "tryMoveItems", at = @At("HEAD"), cancellable = true)
    private static void insertAndExtract(Level level, BlockPos blockPos, BlockState blockState, HopperBlockEntity hopperBlockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetAjiAdditionSettings.lockAllHopper) cir.setReturnValue(false);
    }
}
