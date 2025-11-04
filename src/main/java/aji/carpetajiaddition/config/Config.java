package aji.carpetajiaddition.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface Config {
    String getName();

    void load(JsonReader reader) throws IOException;

    void save(JsonWriter writer) throws IOException;
}
