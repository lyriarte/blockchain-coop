package io.civis.blockchain.coop.core.config;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public interface HasTlsCacerts {

    String getTlsCacerts();

    default URL getTlsCacertsAsUrl() {
        return Resources.getResource(getTlsCacerts());
    }

    default Properties getPeerTlsProperties() throws IOException {
        Properties prop = new Properties();
        prop.setProperty("allowAllHostNames", "true");
        URL path = getTlsCacertsAsUrl();
        prop.setProperty("pemFile", path.getFile());
        return prop;
    }
}
