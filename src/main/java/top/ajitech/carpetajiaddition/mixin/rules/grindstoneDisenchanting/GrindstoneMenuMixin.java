package top.ajitech.carpetajiaddition.mixin.rules.grindstoneDisenchanting;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

@Mixin(net.minecraft.world.inventory.GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {
    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    private void computeResult(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<ItemStack> cir) {
        if (!CarpetAjiAdditionRules.grindstoneDisenchanting) {
            return;
        }
        if (itemStack.isEmpty() || itemStack.getCount() > 1 || itemStack2.isEmpty() || !itemStack2.is(Items.BOOK)) {
            return;
        }
        if (!EnchantmentHelper.hasAnyEnchantments(itemStack)) {
            return;
        }
        // 诅咒类附魔只有在开启 enhanceGrindstone 时才允许被剥离。
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(itemStack).entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (holder.is(EnchantmentTags.CURSE) && !CarpetAjiAdditionRules.enhanceGrindstone) {
                continue;
            }
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(book));
            mutable.set(holder, entry.getIntValue());
            EnchantmentHelper.setEnchantments(book, mutable.toImmutable());
            cir.setReturnValue(book);
            return;
        }
    }
}
