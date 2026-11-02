package top.ajitech.carpetajiaddition.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getItemRegistryName;
import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getResourceLocation;

public class StonecuttingRecipe implements Recipe {
    private static final String TYPE = "minecraft:stonecutting";

    private final Identifier id;
    private final String ingredient;
    private final String result;
    private final int count;

    private StonecuttingRecipe(Identifier id, String ingredient, String result, int count) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.count = count;
    }

    @Override
    public void addToRecipeMap(PackResources packResources, Map<Identifier, Resource> map, FileToIdConverter converter) {
        JsonObject json = new JsonObject();
        json.add("type", new JsonPrimitive(TYPE));
        json.add("ingredient", new JsonPrimitive(ingredient));
        JsonObject resultJson = new JsonObject();
        resultJson.add("id", new JsonPrimitive(result));
        resultJson.add("count", new JsonPrimitive(count));
        json.add("result", resultJson);
        map.put(converter.idToFile(id), new Resource(packResources, () -> new ByteArrayInputStream(json.toString().getBytes())));
    }

    public static StonecuttingRecipe create(String name, Item ingredient, Item result, int count) {
        return new StonecuttingRecipe(getResourceLocation(name), getItemRegistryName(ingredient), getItemRegistryName(result), count);
    }
}
