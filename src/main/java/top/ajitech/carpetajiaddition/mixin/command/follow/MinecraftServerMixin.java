package top.ajitech.carpetajiaddition.mixin.command.follow;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ajitech.carpetajiaddition.data.FollowCommandData;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow
    @Final
    private ServerScoreboard scoreboard;

    @Inject(method = "loadLevel", at = @At("RETURN"), order = 1100)
    private void loadLevel(CallbackInfo ci) {
        PlayerTeam team = scoreboard.getPlayerTeam("followItems");
        if (team == null){
            team = scoreboard.addPlayerTeam("followItems");
        }
        //#if MC < 260200
        team.setColor(FollowCommandData.getInstance().getColor());
        //#else
        //$$ team.setColor(java.util.Optional.of(FollowCommandData.getInstance().getColor()));
        //#endif
    }
}
