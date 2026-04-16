package aji.carpetajiaddition.mixin.rules.toughTurtleEgg;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.entity.Entity;
//#if MC < 12102
import net.minecraft.world.level.Level;
//#else
//$$ import net.minecraft.server.level.ServerLevel;
//#endif
import net.minecraft.world.level.block.TurtleEggBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleEggBlock.class)
public abstract class TurtleEggBlockMixin{
    @Inject(method = "canDestroyEgg", at = @At("HEAD"), cancellable = true)
    //#if MC < 12102
    private void canDestroyEgg(Level level, Entity entity, CallbackInfoReturnable<Boolean> cir) {
    //#else
    //$$ private void canDestroyEgg(ServerLevel serverLevel, Entity entity, CallbackInfoReturnable<Boolean> cir) {
    //#endif
        if (CarpetAjiAdditionSettings.toughTurtleEgg) {
            cir.setReturnValue(false);
        }
    }
}
