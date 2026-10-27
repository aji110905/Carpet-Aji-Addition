package top.ajitech.carpetajiaddition.recipe;

import java.util.Map;

@FunctionalInterface
public interface Recipe {
    //#if MC < 12102
    void addToRecipeMap(Map<net.minecraft.resources.ResourceLocation, com.google.gson.JsonElement> map);
    //#elseif MC < 260300
    //$$ void addToRecipeMap(Map<net.minecraft.resources.ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> map, net.minecraft.core.HolderLookup.Provider provider);
    //#else
    //$$ void addToRecipeMap(net.minecraft.server.packs.PackResources packResources, Map<net.minecraft.resources.Identifier, net.minecraft.server.packs.resources.Resource> map, net.minecraft.resources.FileToIdConverter converter);
    //#endif
}
