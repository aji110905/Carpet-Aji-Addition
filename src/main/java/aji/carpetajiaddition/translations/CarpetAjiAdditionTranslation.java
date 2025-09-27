package aji.carpetajiaddition.translations;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class CarpetAjiAdditionTranslation {
    private static Map<String, String> CarpetAjiAdditionTranslations;

    public static void readLanguageFiles(String lang){
        CarpetAjiAdditionTranslations = getTranslationsMap.getCarpetAjiAdditionTranslations(lang);
    }
    public static String tr (String path){
        String str = CarpetAjiAdditionTranslations.get(path);
        if (str == null) return "";
        else return str;
    }

    public static String tr (String path, String... args) {
        String str = tr(path);
        if (str.isEmpty()) return str;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String value = (args[i] == null) ? "null" : args[i];
            str = str.replace(placeholder, value);
        }
        return str;
    }

    public static String tr (String path, Text... args){
        if (tr(path).isEmpty()) return "";
        String[] stringArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            stringArgs[i] = args[i].getString();
        }
        return tr(path, stringArgs);
    }

    public static Text trText (String path) {
        if (tr(path).isEmpty()) return Text.empty();
        String str = tr(path);
        return Text.literal(str);
    }

    public static Text trText (String path, String... args) {
        if (tr(path).isEmpty()) return Text.empty();
        String str = tr(path, args);
        return Text.literal(str);
    }

    public static Text trText (String path, Text... args){
        String template = tr(path);
        if (template.isEmpty()) return Text.empty();
        Text result = Text.empty();
        String[] parts = template.split("\\{\\d+}");
        for (int i = 0; i < parts.length; i++) {
            result = result.copy().append(parts[i]);
            if (i < args.length) {
                result = result.copy().append(args[i]);
            }
        }
        return result;
    }

    public static class trColor {
        public static String tr (Formatting color){
            return CarpetAjiAdditionTranslation.tr(TranslationsKey.COLOR + color.getName());
        }

        public static Text trText (Formatting color, boolean colorful){
            MutableText text = Text.literal(tr(color));
            if (colorful) {
                return text.withColor(color.getColorValue());
            }else return text;
        }
    }
}
