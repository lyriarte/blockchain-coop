package io.civis.blockchain.coop.core.config;

import java.util.Objects;
import java.util.StringJoiner;

public class OrdererConfig implements HasTlsCacerts {

    private String url;
    private String serverHostname;
    private String tlsCacerts;

    public OrdererConfig() {
    }

    public OrdererConfig(String url, String serverHostname, String tlsCacerts) {
        this.url = url;
        this.serverHostname = serverHostname;
        this.tlsCacerts = tlsCacerts;
    }

    public String getUrl() {
        return url;
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public String getTlsCacerts() {
        return tlsCacerts;
    }

    public OrdererConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public OrdererConfig setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
        return this;
    }

    public OrdererConfig setTlsCacerts(String tlsCacerts) {
        this.tlsCacerts = tlsCacerts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdererConfig)) return false;
        OrdererConfig that = (OrdererConfig) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(serverHostname, that.serverHostname) &&
                Objects.equals(tlsCacerts, that.tlsCacerts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, serverHostname, tlsCacerts);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrdererConfig.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("name='" + serverHostname + "'")
                .add("tlsCacerts='" + tlsCacerts + "'")
                .toString();
    }
}
