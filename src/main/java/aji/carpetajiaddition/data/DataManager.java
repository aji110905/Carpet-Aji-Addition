package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;

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
            try {
                path.toFile().createNewFile();
            } catch (IOException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Failed to create data file", e);
            }
            saveData();
        }
        loadData();
    }

    public void saveData(){
        NbtCompound compound = new NbtCompound();
        for (Data data : All_DATA) {
            compound.put(data.name(), data.toNbt());
        }
        try {
            NbtIo.write(compound, path);
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to save data", e);
        }
    }

    public void loadData(){
        try {
            NbtCompound compound = NbtIo.read(path);
            for (Data data : All_DATA) {
                data.load(compound.get(data.name()));
            }
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to load data", e);
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