package aji.carpetajiaddition.config;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

public interface Config {
    String name();

    void initConfigFile(JsonWriter writer);

    void load(JsonElement element);
}
