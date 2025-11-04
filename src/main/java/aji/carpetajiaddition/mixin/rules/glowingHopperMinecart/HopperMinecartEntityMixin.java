package aji.carpetajiaddition.mixin.rules.glowingHopperMinecart;

import aji.carpetajiaddition.CarpetAjiAdditionModEntryPoint;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HopperMinecartEntity.class)
public abstract class HopperMinecartEntityMixin extends StorageMinecartEntity implements Hopper {
    @Shadow
    public abstract boolean isEnabled();

    protected HopperMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private boolean nextTickState = isEnabled();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!CarpetAjiAdditionSettings.glowingHopperMinecart) {
            if (CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().getTeam("enabled_hopper_minecraft") != null) {
                CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().removeTeam(CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().getTeam("enabled_hopper_minecraft"));
            }
            if (CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().getTeam("locked_hopper_minecraft") != null) {
                CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().removeTeam(CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().getTeam("locked_hopper_minecraft"));
            }
            return;
        }
        this.setGlowing(true);
        Team enabled = CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().getTeam("enabled_hopper_minecraft");
        if (enabled == null) {
            enabled = CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().addTeam("enabled_hopper_minecraft");
            enabled.setColor(Formatting.WHITE);
        }
        Team locked = CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().getTeam("locked_hopper_minecraft");
        if (locked == null) {
            locked = CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().addTeam("locked_hopper_minecraft");
            locked.setColor(Formatting.RED);
        }
        if (isEnabled() == nextTickState) return;
        if (isEnabled()) {
            CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().addScoreHolderToTeam(getUuid().toString(), enabled);
        } else {
            CarpetAjiAdditionModEntryPoint.minecraftServer.getScoreboard().addScoreHolderToTeam(getUuid().toString(), locked);
        }
        nextTickState = isEnabled();
    }
}
