package aji.carpetajiaddition.mixin.rules.sitOnTheGround;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Unique
    private boolean lastSneakState = false;
    @Unique
    private long firstSneakTimestamp = -1;
    @Unique
    private int sneakCount = 0;
    @Unique
    private Entity ridenEntity = null;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!CarpetAjiAdditionSettings.sitOnTheGround) return;
        boolean isSneaking = this.isCrouching();
        long currentTime = this.level().getGameTime();
        if (ridenEntity != null && !this.isPassenger()) {
            ridenEntity.discard();
            ridenEntity = null;
        }

        if (isSneaking && !lastSneakState) {
            if (sneakCount == 0) {
                firstSneakTimestamp = currentTime;
                sneakCount = 1;
            }
        } else if (!isSneaking && lastSneakState) {
            if (sneakCount >= 1 && sneakCount < 4) {
                if (currentTime - firstSneakTimestamp <= 10) {
                    sneakCount++;
                } else {
                    sneakCount = 0;
                    firstSneakTimestamp = -1;
                }
            } else if (sneakCount == 4) {
                if (!level().isClientSide()) {
                    if (ridenEntity != null) {
                        ridenEntity.discard();
                        ridenEntity = null;
                    }
                    ArmorStand armorStand = new ArmorStand(this.level(), this.getX(), this.getY() - 1.9, this.getZ());
                    ridenEntity = armorStand;
                    armorStand.setInvisible(true);
                    armorStand.setNoGravity(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setCustomNameVisible(false);
                    this.level().addFreshEntity(armorStand);
                    //#if MC < 12109
                    this.startRiding(armorStand, true);
                    //#else
                    //$$ this.startRiding(armorStand, true, true);
                    //#endif
                }
                sneakCount = 0;
                firstSneakTimestamp = -1;
            }
        }

        lastSneakState = isSneaking;
    }
}
