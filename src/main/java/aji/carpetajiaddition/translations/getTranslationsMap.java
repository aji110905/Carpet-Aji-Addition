package aji.carpetajiaddition.translations;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class getTranslationsMap {
    private static Map<String, String> getTranslationFromResourcePath(String lang) {
        Map<String, String> translations = Maps.newHashMap();
        String resourcePath = "assets/carpetajiaddition/lang/zh_cn.json";
        if (CarpetAjiAdditionSettings.useMachineFlip){
            resourcePath = "assets/carpetajiaddition/lang/"+ lang +".json";
        }
        InputStream inputStream = getTranslationsMap.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            return translations;
        }

        Gson gson = new Gson();
        translations = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), new TypeToken<Map<String, String>>(){}.getType());


        return translations;
    }

    public static Map<String, String> getFabricCarpetTranslations(String lang) {
        Map<String, String> fabricCarpetTranslations = Maps.newHashMap();
        Map<String, String> translations = getTranslationFromResourcePath(lang);

        String targetPrefix = "carpetajiaddition.carpet";
        String removePrefix = "carpetajiaddition.";

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            String originalKey = entry.getKey();
            if (originalKey != null && originalKey.startsWith(targetPrefix)) {
                String newKey = originalKey.substring(removePrefix.length());
                fabricCarpetTranslations.put(newKey, entry.getValue());
            }
        }

        return fabricCarpetTranslations;
    }

    public static Map<String, String> getCarpetAjiAdditionTranslations(String lang) {
        Map<String, String> CarpetAjiAdditionTranslations = Maps.newHashMap();
        Map<String, String> translations = getTranslationFromResourcePath(lang);

        String targetPrefix = "carpetajiaddition.carpet";

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            String originalKey = entry.getKey();
            if (!(originalKey != null && originalKey.startsWith(targetPrefix))) {
                CarpetAjiAdditionTranslations.put(entry.getKey(), entry.getValue());
            }
        }

        return CarpetAjiAdditionTranslations;
    }
}