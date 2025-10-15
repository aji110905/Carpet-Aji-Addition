package aji.carpetajiaddition.mixin.command.follow;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import aji.carpetajiaddition.commands.FollowCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Ownable {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (FollowCommand.data.getFollowItems().contains(getStack().getItem())) {
            CarpetAjiAdditionMod.minecraftServer.getScoreboard().addScoreHolderToTeam(getUuid().toString(), CarpetAjiAdditionMod.minecraftServer.getScoreboard().getTeam("followItems"));
            setGlowing(true);
        } else {
            CarpetAjiAdditionMod.minecraftServer.getScoreboard().getTeam("followItems").getPlayerList().forEach(uuidString -> {
                if (uuidString.equals(getUuid().toString())) {
                    CarpetAjiAdditionMod.minecraftServer.getScoreboard().removeScoreHolderFromTeam(uuidString, CarpetAjiAdditionMod.minecraftServer.getScoreboard().getTeam("followItems"));
                }
            });
            setGlowing(false);
        }
    }
}
