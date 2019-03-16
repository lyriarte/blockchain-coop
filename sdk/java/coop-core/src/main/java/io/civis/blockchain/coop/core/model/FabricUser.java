package io.civis.blockchain.coop.core.model;

import com.google.common.collect.Sets;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.util.Objects;
import java.util.Set;

public class FabricUser implements User {

    private String name;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;
    private Set<String> roles;
    private String account;

    public FabricUser(String name, String affiliation, Enrollment enrollment, String mspId, Set<String> roles, String account) {
        this.name = name;
        this.affiliation = affiliation;
        this.enrollment = enrollment;
        this.mspId = mspId;
        this.roles = roles;
        this.account = account;
    }

    public FabricUser(String name, String affiliation, Enrollment enrollment, String mspId) {
        this(name, affiliation, enrollment, mspId, Sets.newHashSet(), "");
    }

    public String getName() {
        return name;
    }

    public FabricUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public FabricUser setAffiliation(String affiliation) {
        this.affiliation = affiliation;
        return this;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public FabricUser setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
        return this;
    }

    public String getMspId() {
        return mspId;
    }

    public FabricUser setMspId(String mspId) {
        this.mspId = mspId;
        return this;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public FabricUser setRoles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public FabricUser setAccount(String account) {
        this.account = account;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FabricUser that = (FabricUser) o;
        return name.equals(that.name) &&
                affiliation.equals(that.affiliation) &&
                enrollment.equals(that.enrollment) &&
                mspId.equals(that.mspId) &&
                roles.equals(that.roles) &&
                account.equals(that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, affiliation, enrollment, mspId, roles, account);
    }

    @Override
    public String toString() {
        return "FabricUser{" +
                "name='" + name + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", enrollment=" + enrollment +
                ", mspId='" + mspId + '\'' +
                ", roles=" + roles +
                ", account='" + account + '\'' +
                '}';
    }
}
