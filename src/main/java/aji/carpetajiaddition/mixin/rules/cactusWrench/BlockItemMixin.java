package aji.carpetajiaddition.mixin.rules.cactusWrench;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import carpet.helpers.BlockRotator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    public BlockItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "placeBlock",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void placeBlock(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir){
        if (!CarpetAjiAdditionSettings.cactusWrench) return;
        Player player = context.getPlayer();
        if (player == null) return;
        if (!(player.getOffhandItem().getItem() == Items.CACTUS)) return;
        Block block = state.getBlock();
        if (!(block instanceof ObserverBlock || block instanceof DispenserBlock || block instanceof PistonBaseBlock || block instanceof SlabBlock || block instanceof EndRodBlock)) return;
        cir.setReturnValue(
                BlockRotator.flipBlock(
                        state,
                        context.getLevel(),
                        player, context.getHand(),
                        new BlockHitResult(
                                context.getClickLocation(),
                                context.getHorizontalDirection(),
                                context.getClickedPos(),
                                false
                        )
                )
        );
    }
}
