package aji.carpetajiaddition.config;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.notice.Notice;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class NoticeConfig implements Config{
    public static final String CONFIG_NAME = "notice";

    private boolean general = false;
    private boolean sendOrder = false;
    private Notice entrant = new Notice(null);
    private Notice others = new Notice(null);

    @Override
    public String name() {
        return CONFIG_NAME;
    }

    @Override
    public void initConfigFile(JsonWriter writer) {
        try {
            writer.name(CONFIG_NAME);
            writer.beginObject();
            writer.name("general").value(false);
            writer.name("send_order").value(false);
            writer.name("info");
            writer.beginObject();
            writer.name("entrant").nullValue();
            writer.name("others").nullValue();
            writer.endObject();
            writer.endObject();
        } catch (IOException e) {
            CarpetAjiAdditionSettings.LOGGER.error("Failed to initialization the config file", e);
        }
    }

    @Override
    public void load(JsonObject object) {
        general = object.get("general").getAsBoolean();
        sendOrder = object.get("send_order").getAsBoolean();
        JsonObject info = object.getAsJsonObject("info");
        entrant = new Notice(info.get("entrant"));
        others = new Notice(info.get("others"));
    }

    public boolean isGeneral() {
        return general;
    }

    public Notice getEntrant() {
        return entrant;
    }

    public Notice getOthers() {
        return others;
    }

    public boolean isSendOrder() {
        return sendOrder;
    }
}
