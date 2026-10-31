package top.ajitech.carpetajiaddition.util;

import java.io.File;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {
    }

    public static String getNameWithoutExtension(Path path) {
        return getNameWithoutExtension(path.toFile());
    }

    public static String getNameWithoutExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        return index < 0 ? name : name.substring(0, index);
    }
}
