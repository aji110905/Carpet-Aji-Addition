package aji.carpetajiaddition;

import aji.carpetajiaddition.observer.RecipeRuleObserve;
import aji.carpetajiaddition.annotation.MustSetDefault;
import carpet.api.settings.Rule;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static aji.carpetajiaddition.constant.RuleCategory.*;
import static carpet.api.settings.RuleCategory.*;

public class CarpetAjiAdditionRules {
    public static final Set<String> MUST_SET_DEFAULT_RULES;

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
        MUST_SET_DEFAULT_RULES = new HashSet<>();
        for (Field field : CarpetAjiAdditionRules.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(MustSetDefault.class)){
                MUST_SET_DEFAULT_RULES.add(field.getName());
            }
        }
    }
}