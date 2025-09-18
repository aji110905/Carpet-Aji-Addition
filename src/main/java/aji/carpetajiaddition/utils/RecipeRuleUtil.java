package aji.carpetajiaddition.utils;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class RecipeRuleUtil {
    public static void initializationRecipeRuleDate() {
        cleanRecipeRuleDate();
        File file = new File(CarpetAjiAdditionMod.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "\\CarpetAjiAdditionData\\pack.mcmeta");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            String packMetaContent = "{\n" +
                    "  \"pack\": {\n" +
                    "    \"pack_format\": 34,\n" +
                    "  }\n" +
                    "}";
            try {
                Files.write(file.toPath(), packMetaContent.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void cleanRecipeRuleDate() {
        File file = new File(CarpetAjiAdditionMod.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "\\CarpetAjiAdditionData");
        if (file.exists()) {
            try {
                Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
