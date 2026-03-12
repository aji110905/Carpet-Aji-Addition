package aji.carpetajiaddition.util;

import aji.carpetajiaddition.CarpetAjiAddition;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class DataPackUtil {
    private DataPackUtil(){
    }

    public static Path getDataPackPath(){
        return CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR).resolve("/CarpetAjiAdditionData/");
    }

    public static void initializationDataPack() {
        cleanDataPack();
        File file = getDataPackPath().resolve("pack.mcmeta").toFile();
        file.getParentFile().mkdirs();
        try {
            InputStream stream = CarpetAjiAddition.class.getClassLoader().getResourceAsStream("assets/carpetajiaddition/pack.mcmeta.json");
            Files.write(file.toPath(), stream.readAllBytes());
            stream.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Initializing data pack failed", e);
        }
    }

    public static void cleanDataPack() {
        File file = getDataPackPath().toFile();
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
