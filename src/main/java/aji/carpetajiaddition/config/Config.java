package aji.carpetajiaddition.config;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface Config {
    String name();

    void initConfigFile(JsonWriter writer) throws IOException;

    void load(JsonElement element);
}
