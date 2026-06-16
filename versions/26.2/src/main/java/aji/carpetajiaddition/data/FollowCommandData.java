package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionExtension;
import aji.carpetajiaddition.constant.ModConstants;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FollowCommandData implements Data {
    public static final String DATA_NAME = "followCommand";

    private final Set<Item> followItems = new HashSet<>();
    private TeamColor color = TeamColor.BLUE;

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
        compound.put("color", StringTag.valueOf(color.getSerializedName()));
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
                followItems.add(Item.byId(((IntTag) intTag).intValue()));
            }
        }
        StringTag colorTag = (StringTag) compound.get("color");
        if (colorTag != null) {
            color = TeamColor.byName(colorTag.asString().get());
        }
    }

    public Set<Item> getFollowItems() {
        return followItems;
    }

    public boolean addToFollowItems(Item Item) {
        return followItems.add(Item);
    }

    public boolean removeFromFollowItems(Item Item) {
        return followItems.remove(Item);
    }

    public TeamColor getColor() {
        return color;
    }

    public void setColor(MinecraftServer server, TeamColor color) {
        this.color = color;
        PlayerTeam team = server.getScoreboard().getPlayerTeam("followItems");
        if (team == null) {
            ModConstants.LOGGER.warn("Team 'followItems' not found");
            return;
        }
        team.setColor(Optional.of(color));
    }

    public static FollowCommandData getInstance(){
        return (FollowCommandData) CarpetAjiAdditionExtension.INSTANCE.getDataManager().getData(DATA_NAME);
    }
}