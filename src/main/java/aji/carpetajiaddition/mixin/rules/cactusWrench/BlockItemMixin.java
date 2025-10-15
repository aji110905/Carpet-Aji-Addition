package aji.carpetajiaddition.mixin.rules.cactusWrench;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import carpet.helpers.BlockRotator;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void place(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir){
        if (!CarpetAjiAdditionSettings.cactusWrench) return;
        PlayerEntity player = context.getPlayer();
        if (player == null) return;
        if (!(player.getOffHandStack().getItem() == Items.CACTUS)) return;
        Block block = state.getBlock();
        if (!(block instanceof ObserverBlock || block instanceof DispenserBlock || block instanceof PistonBlock || block instanceof SlabBlock || block instanceof EndRodBlock)) return;
        cir.setReturnValue(
                BlockRotator.flipBlock(
                        state,
                        context.getWorld(),
                        player, context.getHand(),
                        new BlockHitResult(
                                context.getHitPos(),
                                context.getSide(),
                                context.getBlockPos(),
                                false
                        )
                )
        );
    }
}
