package aji.carpetajiaddition.data;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface Data {
    String name();

    Tag toNbt();

    void load(@Nullable Tag tag);
}
