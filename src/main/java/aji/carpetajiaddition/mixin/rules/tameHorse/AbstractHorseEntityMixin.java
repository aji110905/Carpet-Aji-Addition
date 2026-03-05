package aji.carpetajiaddition.mixin.rules.tameHorse;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity implements InventoryChangedListener, RideableInventory, Tameable, JumpingMount, Saddleable {

    protected AbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getTemper", at = @At("HEAD"), cancellable = true)
    public void getTemper(CallbackInfoReturnable<Integer> cir) {
        if (!CarpetAjiAdditionSettings.tameHorse) return;
        cir.setReturnValue(100);
    }
}
