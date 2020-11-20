package org.xcolab.client.members.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCompany implements Serializable {

    private static final long serialVersionUID = 1267595728382132595L;

    public static final TypeProvider<DataCompany> TYPES =
            new TypeProvider<>(DataCompany.class,
                    new ParameterizedTypeReference<List<DataCompany>>() {
                    });

    private Long id_user;
    private String twitter;
    private String web;
    private Boolean is_community;
    private Long id_sector_company;
    private String other;
    private String cod_postal;


    public DataCompany() {}

    public DataCompany(DataCompany value) {
        this.id_user = value.id_user;
        this.twitter = value.twitter;
        this.web = value.web;
        this.is_community = value.is_community;
        this.id_sector_company = value.id_sector_company;
        this.other = value.other;
        this.cod_postal = value.cod_postal;
    }

    public DataCompany(Long id_user, String twitter, String web, Boolean is_community,
            Long id_sector, String other, String cod_postal) {
        this.id_user = id_user;
        this.twitter = twitter;
        this.web = web;
        this.is_community = is_community;
        this.id_sector_company = id_sector;
        this.other = other;
        this.cod_postal = cod_postal;
    }

    public Long getIdUser() {
        return this.id_user;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getWeb() {
        return web;
    }

    public Boolean getIsCommunity() {
        return is_community;
    }

    public Long getIdSectorCompany() {
        return id_sector_company;
    }

    public String getOther() {
        return other;
    }

    public String getCodPostal() {
        return cod_postal;
    }

    public void setIdUser(Long id_user) {
        this.id_user = id_user;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setIsCommunity(Boolean is_community) {
        this.is_community = is_community;
    }

    public void setIdSectorCompany(Long id_sector) {
        this.id_sector_company = id_sector;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void setCodPostal(String cod_postal) {
        this.cod_postal = cod_postal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataCompany)) {
            return false;
        }
        DataCompany data = (DataCompany) o;
        return Objects.equals(id_user, data.id_user) && Objects.equals(
                twitter, data.twitter) && Objects.equals(
                web, data.web) && Objects.equals(
                is_community, data.is_community) && Objects.equals(
                id_sector_company, data.id_sector_company);
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
                .append("is_community", is_community)
                .append("id_sector_company", id_sector_company)
                .append("other", other)
                .append("cod_postal", cod_postal)
                .toString();
    }
}
