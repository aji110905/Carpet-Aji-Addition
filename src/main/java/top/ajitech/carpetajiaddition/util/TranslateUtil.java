package top.ajitech.carpetajiaddition.util;

import top.ajitech.carpetajiaddition.translate.TranslateManager;
import top.ajitech.carpetajiaddition.translate.TranslationsKey;
import top.ajitech.carpetajiaddition.translate.Translator;
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
        if (template.isEmpty()) {
            return Component.empty();
        }
        MutableComponent result = Component.empty();
        int cursor = 0;
        for (Component arg : args) {
            int index = template.indexOf("{}", cursor);
            if (index < 0) {
                break;
            }
            result.append(literal(template.substring(cursor, index)));
            result.append(arg);
            cursor = index + 2;
        }
        result.append(literal(template.substring(cursor)));
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
        return TranslateManager.INSTANCE.getTranslator();
    }
}
