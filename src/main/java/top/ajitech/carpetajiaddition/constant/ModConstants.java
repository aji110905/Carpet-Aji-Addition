package top.ajitech.carpetajiaddition.constant;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModConstants {
    private ModConstants() {}

    public static final String MOD_ID = "carpetajiaddition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = FabricLoader.getInstance()
            .getModContainer(MOD_ID)
            .orElseThrow()
            .getMetadata()
            .getVersion()
            .toString();
}