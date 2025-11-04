package aji.carpetajiaddition;

import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;

public class CarpetAjiAdditionModEntryPoint implements ModInitializer {
	@Override
	public void onInitialize() {
        CarpetServer.manageExtension(new CarpetAjiAddition());
	}
}