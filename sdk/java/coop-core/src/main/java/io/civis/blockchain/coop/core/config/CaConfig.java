package io.civis.blockchain.coop.core.config;

public class CaConfig implements HasTlsCacerts {

    private String name;
    private String url;
    private String tlsCacerts;

    public CaConfig() {
    }

    public CaConfig(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getTlsCacerts() {
        return tlsCacerts;
    }

    public CaConfig setName(String name) {
        this.name = name;
        return this;
    }

    public CaConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public CaConfig setTlsCacerts(String tlsCacerts) {
        this.tlsCacerts = tlsCacerts;
        return this;
    }
}
