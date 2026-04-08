package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.mixin.rules.betterLogCommand.LoggerRegistryAccessor;
import carpet.logging.LoggerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Map;

public class BetterLogCommandData implements Data{
    public static final String DATA_NAME = "betterLogCommand";
    private final Map<String, Map<String, String>> playerSubscriptions = ((LoggerRegistryAccessor) new LoggerRegistry()).getPlayerSubscriptions();
    private boolean isFirstLoad = true;

    @Override
    public String name() {
        return DATA_NAME;
    }

    @Override
    public Tag toNbt() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, Map<String, String>> entry : playerSubscriptions.entrySet()) {
            CompoundTag tag1 = new CompoundTag();
            for (Map.Entry<String, String> entry1 : entry.getValue().entrySet()) {
                String value = entry1.getValue();
                if (value == null) {
                    value = "null";
                }
                tag1.putString(entry1.getKey(), value);
            }
            tag.put(entry.getKey(), tag1);
        }
        return tag;
    }

    @Override
    public void load(Tag tag) {
        if (isFirstLoad && CarpetAjiAdditionSettings.betterLogCommand) {
            CompoundTag tag1 = (CompoundTag) tag;
            for (String string : tag1.getAllKeys()) {
                CompoundTag tag2 = (CompoundTag) tag1.get(string);
                for (String string1 : tag2.getAllKeys()) {
                    //#if MC < 12105
                    String s = tag2.getString(string1);
                    //#else
                    //$$ String s = tag2.getString(string1).orElseThrow();
                    //#endif
                    if (s.equals("null")) {
                        s = null;
                    }
                    LoggerRegistry.subscribePlayer(string, string1, s);
                }
            }
        }
        isFirstLoad = false;
    }
}
