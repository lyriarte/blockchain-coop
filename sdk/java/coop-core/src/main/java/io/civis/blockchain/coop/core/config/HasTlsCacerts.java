package io.civis.blockchain.coop.core.config;

import com.google.common.base.Strings;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public interface HasTlsCacerts {

    String getTlsCacerts();

    default URL getTlsCacertsAsUrl(String cryptoBase) {
        if (!Strings.isNullOrEmpty(cryptoBase) && !cryptoBase.endsWith("/")) {
            cryptoBase = cryptoBase + "/";
        }
        String baseTlsCacerts = cryptoBase + getTlsCacerts();
        return Resources.getResource(baseTlsCacerts);
    }

    default Properties getPeerTlsProperties(String cryptoBase) throws IOException {
        Properties prop = new Properties();
        prop.setProperty("allowAllHostNames", "true");
        URL path = getTlsCacertsAsUrl(cryptoBase);
        prop.setProperty("pemFile", path.getFile());
        return prop;
    }
}
