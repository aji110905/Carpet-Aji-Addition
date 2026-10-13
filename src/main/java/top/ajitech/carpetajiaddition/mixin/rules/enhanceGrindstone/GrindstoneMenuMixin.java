package top.ajitech.carpetajiaddition.mixin.rules.enhanceGrindstone;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {
    @Inject(method = "removeNonCursesFrom", at = @At("RETURN"), cancellable = true)
    private void removeNonCursesFrom(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        if (!CarpetAjiAdditionRules.enhanceGrindstone) {
            return;
        }
        ItemStack result = cir.getReturnValue();
        if (result.isEmpty()) {
            return;
        }
        boolean cursed = false;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(result).entrySet()) {
            if (entry.getKey().is(EnchantmentTags.CURSE)) {
                cursed = true;
                break;
            }
        }
        if (!cursed) {
            return;
        }
        EnchantmentHelper.updateEnchantments(result, mutable -> mutable.removeIf(holder -> holder.is(EnchantmentTags.CURSE)));
        if (result.is(Items.ENCHANTED_BOOK) && EnchantmentHelper.getEnchantmentsForCrafting(result).isEmpty()) {
            result = result.transmuteCopy(Items.BOOK);
        }
        result.set(DataComponents.REPAIR_COST, 0);
        cir.setReturnValue(result);
    }
}
