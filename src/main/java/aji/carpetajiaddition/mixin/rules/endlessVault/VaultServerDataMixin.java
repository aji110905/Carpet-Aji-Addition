package aji.carpetajiaddition.mixin.rules.endlessVault;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.UUID;

@Mixin(VaultServerData.class)
public abstract class VaultServerDataMixin {
    @Inject(method = "hasRewardedPlayer", at = @At("HEAD"), cancellable = true)
    public void hasRewardedPlayer(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (CarpetAjiAdditionSettings.keepOpeningVault) cir.setReturnValue(false);
    }

    @Inject(method = "getRewardedPlayers", at = @At("HEAD"), cancellable = true)
    public void getRewardedPlayers(CallbackInfoReturnable<Set<UUID>> cir) {
        if (CarpetAjiAdditionSettings.keepOpeningVault) cir.setReturnValue(new ObjectLinkedOpenHashSet<>());
    }
}
