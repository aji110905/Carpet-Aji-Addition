package aji.carpetajiaddition.mixin.rules.glowingHopperMinecart;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartHopper.class)
public abstract class MinecartHopperMixin extends AbstractMinecartContainer{
    protected MinecartHopperMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean isEnabled();

    @Unique
    private boolean nextTickState = isEnabled();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        //#if MC < 12109
        MinecraftServer server = getServer();
        //#else
        //$$ MinecraftServer server = level().getServer();
        //#endif
        if (server == null) {
            return;
        }
        ServerScoreboard scoreboard = server.getScoreboard();
        if (!CarpetAjiAdditionSettings.glowingHopperMinecart) {
            PlayerTeam enabled = scoreboard.getPlayerTeam("enabled_hopper_minecraft");
            if (enabled != null) {
                scoreboard.removePlayerTeam(enabled);
            }
            PlayerTeam locked = scoreboard.getPlayerTeam("locked_hopper_minecraft");
            if (locked != null) {
                scoreboard.removePlayerTeam(locked);
            }
            return;
        }
        this.setGlowingTag(true);
        PlayerTeam enabled = scoreboard.getPlayerTeam("enabled_hopper_minecraft");
        if (enabled == null) {
            enabled = scoreboard.addPlayerTeam("enabled_hopper_minecraft");
            enabled.setColor(ChatFormatting.WHITE);
        }
        PlayerTeam locked = scoreboard.getPlayerTeam("locked_hopper_minecraft");
        if (locked == null) {
            locked = scoreboard.addPlayerTeam("locked_hopper_minecraft");
            locked.setColor(ChatFormatting.RED);
        }
        if (isEnabled() == nextTickState) return;
        if (isEnabled()) {
            scoreboard.addPlayerToTeam(getUUID().toString(), enabled);
        } else {
            scoreboard.addPlayerToTeam(getUUID().toString(), locked);
        }
        nextTickState = isEnabled();
    }
}
