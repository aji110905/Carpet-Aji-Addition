package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.commands.FollowCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class FollowCommandData implements Data {
    public static final String DATA_NAME = "followCommand";

    private final Set<Item> followItems = new HashSet<>();
    private ChatFormatting color = ChatFormatting.BLUE;

    @Override
    public String name() {
        return DATA_NAME;
    }

    @Override
    public Tag toNbt() {
        ListTag list = new ListTag();
        for (Item followItem : followItems) {
            list.add(IntTag.valueOf(Item.getId(followItem)));
        }
        CompoundTag compound = new CompoundTag();
        compound.put("followItems", list);
        compound.put("color", IntTag.valueOf(color.getId()));
        return compound;
    }

    @Override
    public void load(@Nullable Tag tag) {
        if (tag == null) {
            return;
        }
        followItems.clear();
        CompoundTag compound = (CompoundTag) tag;
        ListTag followItemsTag = (ListTag) compound.get("followItems");
        if (followItemsTag != null) {
            for (Tag intTag : followItemsTag) {
                followItems.add(Item.byId(((IntTag) intTag).getAsInt()));
            }
        }
        IntTag colorTag = (IntTag) compound.get("color");
        if (colorTag != null) {
            color = ChatFormatting.getById(colorTag.getAsInt());
        }
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

    public ChatFormatting getColor() {
        return color;
    }

    public void setColor(ChatFormatting color) {
        this.color = color;
        CarpetAjiAdditionSettings.minecraftServer.getScoreboard().getPlayerTeam("followItems").setColor(color);
        FollowCommand.data = this;
    }
}
