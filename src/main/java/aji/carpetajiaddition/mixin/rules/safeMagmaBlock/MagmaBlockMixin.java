package aji.carpetajiaddition.mixin.rules.safeMagmaBlock;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void onSteepedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci){
        if (!CarpetAjiAdditionSettings.safeMagmaBlock) return;
        if (world.isClient) return;
        super.onSteppedOn(world, pos, state, entity);
        ci.cancel();
    }
}
