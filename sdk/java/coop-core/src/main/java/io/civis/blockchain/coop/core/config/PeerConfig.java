package io.civis.blockchain.coop.core.config;

import java.util.Objects;
import java.util.StringJoiner;

public class PeerConfig implements HasTlsCacerts {

    private String requests;
    private String events;
    private String serverHostname;
    private String tlsCacerts;

    public PeerConfig() {
    }

    public PeerConfig(String requests, String events, String serverHostname, String tlsCacerts) {
        this.requests = requests;
        this.events = events;
        this.serverHostname = serverHostname;
        this.tlsCacerts = tlsCacerts;
    }

    public String getRequests() {
        return requests;
    }

    public String getEvents() {
        return events;
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public String getTlsCacerts() {
        return tlsCacerts;
    }

    public PeerConfig setRequests(String requests) {
        this.requests = requests;
        return this;
    }

    public PeerConfig setEvents(String events) {
        this.events = events;
        return this;
    }

    public PeerConfig setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
        return this;
    }

    public PeerConfig setTlsCacerts(String tlsCacerts) {
        this.tlsCacerts = tlsCacerts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeerConfig)) return false;
        PeerConfig that = (PeerConfig) o;
        return Objects.equals(requests, that.requests) &&
                Objects.equals(events, that.events) &&
                Objects.equals(serverHostname, that.serverHostname) &&
                Objects.equals(tlsCacerts, that.tlsCacerts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requests, events, serverHostname, tlsCacerts);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PeerConfig.class.getSimpleName() + "[", "]")
                .add("requests='" + requests + "'")
                .add("events='" + events + "'")
                .add("serverHostname='" + serverHostname + "'")
                .add("tlsCacerts='" + tlsCacerts + "'")
                .toString();
    }
}
