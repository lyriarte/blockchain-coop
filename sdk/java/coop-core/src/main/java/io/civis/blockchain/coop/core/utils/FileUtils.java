package io.civis.blockchain.coop.core.utils;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import io.civis.blockchain.coop.core.config.FabricConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class FileUtils {

    public static final String FILE = "file:";

    public static URL getUrl(String filename) throws MalformedURLException {
        if(filename.startsWith(FILE)) {
            return new URL(filename);
        }
        return Resources.getResource(filename);
    }
}
