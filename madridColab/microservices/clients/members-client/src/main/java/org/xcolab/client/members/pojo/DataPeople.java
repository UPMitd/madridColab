package org.xcolab.client.members.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.xml.crypto.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPeople implements Serializable {

    private static final long serialVersionUID = 1267595728382132595L;

    public static final TypeProvider<DataPeople> TYPES =
            new TypeProvider<>(DataPeople.class,
                    new ParameterizedTypeReference<List<DataPeople>>() {
                    });

    private Long id_user;
    private String twitter;
    private String linkedin;
    private Boolean is_community;
    private Long id_sector;
    private String other;
    private String cod_postal;


    public DataPeople() {}

    public DataPeople(DataPeople value) {
        this.id_user = value.id_user;
        this.twitter = value.twitter;
        this.linkedin = value.linkedin;
        this.is_community = value.is_community;
        this.id_sector = value.id_sector;
        this.other = value.other;
        this.cod_postal = value.cod_postal;
    }

    public DataPeople(Long id_user, String twitter, String linkedin, Boolean is_community,
            Long id_sector, String other, String cod_postal) {
        this.id_user = id_user;
        this.twitter = twitter;
        this.linkedin = linkedin;
        this.is_community = is_community;
        this.id_sector = id_sector;
        this.other = other;
        this.cod_postal = cod_postal;
    }

    public Long getIdUser() {
        return this.id_user;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public Boolean getIsCommunity() {
        return is_community;
    }

    public Long getIdSector() {
        return id_sector;
    }

    public String getCodPostal() {
        return cod_postal;
    }

    public String getOther() {
        return other;
    }

    public void setIdUser(Long id_user) {
        this.id_user = id_user;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setIsCommunity(Boolean is_community) {
        this.is_community = is_community;
    }

    public void setIdSector(Long id_sector) {
        this.id_sector = id_sector;
    }

    public void setCodPostal(String cod_postal) {
        this.cod_postal = cod_postal;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataPeople)) {
            return false;
        }
        DataPeople data = (DataPeople) o;
        return Objects.equals(id_user, data.id_user) && Objects.equals(
                twitter, data.twitter) && Objects.equals(
                linkedin, data.linkedin) && Objects.equals(
                is_community, data.is_community) && Objects.equals(
                id_sector, data.id_sector) && Objects.equals(
                other, data.other) && Objects.equals(
                cod_postal, data.cod_postal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_user);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id_user", id_user)
                .append("twitter", twitter)
                .append("linkedin", linkedin)
                .append("is_community", is_community)
                .append("id_sector", id_sector)
                .append("other", other)
                .append("cod_postal", cod_postal)
                .toString();
    }
}
