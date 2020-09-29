package org.xcolab.view.pages.loginregister;

import org.xcolab.view.util.validation.CompareStrings;

import java.io.Serializable;
import java.util.List;

@CompareStrings(propertyNames = {"password", "retypePassword"},
        message = "{register.form.validation.passwordequal}")
public class CreateUserBeanCity extends CreateUserBean implements Serializable {

    private static final long serialVersionUID = 3L;

    private String twitter;
    private String web;

    private String aut_community;
    private String municipality;

    private List<Long> ods;


    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public List<Long> getOds() {
        return ods;
    }

    public void setOds(List<Long> ods) {
        this.ods = ods;
    }

    public String getWeb() {
        return web;
    }

    public String getAut_community() {
        return aut_community;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setAut_community(String aut_community) {
        this.aut_community = aut_community;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
}
