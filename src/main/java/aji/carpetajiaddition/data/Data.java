package aji.carpetajiaddition.data;
import net.minecraft.nbt.Tag;

public interface Data {
    String name();

    Tag toNbt();

    void load(Tag tag);
}
