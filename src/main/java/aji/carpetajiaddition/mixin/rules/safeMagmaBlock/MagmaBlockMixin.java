package aji.carpetajiaddition.mixin.rules.safeMagmaBlock;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public abstract class MagmaBlockMixin extends Block {

    public MagmaBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    private void onSteepedOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo ci){
        if (!CarpetAjiAdditionSettings.safeMagmaBlock) return;
        if (level.isClientSide()) return;
        super.stepOn(level, blockPos, blockState, entity);
        ci.cancel();
    }
}
