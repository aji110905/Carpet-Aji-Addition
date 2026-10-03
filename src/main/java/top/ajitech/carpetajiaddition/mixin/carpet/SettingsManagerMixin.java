package top.ajitech.carpetajiaddition.mixin.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Inject(
            method = "registerCommand",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;"
            )
    )
    private static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, CallbackInfo ci, @Local LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder) {
        literalargumentbuilder.then(Commands.literal("extensions").executes(context -> {
            int error = 0;
            List<String> success = new ArrayList<>();
            for (CarpetExtension extension : CarpetServer.extensions) {
                String id = extension.version();
                ModContainer modContainer = FabricLoader.getInstance().getModContainer(id).orElse(null);
                if (modContainer == null) {
                    error++;
                    continue;
                }
                ModMetadata metadata = modContainer.getMetadata();
                String name = Objects.requireNonNullElse(metadata.getName(), id);
                Version version = metadata.getVersion();
                success.add(version == null ? name : name + " " + version);
            }
            CommandSourceStack source = context.getSource();
            Messenger.m(source, "w " + TranslateUtil.tr(TranslationsKey.SUFFIX + "command.carpet.extensions.start", String.valueOf(success.size() + error)));
            for (String string : success) {
                Messenger.m(source, "g " + string);
            }
            if (error > 0) {
                Messenger.m(source, "r " + TranslateUtil.tr(TranslationsKey.SUFFIX + "command.carpet.extensions.error", String.valueOf(error)));
            }
            return 1;
        }));
    }
}
