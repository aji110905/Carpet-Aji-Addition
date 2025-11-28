package aji.carpetajiaddition.mixin.rules.entryNotice;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.config.NoticeConfig;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if (!CarpetAjiAdditionSettings.entryNotice) return;
        ((NoticeConfig) CarpetAjiAdditionSettings.config.getConfig("notice")).send(player);
    }
}
