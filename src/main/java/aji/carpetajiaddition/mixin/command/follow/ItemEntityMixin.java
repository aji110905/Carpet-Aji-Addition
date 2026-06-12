package aji.carpetajiaddition.mixin.command.follow;

import aji.carpetajiaddition.data.FollowCommandData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity{
    @Shadow
    public abstract ItemStack getItem();

    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

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
        PlayerTeam team = scoreboard.getPlayerTeam("followItems");
        if (team == null) {
            return;
        }
        String uuid = getUUID().toString();
        if (FollowCommandData.getInstance().getFollowItems().contains(getItem().getItem())) {
            scoreboard.addPlayerToTeam(uuid, team);
            setGlowingTag(true);
        } else {
            if (team.getPlayers().contains(uuid)) {
                scoreboard.removePlayerFromTeam(uuid, team);
            }
            setGlowingTag(false);
        }
    }
}
