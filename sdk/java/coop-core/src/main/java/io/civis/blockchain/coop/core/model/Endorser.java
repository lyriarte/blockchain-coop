package io.civis.blockchain.coop.core.model;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Endorser {

    public static Endorser fromStringPair(String pair) {
        List<String> endorser = Splitter.on(":").splitToList(pair);
        return new Endorser(endorser.get(0), endorser.get(1));
    }

    private String peer;
    private String organisation;

    public Endorser(String peer, String organisation) {
        this.peer = peer;
        this.organisation = organisation;
    }

    public String getPeer() {
        return peer;
    }

    public String getOrganisation() {
        return organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endorser)) return false;
        Endorser endorser = (Endorser) o;
        return Objects.equals(peer, endorser.peer) &&
                Objects.equals(organisation, endorser.organisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(peer, organisation);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Endorser.class.getSimpleName() + "[", "]")
                .add("peer='" + peer + "'")
                .add("organisation='" + organisation + "'")
                .toString();
    }
}
