package aji.carpetajiaddition;

import aji.carpetajiaddition.config.ConfigManager;
import aji.carpetajiaddition.data.DataManager;
import carpet.api.settings.Rule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static aji.carpetajiaddition.setting.RuleCategory.*;
import static carpet.api.settings.RuleCategory.*;

public class CarpetAjiAdditionSettings {
    public static final String MOD_ID = "carpetajiaddition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().toString();
    public static MinecraftServer minecraftServer = null;
    public static DataManager data = null;
    public static ConfigManager config = null;

    @Rule(categories = {CAA, FEATURE})
    public static boolean useMachineFlip = true;

    @Rule(categories = {CAA, CREATIVE})
    public static boolean glowingHopperMinecart = false;

    @Rule(categories = {CAA, SURVIVAL})
    public static boolean sitOnTheGround = false;

    @Rule(categories = {CAA, SURVIVAL, CREATIVE, FEATURE})
    public static boolean lockAllHopper = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean keepOpeningVault = false;

    @Rule(categories = {CAA, SURVIVAL, CREATIVE, FEATURE})
    public static boolean lockAllHopperMinecart = false;

    @Rule(categories = {CAA, SURVIVAL, CREATIVE, FEATURE})
    public static boolean cactusWrench = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean tameHorse = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean safeMagmaBlock = false;

    @Rule(categories = {CAA, COMMAND})
    public static String commandFollow = "ops";
}