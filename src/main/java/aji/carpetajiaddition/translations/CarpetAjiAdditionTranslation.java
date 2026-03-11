package aji.carpetajiaddition.translations;

import aji.carpetajiaddition.settings.RuleCategory;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.SettingsManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarpetAjiAdditionTranslation {
    private static Map<String, String> translations = null;
    private static final Map<String, Map<String, String>> ALL_TRANSLATION_MAP = new HashMap<>();
    private static final Map<String, Map<String, String>> ALL_FABRIC_CARPET_TRANSLATION_MAP = new HashMap<>();
    private static boolean machineFlipRuleIsAdded = false;

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

    public static class trColor {
        public static String tr (ChatFormatting color){
            final String COLOR = TranslationsKey.SUFFIX + "color.";
            return CarpetAjiAdditionTranslation.tr(COLOR + color.getName());
        }

        public static Component trText (ChatFormatting color, boolean colorful){
            MutableComponent component = Component.literal(tr(color));
            if (colorful) {
                return component.withColor(color.getColor());
            }else return component;
        }
    }

    private static void getTranslationFromResourcePath() {
        String[] languageFiles = {
                "en_us",
                "fr_fr",
                "pt_br",
                "zh_cn",
                "zh_tw"
        };
        Gson gson = new Gson();
        for (String language : languageFiles) {
            String resourcePath = "assets/carpetajiaddition/lang/" + language + ".json";
            InputStream inputStream = CarpetAjiAdditionTranslation.class.getClassLoader().getResourceAsStream(resourcePath);
            Map<String, String> translations = gson.fromJson(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                    new TypeToken<Map<String, String>>(){}.getType()
            );

            Map<String, String> fabricCarpetTranslations = new HashMap<>();
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                String originalKey = entry.getKey();
                if (originalKey != null && originalKey.startsWith("carpetajiaddition.carpet")) {
                    String newKey = originalKey.substring(18);
                    fabricCarpetTranslations.put(newKey, entry.getValue());
                }
            }
            ALL_FABRIC_CARPET_TRANSLATION_MAP.put(language, fabricCarpetTranslations);

            Map<String, String> translationsMap = new HashMap<>();
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                String originalKey = entry.getKey();
                if (!(originalKey != null && originalKey.startsWith("carpetajiaddition.carpet"))) {
                    translationsMap.put(entry.getKey(), entry.getValue());
                }
            }
            ALL_TRANSLATION_MAP.put(language, translationsMap);
        }
    }

    public static Map<String, String> getFabricCarpetTranslations(String lang) {
        if (machineFlipRuleIsAdded && !(boolean) CarpetServer.settingsManager.getCarpetRule("useMachineFlip").value()) {
            lang = "zh_cn";
        }
        translations = new HashMap<>(ALL_TRANSLATION_MAP.get(lang));
        return new HashMap<>(ALL_FABRIC_CARPET_TRANSLATION_MAP.get(lang));
    }

    public static void addMachineFlipRuleToSettingManager(){
        CarpetServer.settingsManager.addCarpetRule(
                new CarpetRule<Boolean>() {
                    private Boolean value = true;
                    private static final String NAME = "useMachineFlip";

                    @Override
                    public String name() {
                        return NAME;
                    }

                    @Override
                    public List<Component> extraInfo() {
                        return List.of();
                    }

                    @Override
                    public Collection<String> categories() {
                        return List.of(RuleCategory.CAA);
                    }

                    @Override
                    public Collection<String> suggestions() {
                        return List.of("true", "false");
                    }

                    @Override
                    public SettingsManager settingsManager() {
                        return CarpetServer.settingsManager;
                    }

                    @Override
                    public Boolean value() {
                        return value;
                    }

                    @Override
                    public boolean canBeToggledClientSide() {
                        return false;
                    }

                    @Override
                    public Class<Boolean> type() {
                        return Boolean.class;
                    }

                    @Override
                    public Boolean defaultValue() {
                        return true;
                    }

                    @Override
                    public void set(CommandSourceStack source, String value) throws InvalidRuleValueException {
                        if (value.equals("true")) set(source, true);
                        else if (value.equals("false")) set(source, false);
                        else throw new InvalidRuleValueException("Invalid boolean value");
                    }

                    @Override
                    public void set(CommandSourceStack source, Boolean value) throws InvalidRuleValueException {
                        this.value = value;
                        CarpetRule<?> rule = CarpetServer.settingsManager.getCarpetRule("language");
                        rule.set(null, (String) rule.value());
                    }

                    @Override
                    public String toString() {
                        return NAME + ": " + value;
                    }
                }
        );
        machineFlipRuleIsAdded = true;
    }

    static {
        getTranslationFromResourcePath();
    }
}
