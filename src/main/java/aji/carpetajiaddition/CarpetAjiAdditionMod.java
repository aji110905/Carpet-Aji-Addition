package aji.carpetajiaddition;

import aji.carpetajiaddition.data.DataManager;
import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetAjiAdditionMod implements ModInitializer {
	public static final String MOD_ID = "carpetajiaddition";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().toString();
    public static MinecraftServer minecraftServer = null;
    public static DataManager data = null;

	@Override
	public void onInitialize() {
        CarpetServer.manageExtension(new CarpetAjiAdditionExtension());
	}
}