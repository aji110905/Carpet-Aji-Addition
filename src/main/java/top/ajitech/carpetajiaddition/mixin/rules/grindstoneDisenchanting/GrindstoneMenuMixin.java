package top.ajitech.carpetajiaddition.mixin.rules.grindstoneDisenchanting;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import top.ajitech.carpetajiaddition.helper.GrindstoneDisenchantingHelper;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {
    @Unique
    private boolean disenchantingQuickMove;

    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    private void computeResult(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<ItemStack> cir) {
        if (!CarpetAjiAdditionRules.grindstoneDisenchanting) {
            return;
        }
        if (!GrindstoneDisenchantingHelper.isValidDisenchantingInput(itemStack, itemStack2)) {
            return;
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(itemStack).entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (GrindstoneDisenchantingHelper.shouldSkipEnchantment(holder)) {
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

    @Inject(method = "quickMoveStack", at = @At("HEAD"))
    private void quickMoveStackHead(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        this.disenchantingQuickMove = false;
        if (!CarpetAjiAdditionRules.grindstoneDisenchanting) {
            return;
        }
        if (index != GrindstoneMenu.RESULT_SLOT) {
            return;
        }
        GrindstoneMenu menu = (GrindstoneMenu) (Object) this;
        ItemStack result = menu.getSlot(GrindstoneMenu.RESULT_SLOT).getItem();
        if (result.isEmpty() || !result.is(Items.ENCHANTED_BOOK)) {
            return;
        }
        Container inputSlots = menu.getSlot(GrindstoneMenu.INPUT_SLOT).container;
        ItemStack input = inputSlots.getItem(0);
        ItemStack book = inputSlots.getItem(1);
        if (!GrindstoneDisenchantingHelper.isValidDisenchantingInput(input, book)) {
            return;
        }
        this.disenchantingQuickMove = true;
    }

    @Inject(method = "quickMoveStack", at = @At("RETURN"), cancellable = true)
    private void quickMoveStackReturn(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.disenchantingQuickMove) {
            return;
        }
        this.disenchantingQuickMove = false;
        cir.setReturnValue(ItemStack.EMPTY);
    }
}
