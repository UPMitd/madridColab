package org.xcolab.view.pages.loginregister;

import org.xcolab.view.util.validation.CompareStrings;

import java.io.Serializable;
import java.util.List;

@CompareStrings(propertyNames = {"password", "retypePassword"},
        message = "{register.form.validation.passwordequal}")
public class CreateUserBeanCompany extends CreateUserBean implements Serializable {

    private static final long serialVersionUID = 2L;

    /*@NotNull
    private Long community;*/

    private Long id_sector_company;

    private String twitter;
    private String web;


    private Boolean is_community;

    private List<Long> communities;
    private List<Long> ods;

    public Long getId_sector_company() {
        return id_sector_company;
    }

    public void setId_sector_company(Long id_sector_company) {
        this.id_sector_company = id_sector_company;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getWeb() {
        return web;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Boolean getIs_community() {
        return is_community;
    }

    public void setIs_community(Boolean community) {
        is_community = community;
    }

    public List<Long> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Long> communities) {
        this.communities = communities;
    }

    public List<Long> getOds() {
        return ods;
    }

    public void setOds(List<Long> ods) {
        this.ods = ods;
    }

}
