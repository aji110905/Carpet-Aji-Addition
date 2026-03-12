package aji.carpetajiaddition.util;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;

public class DataPackUtil {
    private DataPackUtil(){
    }

    public static Path getDataPackPath(){
        return CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR).resolve("/CarpetAjiAdditionData/");
    }
}
