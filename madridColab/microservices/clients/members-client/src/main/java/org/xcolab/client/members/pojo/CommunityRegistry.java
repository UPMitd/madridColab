package org.xcolab.client.members.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunityRegistry implements Serializable {

    private static final long serialVersionUID = 1267595728382132595L;

    public static final TypeProvider<CommunityRegistry> TYPES =
            new TypeProvider<>(CommunityRegistry.class,
                    new ParameterizedTypeReference<List<CommunityRegistry>>() {
                    });

    private Long id_user;
    private Long id_community;

    public CommunityRegistry() {}

    public CommunityRegistry(CommunityRegistry value) {
        this.id_user = value.id_user;
        this.id_community = value.id_community;
    }

    public CommunityRegistry(Long id_user, Long id_community) {
        this.id_user = id_user;
        this.id_community = id_community;
    }

    public Long getIdUser() {
        return this.id_user;
    }

    public void setIdUser(Long id_user) {
        this.id_user = id_user;
    }

    public Long getIdCommunity() {
        return this.id_community;
    }

    public void setIdCommunity(Long id_community) {
        this.id_community = id_community;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunityRegistry)) {
            return false;
        }
        CommunityRegistry community = (CommunityRegistry) o;
        return Objects.equals(id_user, community.id_user) && Objects.equals(
                id_community, community.id_community);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_user, id_community);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id_user", id_user)
                .append("id_community", id_community)
                .toString();
    }
}
