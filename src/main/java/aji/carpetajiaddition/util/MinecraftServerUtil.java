package aji.carpetajiaddition.util;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;

public class MinecraftServerUtil {
    private MinecraftServerUtil(){
    }

    public static boolean serverIsRunning(){
        return CarpetAjiAdditionSettings.minecraftServer != null && CarpetAjiAdditionSettings.minecraftServer.isRunning();
    }
}
