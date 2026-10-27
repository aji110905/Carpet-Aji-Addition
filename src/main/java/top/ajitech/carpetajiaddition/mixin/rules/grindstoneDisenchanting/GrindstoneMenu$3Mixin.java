package top.ajitech.carpetajiaddition.mixin.rules.grindstoneDisenchanting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$3")
public abstract class GrindstoneMenu$3Mixin {
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void mayPlace(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!CarpetAjiAdditionRules.GRINDSTONE_DISENCHANTING.value()) {
            return;
        }
        if (itemStack.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(itemStack)) {
            cir.setReturnValue(true);
            return;
        }
        if (itemStack.is(Items.BOOK)) {
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(false);
    }
}
