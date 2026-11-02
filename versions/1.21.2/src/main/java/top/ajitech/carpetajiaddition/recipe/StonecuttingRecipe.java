package top.ajitech.carpetajiaddition.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getItemRegistryName;
import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getResourceLocation;

public class StonecuttingRecipe implements Recipe {
    private static final String TYPE = "minecraft:stonecutting";

    private final ResourceLocation id;
    private final String ingredient;
    private final String result;
    private final int count;

    private StonecuttingRecipe(ResourceLocation id, String ingredient, String result, int count) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.count = count;
    }

    @Override
    public void addToRecipeMap(Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> map, HolderLookup.Provider provider) {
        JsonObject json = new JsonObject();
        json.add("type", new JsonPrimitive(TYPE));
        json.add("ingredient", new JsonPrimitive(ingredient));
        JsonObject resultJson = new JsonObject();
        resultJson.add("id", new JsonPrimitive(result));
        resultJson.add("count", new JsonPrimitive(count));
        json.add("result", resultJson);
        map.put(id, net.minecraft.world.item.crafting.Recipe.CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new));
    }

    public static StonecuttingRecipe create(String name, Item ingredient, Item result, int count) {
        return new StonecuttingRecipe(getResourceLocation(name), getItemRegistryName(ingredient), getItemRegistryName(result), count);
    }
}
