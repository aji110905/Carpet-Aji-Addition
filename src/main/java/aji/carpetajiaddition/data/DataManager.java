package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.nio.file.Path;
import java.util.Set;

public class DataManager {
    private final Path path;
    private final Set<Data> All_DATA = Set.of(
            new FollowCommandData()
    );

    public DataManager(Path path) {
        this.path = path;
        if(!this.path.toFile().exists()){
            saveData();
        }
        loadData();
    }

    public void saveData(){
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(path.toFile()));
            writer.setIndent("  ");
            writer.beginObject();
            for(Data data : All_DATA){
                data.save(writer);
            }
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to saveConfig the data file", e);
        }
    }

    public void loadData(){
        try {
            JsonReader reader = new JsonReader(new FileReader(path.toFile()));
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            for(Data data : All_DATA){
                data.load(jsonObject.getAsJsonObject(data.name()));
            }
            reader.close();
        }catch (IOException e){
            CarpetAjiAdditionSettings.LOGGER.error("Failed to loadConfig the data file", e);
        }
    }

    public Data getData(String dataName){
        for (Data data : All_DATA) {
            if (data.name().equals(dataName)){
                return data;
            }
        }
        throw new IllegalArgumentException("No data found with name " + dataName);
    }
}