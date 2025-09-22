package aji.carpetajiaddition.validators.RecipeRule;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RecipeRuleValidator<T> extends Validator<T> implements RecipeRule{
    private String targetPath;
    private Map<String, JsonObject> recipeFiles;

    @Override
    public T validate(@Nullable ServerCommandSource source, CarpetRule<T> changingRule, T newValue, String string) {
        targetPath = CarpetAjiAdditionMod.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "/CarpetAjiAdditionData/data/carpetajiaddition//recipe";
        recipeFiles = readRecipeFiles(changingRule.name());
        if (newValue instanceof Boolean) {
            if ((Boolean) newValue) {
                loadRecipe(recipeFiles);
            } else {
                unloadRecipe(recipeFiles);
            }
            if (source != null && source.getWorld() != null) CarpetAjiAdditionMod.minecraftServer.reloadResources(CarpetAjiAdditionMod.minecraftServer.getDataPackManager().getEnabledIds());
            return newValue;
        }else{
            return changingRule.value();
        }
    }

    @Override
    public void loadRecipe(Map map){
        if (map == null) return;
        Map<String, JsonObject> files = (Map<String, JsonObject>) map;
        files.forEach((fileName, jsonObject) -> {
            File file = new File(targetPath, fileName);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonObject.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unloadRecipe(Map map){
        if (map == null) return;
        Map<String, JsonObject> files = (Map<String, JsonObject>) map;
        files.forEach((fileName, jsonObject) -> {
            File file = new File(targetPath, fileName);
            if (file.exists()) {
                file.delete();
            }
        });
    }

    @Override
    public Map<String, JsonObject> readRecipeFiles(String folderName) {
        Map<String, JsonObject> fileMap = new HashMap<>();
        String basePath = "assets/carpetajiaddition/RecipesTweak/" + folderName;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resourceUrl = classLoader.getResource(basePath);
            if (resourceUrl == null) return null;
            if (!resourceUrl.getProtocol().equals("jar")) return null;
            String jarUrl = resourceUrl.getPath();
            jarUrl = URLDecoder.decode(jarUrl, StandardCharsets.UTF_8);
            String jarPath = jarUrl.substring(5, jarUrl.indexOf("!"));
            try (JarFile jarFile = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(basePath + "/") && !entry.isDirectory()) {
                        String fileName = entryName.substring(entryName.lastIndexOf("/") + 1);
                        String relativePath = entryName.substring(basePath.length() + 1);
                        if (!relativePath.contains("/")) {
                            try (InputStream inputStream = classLoader.getResourceAsStream(entryName);
                                 InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(inputStream))) {
                                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                                fileMap.put(fileName, jsonObject);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileMap;
    }
}
