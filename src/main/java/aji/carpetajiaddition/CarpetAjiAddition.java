package aji.carpetajiaddition;

import aji.carpetajiaddition.commands.FollowCommand;
import aji.carpetajiaddition.commands.ModsCommand;
import aji.carpetajiaddition.data.DataManager;
import aji.carpetajiaddition.settings.RecipeRule;
import aji.carpetajiaddition.translations.CarpetAjiAdditionTranslation;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
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
        CarpetAjiAdditionTranslation.addMachineFlipRuleToSettingManager();
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        CarpetAjiAdditionSettings.data = new DataManager(CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.ROOT).getParent().resolve("data/carpetajiaddition.dat"));
        FollowCommand.init();
        CarpetAjiAdditionSettings.minecraftServer.getPackRepository().reload();
        CarpetAjiAdditionSettings.minecraftServer.getPackRepository().addPack("file/CarpetAjiAdditionData");
        RecipeRule.PATH = CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR).resolve("CarpetAjiAdditionData/data/" + CarpetAjiAdditionSettings.MOD_ID + "/recipe").toString();
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext commandBuildContext) {
        FollowCommand.register(dispatcher, commandBuildContext);
        ModsCommand.register(dispatcher, commandBuildContext);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        cleanDataPack();
    }

    @Override
    public String version() {
        return CarpetAjiAdditionSettings.MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return CarpetAjiAdditionTranslation.getFabricCarpetTranslations(lang);
    }

    public static void initializationDataPack() {
        cleanDataPack();
        File file = new File(CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR) + "/CarpetAjiAdditionData/pack.mcmeta");
        file.getParentFile().mkdirs();
        try {
            InputStream stream = CarpetAjiAddition.class.getClassLoader().getResourceAsStream("assets/carpetajiaddition/pack.mcmeta.json");
            Files.write(file.toPath(), stream.readAllBytes());
            stream.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Initializing data pack failed", e);
        }
    }

    public static void cleanDataPack() {
        File file = new File(CarpetAjiAdditionSettings.minecraftServer.getWorldPath(LevelResource.DATAPACK_DIR) + "/CarpetAjiAdditionData");
        if (file.exists()) {
            try {
                Files.walk(file.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Failed to clean up data pack residual data", e);
            }
        }
    }
}