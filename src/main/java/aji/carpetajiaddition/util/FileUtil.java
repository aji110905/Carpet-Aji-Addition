package aji.carpetajiaddition.util;

import java.io.File;

public class FileUtil {
    private FileUtil(){
    }

    public static String getFileNameWithoutExtension(File file){
        String fileName = file.getName();
        return getFileNameWithoutExtension(fileName);
    }

    public static String getFileNameWithoutExtension(String fileName){
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }
}
