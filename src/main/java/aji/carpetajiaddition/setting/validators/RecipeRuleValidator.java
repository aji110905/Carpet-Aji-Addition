package aji.carpetajiaddition.setting.validators;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RecipeRuleValidator<T> extends Validator<T>{
    private String targetPath;

    @Override
    public T validate(@Nullable ServerCommandSource source, CarpetRule<T> changingRule, T newValue, String string) {
        targetPath = CarpetAjiAdditionSettings.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "/CarpetAjiAdditionData/data/carpetajiaddition/recipe";
        Map<String, String> recipeFiles = readRecipeFiles(changingRule.name());
        if (newValue instanceof Boolean) {
            if ((Boolean) newValue) {
                loadRecipe(recipeFiles);
            } else {
                unloadRecipe(recipeFiles);
            }
            if (source != null && source.getWorld() != null) CarpetAjiAdditionSettings.minecraftServer.reloadResources(CarpetAjiAdditionSettings.minecraftServer.getDataPackManager().getEnabledIds());
            return newValue;
        }else{
            return null;
        }
    }

    public void loadRecipe(Map<String, String> files){
        files.forEach((fileName, jsonFile) -> {
            File file = new File(targetPath, fileName);
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

    public void unloadRecipe(Map<String, String> files){
        if (files == null) return;
        files.forEach((fileName, jsonFile) -> {
            File file = new File(targetPath, fileName);
            if (file.exists()) {
                file.delete();
            }
        });
    }

    public Map<String, String> readRecipeFiles(String folderName) {
        Map<String, String> fileMap = new HashMap<>();
        try {
            URL url = RecipeRuleValidator.class.getClassLoader().getResource("assets/carpetajiaddition/RecipesTweak/" + folderName);
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

    public static void initializationDataPack() {
        cleanDataPack();
        File file = new File(CarpetAjiAdditionSettings.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "/CarpetAjiAdditionData/pack.mcmeta");
        file.getParentFile().mkdirs();
        try {
            InputStream stream = RecipeRuleValidator.class.getClassLoader().getResourceAsStream("assets/carpetajiaddition/pack.mcmeta.json");
            Files.write(file.toPath(), stream.readAllBytes());
            stream.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Initializing data pack failed", e);
        }
    }

    public static void cleanDataPack() {
        File file = new File(CarpetAjiAdditionSettings.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "/CarpetAjiAdditionData");
        if (file.exists()) {
            try {
                Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Failed to clean up data pack residual data", e);
            }
        }
    }
}