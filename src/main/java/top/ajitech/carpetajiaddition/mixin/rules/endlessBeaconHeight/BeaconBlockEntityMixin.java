package top.ajitech.carpetajiaddition.mixin.rules.endlessBeaconHeight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
    @WrapOperation(
            method = "applyEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            )
    )
    private static <T extends Entity> List<T> endlessBeaconHeight(Level level, Class<T> type, AABB area, Operation<List<T>> original) {
        if (CarpetAjiAdditionRules.ENDLESS_BEACON_HEIGHT.value()) {
            //#if MC < 12102
            area = new AABB(area.minX, level.getMinBuildHeight(), area.minZ, area.maxX, area.maxY, area.maxZ);
            //#else
            //$$ area = new AABB(area.minX, level.getMinY(), area.minZ, area.maxX, area.maxY, area.maxZ);
            //#endif
        }
        return original.call(level, type, area);
    }
}
