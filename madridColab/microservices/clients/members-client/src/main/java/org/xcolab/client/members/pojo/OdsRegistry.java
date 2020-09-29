package org.xcolab.client.members.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OdsRegistry implements Serializable {

    private static final long serialVersionUID = 1267595728382132595L;

    public static final TypeProvider<OdsRegistry> TYPES =
            new TypeProvider<>(OdsRegistry.class,
                    new ParameterizedTypeReference<List<OdsRegistry>>() {
                    });

    private Long id_user;
    private Long id_ods;

    public OdsRegistry() {}

    public OdsRegistry(OdsRegistry value) {
        this.id_user = value.id_user;
        this.id_ods = value.id_ods;
    }

    public OdsRegistry(Long id_user, Long id_ods) {
        this.id_user = id_user;
        this.id_ods = id_ods;
    }

    public Long getIdUser() {
        return this.id_user;
    }

    public void setIdUser(Long id_user) {
        this.id_user = id_user;
    }

    public Long getIdOds() {
        return this.id_ods;
    }

    public void setIdOds(Long id_ods) {
        this.id_ods = id_ods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OdsRegistry)) {
            return false;
        }
        OdsRegistry ods = (OdsRegistry) o;
        return Objects.equals(id_user, ods.id_user) && Objects.equals(
                id_ods, ods.id_ods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_user, id_ods);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id_user", id_user)
                .append("id_ods", id_ods)
                .toString();
    }
}
