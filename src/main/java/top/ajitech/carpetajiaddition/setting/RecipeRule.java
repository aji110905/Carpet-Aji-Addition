package top.ajitech.carpetajiaddition.setting;

import top.ajitech.carpetajiaddition.CarpetAjiAdditionServer;
import top.ajitech.carpetajiaddition.recipe.Recipe;

import java.util.List;
import java.util.Map;

public class RecipeRule extends BooleanRule{
    private final Recipe recipe;

    public RecipeRule(String name, Recipe recipe) {
        super(name, List.of(RuleCategory.RECIPE), false, false);
        this.recipe = recipe;
    }

    @Override
    protected void onValueChanged(Boolean value) {
        CarpetAjiAdditionServer.getInstance().getRecipeManager().onRecipeRuleValueChanged();
    }

    //#if MC < 12102
    public void addToRecipeMap(Map<net.minecraft.resources.ResourceLocation, com.google.gson.JsonElement> map) {
    //#elseif MC < 260300
    //$$ public void addToRecipeMap(Map<net.minecraft.resources.ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> map, net.minecraft.core.HolderLookup.Provider provider) {
    //#else
    //$$ public void addToRecipeMap(net.minecraft.server.packs.PackResources packResources, Map<net.minecraft.resources.Identifier, net.minecraft.server.packs.resources.Resource> map, net.minecraft.resources.FileToIdConverter converter) {
    //#endif
        if (value()) {
            //#if MC < 12102
            recipe.addToRecipeMap(map);
            //#elseif MC < 260300
            //$$ recipe.addToRecipeMap(map, provider);
            //#else
            //$$ recipe.addToRecipeMap(packResources, map, converter);
            //#endif
        }
    }
}
