package aji.carpetajiaddition;

import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.settings.MustSetDefault;
import aji.carpetajiaddition.validators.observer.RecipeRuleObserve;
import carpet.api.settings.Rule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static aji.carpetajiaddition.settings.RuleCategory.*;
import static carpet.api.settings.RuleCategory.*;

public class CarpetAjiAdditionSettings {
    public static final String MOD_ID = "carpetajiaddition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().toString();
    public static final CarpetAjiAdditionExtension EXTENSION = new CarpetAjiAdditionExtension();
    public static final Set<String> MUST_SET_DEFAULT_RULES = new HashSet<>();
    public static MinecraftServer minecraftServer = null;
    public static DataManager data = null;

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

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean toughTurtleEgg = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean removeEnderPearlDamage = false;

    @Rule(categories = {CAA})
    @MustSetDefault
    public static boolean betterLogCommand = false;

    @Rule(categories = {CAA, COMMAND})
    public static String commandFollow = "ops";

    @Rule(categories = {CAA, COMMAND})
    public static String commandMods = "0";

    @Rule(categories = {CAA, RECIPE}, validators = RecipeRuleObserve.class)
    public static boolean dragonEggRecipe = false;

    @Rule(categories = {CAA, RECIPE}, validators = RecipeRuleObserve.class)
    public static boolean dragonBreathRecipe = false;

    static {
        for (Field field : CarpetAjiAdditionSettings.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(MustSetDefault.class)){
                MUST_SET_DEFAULT_RULES.add(field.getName());
            }
        }
    }
}