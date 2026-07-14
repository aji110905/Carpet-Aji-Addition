package top.ajitech.carpetajiaddition.util;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionExtension;
import top.ajitech.carpetajiaddition.constant.TranslationsKey;
import top.ajitech.carpetajiaddition.translate.Translator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.TeamColor;

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

    public static String tr (TeamColor color){
        return tr(TranslationsKey.COLOR + color.getSerializedName());
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

    public static Component trc (TeamColor color, boolean colorful){
        MutableComponent component = literal(tr(color));
        if (colorful) {
            return component.withColor(color.textColor());
        } else {
            return component;
        }
    }

    public static Component trc (TeamColor color){
        return trc(color, true);
    }
    
    private static Translator getTranslator(){
        return CarpetAjiAdditionExtension.INSTANCE.getTranslateManager().getTranslator();
    }
}