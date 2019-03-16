package io.civis.blockchain.coop.core.config;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class NetworkConfig {

    private OrdererConfig orderer;
    private Map<String, OrganisationConfig> organisations;

    public NetworkConfig() {
    }

    public NetworkConfig(OrdererConfig orderer, Map<String, OrganisationConfig> organisations) {
        this.orderer = orderer;
        this.organisations = organisations;
    }

    public OrdererConfig getOrderer() {
        return orderer;
    }

    public Map<String, OrganisationConfig> getOrganisations() {
        return organisations;
    }

    public OrganisationConfig getOrganisation(String orgName) {
        return organisations.get(orgName);
    }

    public NetworkConfig setOrderer(OrdererConfig orderer) {
        this.orderer = orderer;
        return this;
    }

    public NetworkConfig setOrganisations(Map<String, OrganisationConfig> organisations) {
        this.organisations = organisations;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkConfig)) return false;
        NetworkConfig that = (NetworkConfig) o;
        return Objects.equals(orderer, that.orderer) &&
                Objects.equals(organisations, that.organisations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderer, organisations);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NetworkConfig.class.getSimpleName() + "[", "]")
                .add("orderer=" + orderer)
                .add("organisations=" + organisations)
                .toString();
    }
}
