package aji.carpetajiaddition.settings;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.SettingsManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RecipeRule implements CarpetRule<Boolean> {
    public static String PATH;
    private final String name;
    private Boolean value = false;

    public RecipeRule(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Text> extraInfo() {
        return List.of();
    }

    @Override
    public Collection<String> categories() {
        return List.of(RuleCategory.CAA, RuleCategory.RECIPE);
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
        return false;
    }

    @Override
    public boolean strict() {
        return true;
    }

    @Override
    public void set(ServerCommandSource source, String value) throws InvalidRuleValueException {
        if (value.equals("true")) set(source, true);
        else if (value.equals("false")) set(source, false);
        else throw new InvalidRuleValueException("Invalid boolean value");
    }

    @Override
    public void set(ServerCommandSource source, Boolean value) throws InvalidRuleValueException {
        Map<String, String> recipeFiles = readRecipeFiles(name);
        if (value != null) {
            if (value) {
                loadRecipe(recipeFiles);
            } else {
                unloadRecipe(recipeFiles);
            }
            if (source != null && source.getWorld() != null) CarpetAjiAdditionSettings.minecraftServer.reloadResources(CarpetAjiAdditionSettings.minecraftServer.getDataPackManager().getEnabledIds());
        }else{
            throw new InvalidRuleValueException("Invalid boolean value");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

    public static void addRecipeRulesToSettingManager(){
        CarpetServer.settingsManager.addCarpetRule(new RecipeRule("dragonEggRecipe"));
        CarpetServer.settingsManager.addCarpetRule(new RecipeRule("dragonBreathRecipe"));
    }

    private void loadRecipe(Map<String, String> files){
        files.forEach((fileName, jsonFile) -> {
            File file = new File(PATH, fileName);
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(jsonFile);
                writer.close();
            } catch (IOException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Recipes cannot be loaded into data pack", e);
            }
        });
    }

    private void unloadRecipe(Map<String, String> files){
        if (files == null) return;
        files.forEach((fileName, jsonFile) -> {
            File file = new File(PATH, fileName);
            if (file.exists()) {
                file.delete();
            }
        });
    }

    private Map<String, String> readRecipeFiles(String folderName) {
        Map<String, String> fileMap = new HashMap<>();
        try {
            URL url = RecipeRule.class.getClassLoader().getResource("assets/carpetajiaddition/RecipesTweak/" + folderName);
            if ("jar".equals(url.getProtocol())) {
                String jarPath = URLDecoder.decode(url.getPath().split("!")[0].substring(5), StandardCharsets.UTF_8);
                JarFile jar = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith("assets/carpetajiaddition/RecipesTweak/" + folderName + "/") && !entry.isDirectory()) {
                        InputStream stream = jar.getInputStream(entry);
                        fileMap.put(entry.getName().split("/")[entry.getName().split("/").length - 1], new String(stream.readAllBytes()));
                        stream.close();
                    }
                }
                jar.close();
            } else {
                File[] files = new File(url.toURI()).listFiles();
                if (files != null) {
                    for (File file : files) {
                        FileInputStream stream = new FileInputStream(file);
                        fileMap.put(file.getName(), new String(stream.readAllBytes()));
                        stream.close();
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Reading recipes from resource files failed", e);
        }
        return fileMap;
    }
}
