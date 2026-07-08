package top.ajitech.carpetajiaddition.translate;

import java.util.HashMap;
import java.util.Map;

public class Translator {
    private static final String PREFIX = "carpetajiaddition.";
    private static final String FABRIC_CARPET_PREFIX = "carpetajiaddition.carpet";

    private final Map<String, String> translations;
    private final Map<String, String> fabricCarpetTranslations;

    public String tr (String key){
        String str = translations.get(key);
        if (str == null) {
            return key;
        } else {
            return str;
        }
    }

    public String tr (String key, String... args) {
        String str = tr(key);
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String value = (args[i] == null) ? "null" : args[i];
            str = str.replace(placeholder, value);
        }
        return str;
    }

    protected Translator(Object yaml) {
        translations = new HashMap<>();
        fabricCarpetTranslations = new HashMap<>();
        Map<String, String> langMap = new HashMap<>();
        flatten("", (Map<String, Object>) yaml, langMap);
        for (Map.Entry<String, String> entry : langMap.entrySet()) {
            String originalKey = entry.getKey();
            if (originalKey.startsWith(FABRIC_CARPET_PREFIX)) {
                String newKey = originalKey.substring(PREFIX.length());
                fabricCarpetTranslations.put(newKey, entry.getValue());
            }
        }
        for (Map.Entry<String, String> entry : langMap.entrySet()) {
            String originalKey = entry.getKey();
            if (!originalKey.startsWith(FABRIC_CARPET_PREFIX)) {
                translations.put(originalKey, entry.getValue());
            }
        }
    }

    protected Map<String, String> getFabricCarpetTranslation(){
        return fabricCarpetTranslations;
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
}
