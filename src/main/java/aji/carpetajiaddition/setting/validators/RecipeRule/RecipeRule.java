package aji.carpetajiaddition.setting.validators.RecipeRule;

import java.util.Map;

public interface RecipeRule {
    Map readRecipeFiles(String folderName);

    void loadRecipe(Map map);

    void unloadRecipe(Map map);
}
