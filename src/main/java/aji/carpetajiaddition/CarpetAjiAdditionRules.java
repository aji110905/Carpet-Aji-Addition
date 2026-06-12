package aji.carpetajiaddition;

import aji.carpetajiaddition.constant.ModConstants;
import aji.carpetajiaddition.annotation.MustSetDefault;
import carpet.api.settings.Rule;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static aji.carpetajiaddition.constant.RuleCategory.*;
import static carpet.api.settings.RuleCategory.*;

public class CarpetAjiAdditionRules {
    private static final Set<String> MUST_SET_DEFAULT_RULES;
    private static final Set<Field> RECIPE_RULES;

    @Rule(categories = {CAA, CREATIVE})
    public static boolean glowingHopperMinecart = false;

    @Rule(categories = {CAA, SURVIVAL, CREATIVE, FEATURE})
    public static boolean lockAllHopper = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean keepOpeningVault = false;

    @Rule(categories = {CAA, SURVIVAL, CREATIVE, FEATURE})
    public static boolean lockAllHopperMinecart = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean tameHorse = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean safeMagmaBlock = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean toughTurtleEgg = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean removeEnderPearlDamage = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean safeCreeper = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean anvilCannotDamage = false;

    @Rule(categories = {CAA})
    @MustSetDefault
    public static boolean betterLogCommand = false;

    @Rule(categories = {CAA, COMMAND})
    public static String commandFollow = "ops";

    @Rule(categories = {CAA, COMMAND})
    public static String commandMods = "0";

    @Rule(categories = {CAA, RECIPE})
    public static boolean dragonEggRecipe = false;

    @Rule(categories = {CAA, RECIPE})
    public static boolean dragonBreathRecipe = false;

    public static boolean hasEnabledRecipeRule(){
        for (Field recipeRule : RECIPE_RULES) {
            try {
                if (recipeRule.getBoolean(null)){
                    return true;
                }
            } catch (IllegalAccessException e) {
                ModConstants.LOGGER.error("Failed to get value of rule {}", recipeRule.getName(), e);
            }
        }
        return false;
    }

    public static boolean isMustSetDefaultRule(String ruleName){
        return MUST_SET_DEFAULT_RULES.contains(ruleName);
    }

    static {
        MUST_SET_DEFAULT_RULES = new HashSet<>();
        for (Field field : CarpetAjiAdditionRules.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(MustSetDefault.class)){
                MUST_SET_DEFAULT_RULES.add(field.getName());
            }
        }

        RECIPE_RULES = new HashSet<>();
        for (Field field : CarpetAjiAdditionRules.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Rule.class)){
                continue;
            }
            if (Arrays.asList(field.getAnnotation(Rule.class).categories()).contains(RECIPE)){
                RECIPE_RULES.add(field);
            }
        }
    }
}