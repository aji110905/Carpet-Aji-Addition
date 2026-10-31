package top.ajitech.carpetajiaddition;

import carpet.CarpetServer;
import net.fabricmc.loader.api.FabricLoader;
import top.ajitech.carpetajiaddition.recipe.Recipe;
import top.ajitech.carpetajiaddition.recipe.ShapedRecipe;
import top.ajitech.carpetajiaddition.setting.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.world.item.Items.*;

public final class CarpetAjiAdditionRules {
    private CarpetAjiAdditionRules() {
    }

    private static final Set<Rule<?>> RULES = new HashSet<>();



    public static final BooleanRule GLOWING_HOPPER_MINECART = register("glowingHopperMinecart", false);
    public static final BooleanRule LOCK_ALL_HOPPER = register("lockAllHopper", false);
    public static final BooleanRule KEEP_OPENING_VAULT = register("keepOpeningVault", false);
    public static final BooleanRule LOCK_ALL_HOPPER_MINECART = register("lockAllHopperMinecart", false);
    public static final BooleanRule TAME_HORSE = register("tameHorse", false);
    public static final BooleanRule SAFE_MAGMA_BLOCK = register("safeMagmaBlock", false);
    public static final BooleanRule TOUGH_TURTLE_EGG = register("toughTurtleEgg", false);
    public static final BooleanRule REMOVE_ENDER_PEARL_DAMAGE = register("removeEnderPearlDamage", false);
    public static final BooleanRule BETTER_LOG_COMMAND = register("betterLogCommand", false, true);
    public static final BooleanRule SAFE_CREEPER = register("safeCreeper", false);
    public static final BooleanRule ANVIL_CANNOT_DAMAGE = register(!FabricLoader.getInstance().isModLoaded("carpet_cuo_addition"), "anvilCannotDamage", false);
    public static final BooleanRule NO_SNOW_GOLEM_MELTING = register("noSnowGolemMelting", false);
    public static final BooleanRule GRINDSTONE_DISENCHANTING = register("grindstoneDisenchanting", false);
    public static final BooleanRule ENHANCE_GRINDSTONE = register("enhanceGrindstone", false);
    public static final BooleanRule REMOVE_TOO_EXPENSIVE = register("removeTooExpensive", false);


    private static BooleanRule register(String name, boolean defaultValue) {
        return register(true, name, defaultValue);
    }

    private static BooleanRule register(boolean shouldRegister, String name, boolean defaultValue) {
        return register(shouldRegister, name, List.of(), defaultValue, false);
    }

    private static BooleanRule register(String name, boolean defaultValue, boolean mustSetDefault) {
        return register(true, name, defaultValue, mustSetDefault);
    }

    private static BooleanRule register(boolean shouldRegister, String name, boolean defaultValue, boolean mustSetDefault) {
        return register(shouldRegister, name, List.of(), defaultValue, mustSetDefault);
    }

    private static BooleanRule register(boolean shouldRegister, String name, Collection<String> categories, boolean defaultValue, boolean isMustSetDefault) {
        BooleanRule rule = new BooleanRule(name,  categories, defaultValue, isMustSetDefault);
        if (shouldRegister) {
            RULES.add(rule);
        }
        return rule;
    }



    public static final CommandRule COMMAND_FOLLOW = register("commandFollow", CommandRuleValue.OPS);
    public static final CommandRule COMMAND_MODS = register("commandMods", CommandRuleValue.TRUE);
    public static final CommandRule COMMAND_PLAYERDATA = register("commandPlayerdata", CommandRuleValue.OPS);

    private static CommandRule register(String name, CommandRuleValue defaultValue) {
        CommandRule rule = new CommandRule(name, defaultValue);
        RULES.add(rule);
        return rule;
    }



    public static final Set<RecipeRule> RECIPE_RULES = new HashSet<>();

    static {
        register(
                "dragonEggRecipe",
                ShapedRecipe.builder("dragon_egg", DRAGON_EGG, 1)
                        .pattern("&#&")
                        .pattern("^*^")
                        .pattern("$$$")
                        .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                        .build()
        );
        register(
                "dragonBreathRecipe",
                ShapedRecipe.builder("dragon_breath", DRAGON_BREATH, 1)
                        .pattern("#")
                        .pattern("*")
                        .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                        .build()
        );
        register(
                "elytraRecipe",
                ShapedRecipe.builder("elytra", ELYTRA, 1)
                        .pattern("###")
                        .pattern("*&*")
                        .pattern("*&*")
                        .define('#', DRAGON_EGG).define('*', PHANTOM_MEMBRANE).define('&', END_ROD)
                        .build()
        );
    }

    private static void register(String name, Recipe recipe) {
        RecipeRule rule = new RecipeRule(name, recipe);
        RECIPE_RULES.add(rule);
    }



    public static void register() {
        RECIPE_RULES.forEach(rule -> CarpetServer.settingsManager.addCarpetRule(rule));
        RULES.forEach(rule -> CarpetServer.settingsManager.addCarpetRule(rule));
    }
}