package aji.carpetajiaddition.mixin.carpet;

import aji.carpetajiaddition.CarpetAjiAdditionModEntryPoint;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.setting.CarpetRecipeRule;
import aji.carpetajiaddition.setting.RecipeRule;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import carpet.utils.Messenger;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
    @Shadow(remap = false)
    public abstract void addCarpetRule(CarpetRule<?> rule);

    @Inject(
            method = "listAllSettings",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/server/command/ServerCommandSource;[Ljava/lang/Object;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    public void listAllSettings(ServerCommandSource source, CallbackInfoReturnable<Integer> cir) {
        Messenger.m(source, "g Carpet Aji Addition " + CarpetAjiAdditionTranslation.tr("carpetajiaddition.version") + CarpetAjiAdditionSettings.VERSION);
    }

    @Inject(method = "parseSettingsClass", at = @At("HEAD"), remap = false)
    private void parseSettingsClass(Class<?> settingsClass, CallbackInfo ci) {
        if (settingsClass != CarpetAjiAdditionSettings.class) return;
        for (Field field :settingsClass.getDeclaredFields()){
            if (field.getAnnotation(RecipeRule.class) == null) continue;
            addCarpetRule(new CarpetRecipeRule(field.getName()));
        }
    }
}
