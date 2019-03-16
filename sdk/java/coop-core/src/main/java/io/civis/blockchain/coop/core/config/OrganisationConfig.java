package io.civis.blockchain.coop.core.config;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class OrganisationConfig {

    private String name;
    private String mspid;
    private CaConfig ca;
    private Map<String, PeerConfig> peers;

    public OrganisationConfig() {
    }

    public OrganisationConfig(String name, String mspid, CaConfig ca, Map<String, PeerConfig> peers) {
        this.name = name;
        this.mspid = mspid;
        this.ca = ca;
        this.peers = peers;
    }

    public String getName() {
        return name;
    }

    public String getMspid() {
        return mspid;
    }

    public CaConfig getCa() {
        return ca;
    }

    public Map<String, PeerConfig> getPeers() {
        return peers;
    }

    public OrganisationConfig setName(String name) {
        this.name = name;
        return this;
    }

    public OrganisationConfig setMspid(String mspid) {
        this.mspid = mspid;
        return this;
    }

    public OrganisationConfig setCa(CaConfig ca) {
        this.ca = ca;
        return this;
    }

    public OrganisationConfig setPeers(Map<String, PeerConfig> peers) {
        this.peers = peers;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganisationConfig)) return false;
        OrganisationConfig that = (OrganisationConfig) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(mspid, that.mspid) &&
                Objects.equals(ca, that.ca) &&
                Objects.equals(peers, that.peers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mspid, ca, peers);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrganisationConfig.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("mspid='" + mspid + "'")
                .add("ca=" + ca)
                .add("peers=" + peers)
                .toString();
    }
}
