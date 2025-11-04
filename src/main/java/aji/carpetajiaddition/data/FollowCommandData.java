package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionModEntryPoint;
import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.commands.FollowCommand;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FollowCommandData implements Data {
    public static final String DATA_NAME = "followCommand";

    private Set<Item> followItems = new HashSet<>();
    private Formatting color = Formatting.BLUE;

    @Override
    public void save(JsonWriter writer) {
        try {
            writer.name(DATA_NAME);
            writer.beginObject();

            writer.name("followItems");
            writer.beginArray();
            for (Item item : followItems) {
                writer.value(Item.getRawId(item));
            }
            writer.endArray();

            writer.name("color").value(color.getColorIndex());

            writer.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(JsonObject object) {
        object = object.get(DATA_NAME).getAsJsonObject();
        object.get("followItems").getAsJsonArray().forEach(JsonItem -> {
            followItems.add(Item.byRawId(JsonItem.getAsInt()));
        });
        color = Formatting.byColorIndex(object.get("color").getAsInt());
    }

    public Set<Item> getFollowItems() {
        return followItems;
    }

    public boolean addToFollowItems(Item Item) {
        boolean bl = followItems.add(Item);
        FollowCommand.data = this;
        return bl;
    }

    public boolean removeFromFollowItems(Item Item) {
        boolean bl = followItems.remove(Item);
        FollowCommand.data = this;
        return bl;
    }

    public Formatting getColor() {
        return color;
    }

    public void setColor(Formatting color) {
        this.color = color;
        CarpetAjiAdditionSettings.minecraftServer.getScoreboard().getTeam("followItems").setColor(color);
        FollowCommand.data = this;
    }
}
