package aji.carpetajiaddition.util;

import aji.carpetajiaddition.CarpetAjiAdditionExtension;
import aji.carpetajiaddition.constant.TranslationsKey;
import aji.carpetajiaddition.translate.TranslateManager;
import aji.carpetajiaddition.translate.Translator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.minecraft.network.chat.Component.literal;

public final class TranslateUtil {
    private TranslateUtil(){

    }

    public static String tr (String key){
        return getTranslator().tr(key);
    }

    public static String tr (String key, String... args) {
        return getTranslator().tr(key, args);
    }

    public static String tr (String key, Component... args){
        String[] argsStr = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            argsStr[i] = args[i].getString();
        }
        return tr(key, argsStr);
    }

    public static String tr (ChatFormatting color){
        return tr(TranslationsKey.COLOR + color.getName());
    }

    public static Component trc (String key) {
        return literal(tr(key));
    }

    public static Component trc (String key, String... args) {
        return literal(tr(key, args));
    }

    public static Component trc (String key, Component... args){
        String template = tr(key);
        if (template.isEmpty()) return Component.empty();
        Component result = Component.empty();
        String[] parts = template.split("\\{\\d+}");
        for (int i = 0; i < parts.length; i++) {
            result = result.copy().append(parts[i]);
            if (i < args.length) {
                result = result.copy().append(args[i]);
            }
        }
        return result;
    }

    public static Component trc (ChatFormatting color, boolean colorful){
        MutableComponent component = literal(tr(color));
        if (colorful && color.isColor()) {
            return component.withColor(color.getColor());
        } else {
            return component;
        }
    }

    public static Component trc (ChatFormatting color){
        return trc(color, true);
    }
    
    private static Translator getTranslator(){
        return CarpetAjiAdditionExtension.INSTANCE.getTranslateManager().getTranslator();
    }
}
