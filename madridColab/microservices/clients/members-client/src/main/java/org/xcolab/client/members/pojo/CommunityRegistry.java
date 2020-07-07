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
    private Long id_role;

    public CommunityRegistry() {}

    public CommunityRegistry(CommunityRegistry value) {
        this.id_user = value.id_user;
        this.id_role = value.id_role;
    }

    public CommunityRegistry(Long id_user, Long id_role) {
        this.id_user = id_user;
        this.id_role = id_role;
    }

    public Long getIdUser() {
        return this.id_user;
    }

    public void setIdUser(Long id_user) {
        this.id_user = id_user;
    }

    public Long getIdRole() {
        return this.id_role;
    }

    public void setIdRole(Long id_role) {
        this.id_role = id_role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunityRegistry)) {
            return false;
        }
        CommunityRegistry role = (CommunityRegistry) o;
        return Objects.equals(id_user, role.id_user) && Objects.equals(id_role, role.id_role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_user, id_role);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id_user", id_user)
                .append("id_role", id_role)
                .toString();
    }
}
