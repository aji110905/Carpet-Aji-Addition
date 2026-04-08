package aji.carpetajiaddition.mixin.settings;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.commands.ReloadCommand;
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
                    target = "Lnet/minecraft/server/commands/ReloadCommand;reloadPacks(Ljava/util/Collection;Lnet/minecraft/commands/CommandSourceStack;)V")
    )
    private static void onReloadDataPacks(CommandContext context, CallbackInfoReturnable<Integer> cir) {
        CarpetAjiAdditionSettings.EXTENSION.onReload();
    }
}
