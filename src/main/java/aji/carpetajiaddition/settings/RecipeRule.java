package aji.carpetajiaddition.settings;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.util.IOUtil;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.SettingsManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RecipeRule implements CarpetRule<Boolean> {
    public static String PATH;
    private final String name;
    private Boolean value = false;

    public RecipeRule(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Component> extraInfo() {
        return List.of();
    }

    @Override
    public Collection<String> categories() {
        return List.of(RuleCategory.CAA, RuleCategory.RECIPE);
    }

    @Override
    public Collection<String> suggestions() {
        return List.of("true", "false");
    }

    @Override
    public SettingsManager settingsManager() {
        return CarpetServer.settingsManager;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public boolean canBeToggledClientSide() {
        return false;
    }

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }

    @Override
    public Boolean defaultValue() {
        return false;
    }

    @Override
    public boolean strict() {
        return true;
    }

    @Override
    public void set(CommandSourceStack source, String value) throws InvalidRuleValueException {
        if (value.equals("true")) set(source, true);
        else if (value.equals("false")) set(source, false);
        else throw new InvalidRuleValueException("Invalid boolean value");
    }

    @Override
    public void set(CommandSourceStack source, Boolean value) throws InvalidRuleValueException {
        if (PATH == null) return;
        try {
            Map<String, String> recipeFiles = IOUtil.readAllFilesFromResource("assets/carpetajiaddition/RecipesTweak/" + name);
            if (value != null) {
                if (value) {
                    recipeFiles.forEach((fileName, jsonFile) -> {
                        File file = new File(PATH, fileName);
                        try {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            FileWriter writer = new FileWriter(file);
                            writer.write(jsonFile);
                            writer.close();
                        } catch (IOException e) {
                            CarpetAjiAdditionSettings.LOGGER.error("Recipes cannot be loaded into data pack", e);
                        }
                    });
                } else {
                    recipeFiles.forEach((fileName, jsonFile) -> {
                        File file = new File(PATH, fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                    });
                }
                if (source != null) CarpetAjiAdditionSettings.minecraftServer.reloadResources(CarpetAjiAdditionSettings.minecraftServer.getPackRepository().getSelectedIds());
            }else{
                throw new InvalidRuleValueException("Invalid boolean value");
            }
        } catch (IOException | URISyntaxException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Recipes cannot be loaded into data pack", e);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

    public static void addRecipeRulesToSettingManager(){
        CarpetServer.settingsManager.addCarpetRule(new RecipeRule("dragonEggRecipe"));
        CarpetServer.settingsManager.addCarpetRule(new RecipeRule("dragonBreathRecipe"));
    }
}
