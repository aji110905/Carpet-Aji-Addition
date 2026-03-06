package aji.carpetajiaddition.mixin.rules.tameHorse;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
//#if MC < 12105
import net.minecraft.inventory.InventoryChangedListener;
//#endif
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
//#if MC < 12105
public abstract class AbstractHorseEntityMixin extends AnimalEntity implements InventoryChangedListener, RideableInventory, Tameable, JumpingMount, Saddleable {
//#else
//$$ public abstract class AbstractHorseEntityMixin extends AnimalEntity implements RideableInventory, Tameable, JumpingMount {
//#endif
    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getTemper", at = @At("HEAD"), cancellable = true)
    public void getTemper(CallbackInfoReturnable<Integer> cir) {
        if (!CarpetAjiAdditionSettings.tameHorse) return;
        cir.setReturnValue(100);
    }
}
