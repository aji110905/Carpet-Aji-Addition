package top.ajitech.carpetajiaddition.mixin.hooks;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionServer;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        CarpetAjiAdditionServer.setInstance(new CarpetAjiAdditionServer((MinecraftServer) (Object) this));
        CarpetAjiAdditionServer.getInstance().onServerCreated();
    }

    @Inject(method = "loadLevel", at = @At("RETURN"))
    private void loadLevel(CallbackInfo ci) {
        CarpetAjiAdditionServer.getInstance().onLoadedLevel();
    }

    @Inject(method = "saveEverything", at = @At("HEAD"))
    private void saveEverything(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        CarpetAjiAdditionServer.getInstance().onSave();
    }
}
