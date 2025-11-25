package aji.carpetajiaddition.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public interface Config {
    String name();

    void initConfigFile(JsonWriter writer);

    void load(JsonObject object);
}
