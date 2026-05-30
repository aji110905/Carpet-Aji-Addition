package aji.carpetajiaddition.recipe;

import net.minecraft.world.item.Item;

public abstract class RecipeBuilder {
    protected final boolean enabled;
    protected final String recipeName;
    protected Item resultItem;
    protected int resultCount;
    protected boolean isSetResult = false;

    protected RecipeBuilder(boolean enabled, String recipeName) {
        this.enabled = enabled;
        this.recipeName = recipeName;
    }

    public RecipeBuilder output(Item item, int count) {
        isSetResult = true;
        resultItem = item;
        resultCount = count;
        return this;
    }

    public abstract Recipe build();
}
