package aji.carpetajiaddition.mixin.carpet;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.util.translations.TranslationUtil;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
    @Inject(
            method = "listAllSettings",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/commands/CommandSourceStack;[Ljava/lang/Object;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void listAllSettings(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        Messenger.m(source, "g Carpet Aji Addition " + TranslationUtil.tr("carpetajiaddition.version") + CarpetAjiAdditionSettings.VERSION);
    }
}
