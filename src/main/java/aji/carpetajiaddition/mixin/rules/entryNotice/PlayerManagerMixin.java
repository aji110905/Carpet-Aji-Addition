package aji.carpetajiaddition.mixin.rules.entryNotice;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.config.NoticeConfig;
import aji.carpetajiaddition.notice.NoticeContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if (CarpetAjiAdditionSettings.entryNotice) return;
        NoticeConfig config = (NoticeConfig) CarpetAjiAdditionSettings.config.getConfig(NoticeConfig.CONFIG_NAME);
        NoticeContext context = new NoticeContext(player, server);
        if (config.getPriority().equals("others")){
            sendOthersNotice(context, config);
            config.getEntrant().send(context);
        } else {
            config.getEntrant().send(context);
            sendOthersNotice(context, config);
        }
    }

    @Unique
    private void sendOthersNotice(NoticeContext context, NoticeConfig config) {
        for (ServerPlayerEntity playerEntity : players) {
            if (config.isGeneral() || !playerEntity.getUuid().equals(context.player().getUuid())){
                config.getOthers().send(context);
            }
        }
    }
}
