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

public class OreRecipeRecipeRuleValidator<T> extends Validator<T> implements RecipeRule{
    private String targetPath;
    private Map<String, Map<String, JsonObject>> recipeFiles;

    @Override
    public T validate(@Nullable ServerCommandSource source, CarpetRule<T> changingRule, T newValue, String userInput) {
        targetPath = CarpetAjiAdditionMod.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "\\CarpetAjiAdditionData\\data\\carpetajiaddition\\recipe";
        recipeFiles = readRecipeFiles(changingRule.name());
        if (isValid(newValue)) {
            unloadRecipe(recipeFiles);
            if ("null".equals(newValue)){
            }else if("deepslate".equals(newValue)){
                loadDeepslateRecipe();
            }else if("nether".equals(newValue)){
                loadNetherRecipe();
            }else if("ore".equals(newValue)){
                loadOreRecipe();
            }else if("ore_and_deepslate".equals(newValue)){
                loadDeepslateRecipe();
                loadOreRecipe();
            }else if("deepslate_and_nether".equals(newValue)){
                loadDeepslateRecipe();
                loadNetherRecipe();
            }else if("ore_and_nether".equals(newValue)){
                loadNetherRecipe();
                loadOreRecipe();
            }else if("all".equals(newValue)){
                loadDeepslateRecipe();
                loadNetherRecipe();
                loadOreRecipe();
            }
            if (source != null && source.getWorld() != null) CarpetAjiAdditionMod.minecraftServer.reloadResources(CarpetAjiAdditionMod.minecraftServer.getDataPackManager().getEnabledIds());
            return newValue;
        }else{
            return changingRule.value();
        }
    }

    private static <T> boolean isValid(T newValue) {
        String[] rightValues = {"null", "ore", "deepslate", "nether", "ore_and_deepslate", "deepslate_and_nether", "ore_and_nether", "all"};
        for (String value : rightValues) {
            if (value.equals(newValue)) {
                return true;
            }
        }
        return false;
    }

    public void loadOreRecipe() {
        loadRecipe(recipeFiles.get("ore"));
    }

    public void loadDeepslateRecipe() {
        loadRecipe(recipeFiles.get("deepslate"));
    }

    public void loadNetherRecipe() {
        loadRecipe(recipeFiles.get("nether"));
    }

    @Override
    public void loadRecipe(Map map) {
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
    public void unloadRecipe(Map map) {
        if (map == null) return;
        Map<String, Map<String, JsonObject>> files = (Map<String, Map<String, JsonObject>>) map;
        files.forEach((filename, file) -> {
            file.forEach((fileName, jsonObject) -> {
                File file1 = new File(targetPath, fileName);
                if (file1.exists()) {
                    file1.delete();
                }
            });
        });
    }

    @Override
    public Map<String, Map<String, JsonObject>> readRecipeFiles(String folderName) {
        Map<String, Map<String, JsonObject>> map = new HashMap<>();
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
                        String relativePath = entryName.substring(basePath.length() + 1);
                        String[] pathParts = relativePath.split("/");
                        if (pathParts.length >= 2) {
                            String dirName = pathParts[0];
                            String fileName = String.join("/", Arrays.copyOfRange(pathParts, 1, pathParts.length));
                            try (InputStream inputStream = classLoader.getResourceAsStream(entryName);
                                 InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(inputStream))) {
                                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                                map.computeIfAbsent(dirName, k -> new HashMap<>())
                                        .put(fileName, jsonObject);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
