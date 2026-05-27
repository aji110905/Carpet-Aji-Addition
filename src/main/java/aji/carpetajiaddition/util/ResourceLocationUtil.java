package aji.carpetajiaddition.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class ResourceLocationUtil {
    private ResourceLocationUtil(){

    }

    public static String getItemRegistryName(Item item){
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }
}
