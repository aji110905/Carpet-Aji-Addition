package top.ajitech.carpetajiaddition.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import top.ajitech.carpetajiaddition.util.ResourceLocationUtil;

import java.util.Map;

import static top.ajitech.carpetajiaddition.util.ResourceLocationUtil.getItemRegistryName;

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
    public void addToRecipeMap(Map<ResourceLocation, JsonElement> map) {
        JsonObject json = new JsonObject();
        json.add("type", new JsonPrimitive(TYPE));
        JsonObject ingredientJson = new JsonObject();
        ingredientJson.add("item", new JsonPrimitive(ingredient));
        json.add("ingredient", ingredientJson);
        JsonObject resultJson = new JsonObject();
        resultJson.add("id", new JsonPrimitive(result));
        resultJson.add("count", new JsonPrimitive(count));
        json.add("result", resultJson);
        map.put(id, json);
    }

    public static StonecuttingRecipe create(Item ingredient, Item result, int count) {
        return new StonecuttingRecipe(
                ResourceLocationUtil.getResourceLocation(result + "_from_" + ingredient),
                getItemRegistryName(ingredient),
                getItemRegistryName(result),
                count
        );
    }
}
