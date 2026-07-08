package top.ajitech.carpetajiaddition.mixin.rules.anvilCannotDamage;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void damage(BlockState blockState, CallbackInfoReturnable<BlockState> cir) {
        if (CarpetAjiAdditionRules.anvilCannotDamage) {
            cir.setReturnValue(blockState);
        }
    }
}
