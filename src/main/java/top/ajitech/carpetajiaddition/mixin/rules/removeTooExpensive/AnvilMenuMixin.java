package top.ajitech.carpetajiaddition.mixin.rules.removeTooExpensive;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
    private int removeTooExpensive(int costLimit) {
        return CarpetAjiAdditionRules.removeTooExpensive ? Integer.MAX_VALUE : costLimit;
    }
}
