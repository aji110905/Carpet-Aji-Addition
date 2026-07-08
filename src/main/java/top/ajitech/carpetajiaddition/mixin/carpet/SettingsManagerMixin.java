package top.ajitech.carpetajiaddition.mixin.carpet;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionRules;
import top.ajitech.carpetajiaddition.constant.ModConstants;
import top.ajitech.carpetajiaddition.constant.TranslationsKey;
import top.ajitech.carpetajiaddition.util.TranslateUtil;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
    @Shadow
    protected abstract int setDefault(CommandSourceStack source, CarpetRule<?> rule, String stringValue);

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
        Messenger.m(source, "g Carpet Aji Addition " + TranslateUtil.tr(TranslationsKey.SUFFIX + "version") + ModConstants.VERSION);
    }

    @Inject(method = "setRule", at = @At("RETURN"))
    private void setRule(CommandSourceStack source, CarpetRule<?> rule, String value, CallbackInfoReturnable<Integer> cir) {
        if (CarpetAjiAdditionRules.isMustSetDefaultRule(rule.name())) {
            setDefault(source, rule, value);
        }
    }
}
