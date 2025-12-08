package aji.carpetajiaddition.data;
import net.minecraft.nbt.NbtElement;

public interface Data {
    String name();

    NbtElement toNbt();

    void load(NbtElement element);
}
