package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class DataManager {
    private final Path path;
    private final Set<Data> dataSet = Set.of(
            new FollowCommandData(),
            new BetterLogCommandData()
    );

    public DataManager(Path path) {
        this.path = path;
        File file = path.toFile();
        if(!file.exists()){
            try {
                Path parent = path.getParent();
                if(!parent.toFile().exists()){
                    Files.createDirectories(parent);
                }
                file.createNewFile();
            } catch (IOException e) {
                CarpetAjiAdditionSettings.LOGGER.error("Failed to create data file", e);
            }
            saveData();
        }
        loadData();
    }

    public void saveData(){
        CompoundTag compound = new CompoundTag();
        for (Data data : dataSet) {
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
            CompoundTag compound = NbtIo.read(path);
            for (Data data : dataSet) {
                data.load(compound.get(data.name()));
            }
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to load data", e);
        }
    }

    public Data getData(String dataName){
        for (Data data : dataSet) {
            if (data.name().equals(dataName)){
                return data;
            }
        }
        throw new IllegalArgumentException("No data found with name " + dataName);
    }
}