package top.ajitech.carpetajiaddition.mixin.rules.waterInNether;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

//#if MC < 12111
import net.minecraft.world.level.dimension.DimensionType;
//#else
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.attribute.EnvironmentAttribute;
//$$ import net.minecraft.world.attribute.EnvironmentAttributeSystem;
//$$ import net.minecraft.world.attribute.EnvironmentAttributes;
//#endif
import net.minecraft.world.item.BucketItem;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @WrapOperation(
            method = "emptyContents",
            at = @At(
                    value = "INVOKE",
    //#if MC < 12111
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            )
    )
    private boolean waterInNether(DimensionType instance, Operation<Boolean> original) {
        if (CarpetAjiAdditionRules.WATER_IN_NETHER.value()) {
            return false;
        }
        return original.call(instance);
    }
    //#else
    //$$                 target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"
    //$$         )
    //$$ )
    //$$ private Object waterInNether(EnvironmentAttributeSystem instance, EnvironmentAttribute<?> attribute, BlockPos pos, Operation<Object> original) {
    //$$     if (CarpetAjiAdditionRules.WATER_IN_NETHER.value() && attribute == EnvironmentAttributes.WATER_EVAPORATES) {
    //$$         return Boolean.FALSE;
    //$$     }
    //$$     return original.call(instance, attribute, pos);
    //$$ }
    //#endif
}
