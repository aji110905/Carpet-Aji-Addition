package aji.carpetajiaddition.data;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public interface Data {
    void save(JsonWriter writer);

    void load(JsonObject object);
}
