package aji.carpetajiaddition.config;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class ConfigManager {
    private final Path path;
    private final Set<Config> All_CONFIG = Set.of(

    );

    public ConfigManager(Path path){
        this.path = path;
        if (!path.toFile().exists()){
            try {
                JsonWriter writer = new JsonWriter(new FileWriter(path.toFile()));
                writer.setIndent("  ");
                writer.beginObject();
                for (Config config : All_CONFIG) {
                    config.initConfigFile(writer);
                }
                writer.endObject();
                writer.close();
            } catch (IOException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Failed to initialization the config file", e);
            }
        }
        loadConfig();
    }

    public void loadConfig(){
        try {
            JsonReader reader = new JsonReader(new FileReader(path.toFile()));
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            for (Config config : All_CONFIG) {
                config.load(jsonObject.getAsJsonObject(config.name()));
            }
            reader.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to loadConfig the config file", e);
        }
    }

    public Config getConfig(String configName){
        for (Config config : All_CONFIG) {
            if (config.name().equals(configName)){
                return config;
            }
        }
        throw new IllegalArgumentException("No config found with name " + configName);
    }
}
