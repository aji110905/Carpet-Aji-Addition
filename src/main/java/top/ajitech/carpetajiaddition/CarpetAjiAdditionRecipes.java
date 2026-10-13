package top.ajitech.carpetajiaddition;

import top.ajitech.carpetajiaddition.recipe.Recipe;
import top.ajitech.carpetajiaddition.recipe.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.item.Items.DRAGON_BREATH;
import static net.minecraft.world.item.Items.DRAGON_EGG;
import static net.minecraft.world.item.Items.EGG;
import static net.minecraft.world.item.Items.END_CRYSTAL;
import static net.minecraft.world.item.Items.GLASS_BOTTLE;
import static net.minecraft.world.item.Items.OBSIDIAN;

public class CarpetAjiAdditionRecipes {
    public static final CarpetAjiAdditionRecipes INSTANCE = new CarpetAjiAdditionRecipes();

    public List<Recipe> getRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        if (CarpetAjiAdditionRules.dragonEggRecipe) {
            ShapedRecipe recipe = ShapedRecipe.builder("dragon_egg", DRAGON_EGG, 1)
                    .pattern("&#&")
                    .pattern("^*^")
                    .pattern("$$$")
                    .define('&', CRYING_OBSIDIAN).define('#', GLASS_BOTTLE).define('^', OBSIDIAN).define('*', EGG).define('$', END_CRYSTAL)
                    .build();
            recipes.add(recipe);
        }
        if (CarpetAjiAdditionRules.dragonBreathRecipe) {
            ShapedRecipe recipe = ShapedRecipe.builder("dragon_breath", DRAGON_BREATH, 1)
                    .pattern("#")
                    .pattern("*")
                    .define('#', DRAGON_EGG).define('*', GLASS_BOTTLE)
                    .build();
            recipes.add(recipe);
        }
        return recipes;
    }
}
