package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionModEntryPoint;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class DataManager {
    private Path path;
    private final Map<String, Data> All_DATA = Map.of(
            FollowCommandData.DATA_NAME, new FollowCommandData()
    );

    public DataManager(Path path) {
        try {
            path.toFile().mkdirs();
            this.path = path.getParent().resolve("data/carpetajiaddition.dat.json");
            if(this.path.toFile().createNewFile()){
                saveData();
            }
            loadData();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to initialize the data file", e);
        }
    }

    public void saveData(){
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(path.toFile()));
            writer.setIndent("  ");
            writer.beginObject();
            for(Data data : All_DATA.values()){
                data.save(writer);
            }
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to save the data file", e);
        }
    }

    public void loadData(){
        try {
            JsonReader reader = new JsonReader(new FileReader(path.toFile()));
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            for(Data data : All_DATA.values()){
                data.load(jsonObject);
            }
            reader.close();
        }catch (IOException e){
            CarpetAjiAdditionSettings.LOGGER.error("Failed to load the data file", e);
        }
    }

    public Data getData(String DataName){
        for (Map.Entry<String, Data> entry : All_DATA.entrySet()) {
            if (entry.getKey().equals(DataName)) {
                return entry.getValue();
            }
        }
        return null;
    }
}