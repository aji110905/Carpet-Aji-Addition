package aji.carpetajiaddition.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class IOUtil {
    private IOUtil() {
    }

    public static Map<String, String> readAllFilesFromResource(String path) throws IOException, URISyntaxException {
        Map<String, String> fileMap = new HashMap<>();
        URL url = IOUtil.class.getClassLoader().getResource(path);
        if ("jar".equals(url.getProtocol())) {
            String jarPath = URLDecoder.decode(url.getPath().split("!")[0].substring(5), StandardCharsets.UTF_8);
            JarFile jar = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(path + "/") && !entry.isDirectory()) {
                    InputStream stream = jar.getInputStream(entry);
                    fileMap.put(entry.getName().split("/")[entry.getName().split("/").length - 1], new String(stream.readAllBytes()));
                    stream.close();
                }
            }
            jar.close();
        } else {
            File[] files = new File(url.toURI()).listFiles();
            if (files != null) {
                for (File file : files) {
                    FileInputStream stream = new FileInputStream(file);
                    fileMap.put(file.getName(), new String(stream.readAllBytes()));
                    stream.close();
                }
            }
        }
        return fileMap;
    }
}

