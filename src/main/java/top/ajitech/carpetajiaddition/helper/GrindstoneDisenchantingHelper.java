package top.ajitech.carpetajiaddition.helper;

import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

public final class GrindstoneDisenchantingHelper {
    private GrindstoneDisenchantingHelper() {
    }

    public static boolean isValidDisenchantingInput(ItemStack input, ItemStack book) {
        return !input.isEmpty()
                && input.getCount() == 1
                && !book.isEmpty()
                && book.is(Items.BOOK)
                && EnchantmentHelper.hasAnyEnchantments(input);
    }

    public static boolean shouldSkipEnchantment(Holder<Enchantment> holder) {
        return holder.is(EnchantmentTags.CURSE) && !CarpetAjiAdditionRules.ENHANCE_GRINDSTONE.value();
    }
}
