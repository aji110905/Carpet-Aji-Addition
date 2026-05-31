package aji.carpetajiaddition.translate;

import aji.carpetajiaddition.exception.InitializationException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslateManager {
    private static final String LANG_FILE_PATH = "/assets/carpetajiaddition/lang/";
    private static final String META_FILE_PATH = LANG_FILE_PATH + "meta/meta.yml";
    private static final String LANG_FILE_EXT = ".yml";

    private final String defaultLanguage;
    private final List<String> languages;
    private final Map<String, Translator> translators;

    private String currentLanguage;

    public TranslateManager(){
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getResourceAsStream(META_FILE_PATH)) {
            Map<String, Object> meat = yaml.load(inputStream);
            defaultLanguage = (String) meat.get("default");
            currentLanguage = defaultLanguage;
            languages = (List<String>) meat.get("languages");
            if (!languages.contains(defaultLanguage)) {
                languages.add(defaultLanguage);
            }
        } catch (IOException e) {
            throw new InitializationException("Failed to read language meta file", e);
        } catch (Exception e) {
            throw new InitializationException("Language meta file format error", e);
        }
        translators = new HashMap<>();
        for (String language : languages) {
            try (InputStream inputStream = getClass().getResourceAsStream(LANG_FILE_PATH + language + LANG_FILE_EXT)) {
                translators.put(language, new Translator(yaml.load(inputStream)));
            } catch (IOException e) {
                throw new InitializationException("Failed to read language file", e);
            } catch (Exception e) {
                throw new InitializationException("Language file format error", e);
            }
        }
    }

    public void updateTranslations(String lang) {
        if (languages.contains(lang)) {
            currentLanguage = lang;
        } else {
            currentLanguage = defaultLanguage;
        }
    }

    public Map<String, String> getFabricCarpetTranslationMap() {
        return getTranslator().getFabricCarpetTranslation();
    }

    public Translator getTranslator() {
        return translators.get(currentLanguage);
    }
}
