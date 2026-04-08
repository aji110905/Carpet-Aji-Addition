package aji.carpetajiaddition.mixin.settings;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantBlockableEventLoop<TickTask> implements ServerInfo, ChunkIOErrorReporter, CommandSource, AutoCloseable{
    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void close(CallbackInfo ci) {
        CarpetAjiAdditionSettings.EXTENSION.afterServerClose();
    }

    @Inject(method = "saveEverything", at = @At("HEAD"))
    private void saveEverything(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        CarpetAjiAdditionSettings.EXTENSION.onSave();
    }
}
