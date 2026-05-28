package aji.carpetajiaddition.util;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ResourceLocationUtil {
    private ResourceLocationUtil(){

    }

    public static ResourceLocation getResourceLocation(String name){
        return ResourceLocation.fromNamespaceAndPath(CarpetAjiAdditionSettings.MOD_ID, name);
    }

    public static String getItemRegistryName(Item item){
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }
}
