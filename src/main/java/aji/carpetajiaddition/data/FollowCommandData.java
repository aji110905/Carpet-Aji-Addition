package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.commands.FollowCommand;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FollowCommandData implements Data {
    public static final String DATA_NAME = "followCommand";

    private final Set<Item> followItems = new HashSet<>();
    private Formatting color = Formatting.BLUE;

    @Override
    public String name() {
        return DATA_NAME;
    }

    @Override
    public NbtElement toNbt() {
        NbtList list = new NbtList();
        for (Item followItem : followItems) {
            list.add(NbtInt.of(Item.getRawId(followItem)));
        }
        NbtCompound compound = new NbtCompound();
        compound.put("followItems", list);
        compound.put("color", NbtInt.of(color.getColorIndex()));
        return compound;
    }

    @Override
    public void load(NbtElement element) {
        followItems.clear();
        NbtCompound compound = (NbtCompound) element;
        for (NbtElement nbtElement : ((NbtList) compound.get("followItems"))) {
            followItems.add(Item.byRawId(((NbtInt) nbtElement).intValue()));
        }
        color = Formatting.byColorIndex(((NbtInt)compound.get("color")).intValue());
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
