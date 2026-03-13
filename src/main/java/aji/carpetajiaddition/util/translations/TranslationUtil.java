package aji.carpetajiaddition.util.translations;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.util.FileUtil;
import aji.carpetajiaddition.util.IOUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class TranslationUtil {
    private static Map<String, String> translations = new HashMap<>();
    private static final List<String> LANGUAGES = new ArrayList<>();
    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final Map<String, Map<String, String>> ALL_TRANSLATION_MAP = new HashMap<>();
    private static final Map<String, Map<String, String>> ALL_FABRIC_CARPET_TRANSLATION_MAP = new HashMap<>();

    private TranslationUtil(){
    }

    public static String tr (String path){
        String str = translations.get(path);
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

    public static String tr (String path, Component... args){
        if (tr(path).isEmpty()) return "";
        String[] stringArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            stringArgs[i] = args[i].getString();
        }
        return tr(path, stringArgs);
    }

    public static String tr (ChatFormatting color){
        final String COLOR = TranslationsKey.SUFFIX + "color.";
        return TranslationUtil.tr(COLOR + color.getName());
    }

    public static Component trComponent (String path) {
        if (tr(path).isEmpty()) return Component.empty();
        String str = tr(path);
        return Component.literal(str);
    }

    public static Component trComponent (String path, String... args) {
        if (tr(path).isEmpty()) return Component.empty();
        String str = tr(path, args);
        return Component.literal(str);
    }

    public static Component trComponent (String path, Component... args){
        String template = tr(path);
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

    public static Component trComponent (ChatFormatting color, boolean colorful){
        MutableComponent component = Component.literal(tr(color));
        if (colorful && color.isColor()) {
            return component.withColor(color.getColor());
        }else return component;
    }

    private static void getTranslationFromResourcePath() {
        try {
            Map<String, String> map = IOUtil.readAllFilesFromResource("assets/carpetajiaddition/lang");
            Yaml yaml = new Yaml();
            for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                Map<String, String> langMap = new HashMap<>();
                Map<String, Object> yamlMap = yaml.load(mapEntry.getValue());
                flatten("", yamlMap, langMap);
                String language = FileUtil.getFileNameWithoutExtension(mapEntry.getKey());
                LANGUAGES.add(language);

                Map<String, String> fabricCarpetTranslations = new HashMap<>();
                for (Map.Entry<String, String> entry : langMap.entrySet()) {
                    String originalKey = entry.getKey();
                    if (originalKey != null && originalKey.startsWith("carpetajiaddition.carpet")) {
                        String newKey = originalKey.substring(18);
                        fabricCarpetTranslations.put(newKey, entry.getValue());
                    }
                }
                ALL_FABRIC_CARPET_TRANSLATION_MAP.put(language, fabricCarpetTranslations);

                Map<String, String> translationsMap = new HashMap<>();
                for (Map.Entry<String, String> entry : langMap.entrySet()) {
                    String originalKey = entry.getKey();
                    if (!(originalKey != null && originalKey.startsWith("carpetajiaddition.carpet"))) {
                        translationsMap.put(entry.getKey(), entry.getValue());
                    }
                }
                ALL_TRANSLATION_MAP.put(language, translationsMap);
            }
        } catch (IOException | URISyntaxException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Error when loading translations", e);
        }

    }

    private static void flatten(String prefix, Map<String, Object> map, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (value instanceof Map) {
                flatten(fullKey, (Map<String, Object>) value, result);
            } else {
                result.put(fullKey, String.valueOf(value));
            }
        }
    }

    public static Map<String, String> getFabricCarpetTranslations(String lang) {
        if (!LANGUAGES.contains(lang)) {
            lang = DEFAULT_LANGUAGE;
        }
        translations = ALL_TRANSLATION_MAP.get(lang);
        return ALL_FABRIC_CARPET_TRANSLATION_MAP.get(lang);
    }

    static {
        getTranslationFromResourcePath();
    }
}
