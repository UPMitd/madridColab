package org.xcolab.view.pages.loginregister;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import org.xcolab.view.util.validation.CompareStrings;
import org.xcolab.view.util.validation.HtmlMaxLength;
import org.xcolab.view.util.validation.PasswordLength;
import org.xcolab.view.util.validation.UniqueEmail;
import org.xcolab.view.util.validation.UniqueScreenName;
import org.xcolab.view.util.validation.ValidScreenName;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

@CompareStrings(propertyNames = {"password", "retypePassword"},
        message = "{register.form.validation.passwordequal}")
public class CreateUserBeanExtended extends CreateUserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long community;

    public Long getCommunity() {
        return community;
    }

    public void setCommunity(Long community) {
        this.community = community;
    }
}
