package aji.carpetajiaddition.mixin.data;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadCommand.class)
public abstract class ReloadCommandMixin {
    @Inject(
            method = "method_13530",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/ReloadCommand;tryReloadDataPacks(Ljava/util/Collection;Lnet/minecraft/server/command/ServerCommandSource;)V")
    )
    private static void onReloadDataPacks(CommandContext context, CallbackInfoReturnable<Integer> cir) {
        CarpetAjiAdditionMod.data.loadData();
    }
}
