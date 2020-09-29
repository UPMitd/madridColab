package org.xcolab.client.members.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCity implements Serializable {

    private static final long serialVersionUID = 1267595728382132595L;

    public static final TypeProvider<DataCity> TYPES =
            new TypeProvider<>(DataCity.class,
                    new ParameterizedTypeReference<List<DataCity>>() {
                    });

    private Long id_user;
    private String twitter;
    private String web;
    private String aut_community;
    private String municipality;


    public DataCity() {}

    public DataCity(DataCity value) {
        this.id_user = value.id_user;
        this.twitter = value.twitter;
        this.web = value.web;
        this.aut_community = value.aut_community;
        this.municipality = value.municipality;
    }

    public DataCity(Long id_user, String twitter, String web, String aut_community, String municipality) {
        this.id_user = id_user;
        this.twitter = twitter;
        this.web = web;
        this.aut_community = aut_community;
        this.municipality = municipality;
    }

    public Long getIdUser() {
        return this.id_user;
    }

    public void setIdUser(Long id_user) {
        this.id_user = id_user;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getWeb() {
        return web;
    }

    public String getAutCommunity() {
        return aut_community;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setAutCommunity(String aut_community) {
        this.aut_community = aut_community;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataCity)) {
            return false;
        }
        DataCity data = (DataCity) o;
        return Objects.equals(id_user, data.id_user) && Objects.equals(
                twitter, data.twitter) && Objects.equals(
                web, data.web) && Objects.equals(
                aut_community, data.aut_community) && Objects.equals(
                municipality, data.municipality);
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
                .append("linkedin", web)
                .append("aut_community", aut_community)
                .append("municipality", municipality)
                .toString();
    }
}
