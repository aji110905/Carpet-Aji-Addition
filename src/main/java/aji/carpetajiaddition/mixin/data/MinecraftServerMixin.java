package aji.carpetajiaddition.mixin.data;

import aji.carpetajiaddition.CarpetAjiAdditionModEntryPoint;
import net.minecraft.network.QueryableServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin extends ReentrantThreadExecutor<ServerTask> implements QueryableServer, ChunkErrorHandler, CommandOutput, AutoCloseable{
    public MinecraftServerMixin(String string) {
        super(string);
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void save(boolean suppressLogs, boolean flush, boolean force, CallbackInfoReturnable<Boolean> cir) {
        CarpetAjiAdditionModEntryPoint.data.saveData();
    }
}
