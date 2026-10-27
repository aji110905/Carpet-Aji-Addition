package top.ajitech.carpetajiaddition.mixin.rules.removeTooExpensive;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Restriction(conflict = @Condition("carpet_cuo_addition"))
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2), require = 0)
    private int removeTooExpensive(int costLimit) {
        return CarpetAjiAdditionRules.REMOVE_TOO_EXPENSIVE.value() ? Integer.MAX_VALUE : costLimit;
    }
}
