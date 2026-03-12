package aji.carpetajiaddition;

import aji.carpetajiaddition.commands.FollowCommand;
import aji.carpetajiaddition.commands.ModsCommand;
import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.settings.RecipeRule;
import aji.carpetajiaddition.util.translations.TranslationUtil;
import aji.carpetajiaddition.util.DataPackUtil;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public class CarpetAjiAddition implements CarpetExtension {
    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAjiAdditionSettings.class);
        RecipeRule.addRecipeRulesToSettingManager();
        TranslationUtil.addMachineFlipRuleToSettingManager();
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        CarpetAjiAdditionSettings.data = new DataManager(CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.ROOT).getParent().resolve("data/carpetajiaddition.dat"));
        FollowCommand.init();
        PackRepository packRepository = CarpetAjiAdditionSettings.minecraftServer.getPackRepository();
        packRepository.reload();
        packRepository.addPack("file/CarpetAjiAdditionData");
        RecipeRule.PATH = DataPackUtil.getDataPackPath().resolve("data/" + CarpetAjiAdditionSettings.MOD_ID + "/recipe").toString();
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
        ModsCommand.register(dispatcher, commandBuildContext);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        DataPackUtil.cleanDataPack();
    }

    @Override
    public String version() {
        return CarpetAjiAdditionSettings.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return TranslationUtil.getFabricCarpetTranslations(lang);
    }
}