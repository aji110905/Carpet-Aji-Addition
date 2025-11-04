package aji.carpetajiaddition.config;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class ConfigManager {
    private final File file;
    private static final Set<Config> ALL_CONFIG = Set.of();

    public ConfigManager(File file) {
        this.file = file;
        if (!file.exists()) saveConfig();
    }

    public void saveConfig(){
        try {
            file.createNewFile();
            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.beginObject();
            for (Config config : ALL_CONFIG) {
                config.save(writer);
            }
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to save the config file", e);
        }
    }

    public void loadConfig(){
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            reader.beginObject();
            for (Config config : ALL_CONFIG){
                config.load(reader);
            }
            reader.endObject();
            reader.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to load the config file", e);
        }
    }

    public Config getConfig(String configName){
        for (Config config : ALL_CONFIG){
            if (config.getName().equals(configName)) return config;
        }
        throw new RuntimeException("Config not found");
    }
}
