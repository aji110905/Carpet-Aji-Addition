package aji.carpetajiaddition;

import aji.carpetajiaddition.setting.RecipeRule;
import carpet.api.settings.Rule;

import static aji.carpetajiaddition.setting.RuleCategory.*;
import static carpet.api.settings.RuleCategory.*;

public class CarpetAjiAdditionSettings {
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

    @RecipeRule
    public static Object dragonEggRecipe;

    @RecipeRule
    public static Object oreRecipe;

    @RecipeRule
    public static Object dragonBreathRecipe;
}