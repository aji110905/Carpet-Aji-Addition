package aji.carpetajiaddition;

import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;

public class CarpetAjiAdditionMod implements ModInitializer {
	@Override
	public void onInitialize() {
        CarpetServer.manageExtension(CarpetAjiAdditionSettings.EXTENSION);
	}
}