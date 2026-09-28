package top.ajitech.carpetajiaddition.recipe;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;

import java.util.Map;

public interface Recipe {
    void addToRecipeMap(PackResources packResources, Map<Identifier, Resource> map, FileToIdConverter converter);
}
