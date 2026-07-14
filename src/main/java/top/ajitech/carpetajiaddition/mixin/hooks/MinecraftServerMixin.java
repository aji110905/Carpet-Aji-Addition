package top.ajitech.carpetajiaddition.mixin.hooks;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionExtension;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        CarpetAjiAdditionExtension.INSTANCE.onServerCreated((MinecraftServer) (Object) this);
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void close(CallbackInfo ci) {
        CarpetAjiAdditionExtension.INSTANCE.afterServerClose((MinecraftServer) (Object) this);
    }

    @Inject(method = "saveEverything", at = @At("HEAD"))
    private void saveEverything(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        CarpetAjiAdditionExtension.INSTANCE.onSave((MinecraftServer) (Object) this);
    }
}
