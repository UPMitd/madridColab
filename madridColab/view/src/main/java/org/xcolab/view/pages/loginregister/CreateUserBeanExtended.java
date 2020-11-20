package org.xcolab.view.pages.loginregister;

import org.xcolab.view.util.validation.CompareStrings;

import java.io.Serializable;
import java.util.List;

@CompareStrings(propertyNames = {"password", "retypePassword"},
        message = "{register.form.validation.passwordequal}")
public class CreateUserBeanExtended extends CreateUserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*@NotNull
    private Long community;*/

    private Long id_sector;

    private String twitter;
    private String linkedin;

    private String cod_postal;
    private String other;

    private Boolean is_community;

    private List<Long> communities;
    private List<Long> ods;

    public Long getId_sector() {
        return id_sector;
    }

    public void setId_sector(Long id_sector) {
        this.id_sector = id_sector;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
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

    public String getCod_postal() {
        return cod_postal;
    }

    public void setCod_postal(String cod_postal) {
        this.cod_postal = cod_postal;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    /*public Long getCommunity() {
        return community;
    }

    public void setCommunity(Long community) {
        this.community = community;
    }*/
}
