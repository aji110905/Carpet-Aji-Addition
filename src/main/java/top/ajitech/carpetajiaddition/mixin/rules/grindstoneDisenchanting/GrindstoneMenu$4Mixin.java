package top.ajitech.carpetajiaddition.mixin.rules.grindstoneDisenchanting;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class GrindstoneMenu$4Mixin {
    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void onTake(Player player, ItemStack itemStack, CallbackInfo ci) {
        if (!CarpetAjiAdditionRules.grindstoneDisenchanting) {
            return;
        }
        if (!(player.containerMenu instanceof GrindstoneMenu menu)) {
            return;
        }
        Container inputSlots = menu.getSlot(GrindstoneMenu.INPUT_SLOT).container;
        ItemStack input = inputSlots.getItem(0);
        ItemStack book = inputSlots.getItem(1);
        if (input.isEmpty() || input.getCount() > 1 || book.isEmpty() || !book.is(Items.BOOK)) {
            return;
        }
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
        if (enchantments.isEmpty()) {
            return;
        }
        ci.cancel();
        player.level().levelEvent(1042, player.blockPosition(), 0);
        EnchantmentHelper.updateEnchantments(input, mutable -> mutable.set(enchantments.entrySet().iterator().next().getKey(), 0));
        if (input.is(Items.ENCHANTED_BOOK) && EnchantmentHelper.getEnchantmentsForCrafting(input).isEmpty()) {
            inputSlots.setItem(0, input.transmuteCopy(Items.BOOK));
        } else {
            inputSlots.setItem(0, input);
        }
        ItemStack books = book.copy();
        books.shrink(1);
        inputSlots.setItem(1, books.isEmpty() ? ItemStack.EMPTY : books);
    }
}
