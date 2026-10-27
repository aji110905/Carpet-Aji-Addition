package top.ajitech.carpetajiaddition.mixin.hooks;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionServer;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin {
    //#if MC < 260100
    @Inject(method = "method_13530", at = @At("TAIL"), remap = false)
    //#else
    //$$ @Inject(method = "lambda$register$0", at = @At("TAIL"), remap = false)
    //#endif
    private static void onReload(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        CarpetAjiAdditionServer.getInstance().onReload();
    }
}
