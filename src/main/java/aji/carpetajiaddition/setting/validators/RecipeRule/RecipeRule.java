package aji.carpetajiaddition.setting.validators.RecipeRule;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public interface RecipeRule {
    Map readRecipeFiles(String folderName);

    void loadRecipe(Map map);

    void unloadRecipe(Map map);

    static void initializationDataPack() {
        cleanDataPack();
        File file = new File(CarpetAjiAdditionMod.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "\\CarpetAjiAdditionData\\pack.mcmeta");
        file.getParentFile().mkdirs();
        try {
            Files.write(
                    file.toPath(),
                    RecipeRule.class.getClassLoader().getResourceAsStream("assets/carpetajiaddition/pack.mcmeta.json").readAllBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static void cleanDataPack() {
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
        }
    }
}
