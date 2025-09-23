package aji.carpetajiaddition.utils;

import aji.carpetajiaddition.CarpetAjiAdditionMod;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class DataPackUtil {
    public static void initializationDataPack() {
        cleanDataPack();
        File file = new File(CarpetAjiAdditionMod.minecraftServer.getSavePath(WorldSavePath.DATAPACKS).toString() + "\\CarpetAjiAdditionData\\pack.mcmeta");
        file.getParentFile().mkdirs();
        try {
            Files.write(
                    file.toPath(),
                    DataPackUtil.class.getClassLoader().getResourceAsStream("assets/carpetajiaddition/pack.mcmeta.json").readAllBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void cleanDataPack() {
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
