package aji.carpetajiaddition.settings;

import aji.carpetajiaddition.settings.validators.RecipeRuleValidator;
import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.InvalidRuleValueException;
import carpet.api.settings.SettingsManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public class RecipeRule implements CarpetRule<Boolean> {
    private static final RecipeRuleValidator<Boolean> VALIDATOR = new RecipeRuleValidator<>();
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
    public List<Text> extraInfo() {
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
    public void set(ServerCommandSource source, String value) throws InvalidRuleValueException {
        if (value.equals("true")) set(source, true);
        else if (value.equals("false")) set(source, false);
        else throw new InvalidRuleValueException("Invalid boolean value");
    }

    @Override
    public void set(ServerCommandSource source, Boolean value) throws InvalidRuleValueException {
        Boolean bl = VALIDATOR.validate(source, this, value, value.toString());
        if (bl != null) this.value = bl;
        else throw new InvalidRuleValueException("Invalid boolean value");
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
