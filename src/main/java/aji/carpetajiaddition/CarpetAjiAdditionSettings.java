package aji.carpetajiaddition;

import aji.carpetajiaddition.validators.RecipeRule.OreRecipeRecipeRuleValidator;
import aji.carpetajiaddition.validators.RecipeRule.RecipeRuleValidator;
import carpet.api.settings.Rule;

import static aji.carpetajiaddition.setting.RuleCategory.CAA;
import static aji.carpetajiaddition.setting.RuleCategory.RECIPE;
import static carpet.api.settings.RuleCategory.*;

public class CarpetAjiAdditionSettings {

    @Rule(categories = {CAA, CREATIVE})
    public static boolean glowingHopperMinecart = false;

    @Rule(categories = {CAA, SURVIVAL})
    public static boolean sitOnTheGround = false;

    @Rule(categories = {CAA, FEATURE})
    public static boolean lockAllHopper = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean keepOpeningVault = false;

    @Rule(categories = {CAA, FEATURE})
    public static boolean lockAllHopperMinecart = false;

    @Rule(categories = {CAA, FEATURE})
    public static boolean totemOfUndyingWrench = false;

    @Rule(categories = {CAA, SURVIVAL, FEATURE})
    public static boolean tameHorse = false;

    /*
     * 配方规则
     * validators为{aji.carpetajiaddition.validators.RecipeRule.RecipeRule}的实现类的规则为配方规则
     */

    @Rule(
            categories = {CAA, RECIPE},
            validators = RecipeRuleValidator.class
    )
    public static boolean dragonEggRecipe = false;

    @Rule(
            categories = {CAA, RECIPE},
            validators = OreRecipeRecipeRuleValidator.class,
            options = {
                    "null",
                    "ore",
                    "deepslate",
                    "nether",
                    "ore_and_deepslate",
                    "deepslate_and_nether",
                    "ore_and_nether",
                    "ore_and_nether",
                    "all"
            }
    )
    public static String oreRecipe = "null";

    @Rule(
            categories = {CAA, RECIPE},
            validators = RecipeRuleValidator.class
    )
    public static boolean dragonBreathRecipe = false;
}