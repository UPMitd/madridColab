package org.xcolab.view.pages.loginregister;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import org.xcolab.client.admin.attributes.configuration.ConfigurationAttributeKey;
import org.xcolab.client.admin.attributes.platform.PlatformAttributeKey;
import org.xcolab.client.members.MembersClient;
import org.xcolab.client.tracking.TrackingClient;
import org.xcolab.client.tracking.pojo.Location;
import org.xcolab.commons.CommunityUtil;
import org.xcolab.commons.CountryUtil;
import org.xcolab.commons.ODSUtil;
import org.xcolab.commons.SectorCompanyUtil;
import org.xcolab.commons.SectorUtil;
import org.xcolab.commons.recaptcha.RecaptchaValidator;
import org.xcolab.commons.servlet.RequestParamUtil;
import org.xcolab.util.i18n.I18nUtils;
import org.xcolab.view.auth.MemberAuthUtil;
import org.xcolab.view.errors.AccessDeniedPage;
import org.xcolab.view.i18n.ResourceMessageResolver;
import org.xcolab.view.pages.loginregister.exception.UserLocationNotResolvableException;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class LoginRegisterController {

    private static final Logger _log = LoggerFactory.getLogger(LoginRegisterController.class);

    private static final String REGISTER_VIEW_NAME = "loginregister/register";
    private static final String REGISTER_COMPANY_VIEW_NAME = "loginregister/registerCompany";
    private static final String REGISTER_CITY_VIEW_NAME = "loginregister/registerCity";
    private final LoginRegisterService loginRegisterService;

    private final ResourceMessageResolver resourceMessageResolver;
    private final RecaptchaValidator recaptchaValidator;

    @Autowired
    public LoginRegisterController(LoginRegisterService loginRegisterService,
            ResourceMessageResolver resourceMessageResolver) {
        this.loginRegisterService = loginRegisterService;
        this.resourceMessageResolver = resourceMessageResolver;
        final String recaptchaSecret = PlatformAttributeKey.GOOGLE_RECAPTCHA_SITE_SECRET_KEY.get();
        recaptchaValidator = new RecaptchaValidator(recaptchaSecret);
    }

    //    @Autowired
//    private Validator validator;
//
//    @InitBinder("createUserBean")
//    public void initBinder(WebDataBinder binder) {
//        binder.setValidator(validator);
//    }
    
    @GetMapping("/register")
    public String register(HttpServletRequest request, HttpServletResponse response, Model model) {

        if(!ConfigurationAttributeKey.ALLOW_SELF_REGISTRATION.get()) {
            return new AccessDeniedPage(null).toViewName(response);
        }

        String redirect = RequestParamUtil.getString(request, "redirect");

        if (redirect == null || redirect.trim().isEmpty()) {
            redirect = request.getParameter("redirect");
            if (redirect == null) {
                redirect = request.getHeader(HttpHeaders.REFERER);
            }
        }
        if (MemberAuthUtil.getuserId(request) > 0) {
            return "redirect:/";
        }

        if (StringUtils.isNotEmpty(redirect)) {
            model.addAttribute("redirect", redirect);
        }

        // append SSO attributes
        CreateUserBeanExtended userBean = new CreateUserBeanExtended();
        model.addAttribute("createUserBeanExtended", userBean);

        // Get country location
        if (StringUtils.isEmpty(userBean.getCountry())) {
            try {
                userBean.setLanguage(I18nUtils.DEFAULT_LOCALE.getLanguage());
                userBean.setCountry(getCountryCodeFromRemoteAddress(request.getRemoteAddr()));
            } catch (UserLocationNotResolvableException ignored) {
            }
        }
        model.addAttribute("generateScreenName", ConfigurationAttributeKey.GENERATE_SCREEN_NAME.get());
        final String loginInfoText = ConfigurationAttributeKey.LOGIN_INFO_MESSAGE.get();
        model.addAttribute("hasLoginInfoText", StringUtils.isNotBlank(loginInfoText));
        model.addAttribute("loginInfoText", loginInfoText);
        model.addAttribute("countrySelectItems", CountryUtil.getSelectOptions());
        model.addAttribute("sectorSelectItems", SectorUtil.getSelectOptions());
        model.addAttribute("communitiesSelectItems", CommunityUtil.getSelectOptions());
        model.addAttribute("ODSSelectItems", ODSUtil.getSelectOptions());

        model.addAttribute("activeRegister", "active");

        model.addAttribute("isI18NActive",ConfigurationAttributeKey.IS_I18N_ACTIVE.get());
        model.addAttribute("languageSelectItems", I18nUtils.getSelectList());
        return REGISTER_VIEW_NAME;
    }

    @GetMapping("/registerCompany")
    public String registerCompany(HttpServletRequest request, HttpServletResponse response, Model model) {

        if(!ConfigurationAttributeKey.ALLOW_SELF_REGISTRATION.get()) {
            return new AccessDeniedPage(null).toViewName(response);
        }

        String redirect = RequestParamUtil.getString(request, "redirect");

        if (redirect == null || redirect.trim().isEmpty()) {
            redirect = request.getParameter("redirect");
            if (redirect == null) {
                redirect = request.getHeader(HttpHeaders.REFERER);
            }
        }
        if (MemberAuthUtil.getuserId(request) > 0) {
            return "redirect:/";
        }

        if (StringUtils.isNotEmpty(redirect)) {
            model.addAttribute("redirect", redirect);
        }

        // append SSO attributes
        CreateUserBeanCompany userBean = new CreateUserBeanCompany();
        model.addAttribute("createUserBeanCompany", userBean);

        // Get country location
        if (StringUtils.isEmpty(userBean.getCountry())) {
            try {
                userBean.setLanguage(I18nUtils.DEFAULT_LOCALE.getLanguage());
                userBean.setCountry(getCountryCodeFromRemoteAddress(request.getRemoteAddr()));
            } catch (UserLocationNotResolvableException ignored) {
            }
        }
        model.addAttribute("generateScreenName", ConfigurationAttributeKey.GENERATE_SCREEN_NAME.get());
        final String loginInfoText = ConfigurationAttributeKey.LOGIN_INFO_MESSAGE.get();
        model.addAttribute("hasLoginInfoText", StringUtils.isNotBlank(loginInfoText));
        model.addAttribute("loginInfoText", loginInfoText);
        model.addAttribute("countrySelectItems", CountryUtil.getSelectOptions());
        model.addAttribute("sectorCompanySelectItems", SectorCompanyUtil.getSelectOptions());
        model.addAttribute("communitiesSelectItems", CommunityUtil.getSelectOptions());
        model.addAttribute("ODSSelectItems", ODSUtil.getSelectOptions());

        model.addAttribute("activeRegisterCompany", "active");

        model.addAttribute("isI18NActive",ConfigurationAttributeKey.IS_I18N_ACTIVE.get());
        model.addAttribute("languageSelectItems", I18nUtils.getSelectList());
        return REGISTER_COMPANY_VIEW_NAME;
    }

    @GetMapping("/registerCity")
    public String registerCity(HttpServletRequest request, HttpServletResponse response, Model model) {

        if(!ConfigurationAttributeKey.ALLOW_SELF_REGISTRATION.get()) {
            return new AccessDeniedPage(null).toViewName(response);
        }

        String redirect = RequestParamUtil.getString(request, "redirect");

        if (redirect == null || redirect.trim().isEmpty()) {
            redirect = request.getParameter("redirect");
            if (redirect == null) {
                redirect = request.getHeader(HttpHeaders.REFERER);
            }
        }
        if (MemberAuthUtil.getuserId(request) > 0) {
            return "redirect:/";
        }

        if (StringUtils.isNotEmpty(redirect)) {
            model.addAttribute("redirect", redirect);
        }

        // append SSO attributes
        CreateUserBeanCity userBean = new CreateUserBeanCity();
        model.addAttribute("createUserBeanCity", userBean);

        // Get country location
        if (StringUtils.isEmpty(userBean.getCountry())) {
            try {
                userBean.setLanguage(I18nUtils.DEFAULT_LOCALE.getLanguage());
                userBean.setCountry(getCountryCodeFromRemoteAddress(request.getRemoteAddr()));
            } catch (UserLocationNotResolvableException ignored) {
            }
        }
        model.addAttribute("generateScreenName", ConfigurationAttributeKey.GENERATE_SCREEN_NAME.get());
        final String loginInfoText = ConfigurationAttributeKey.LOGIN_INFO_MESSAGE.get();
        model.addAttribute("hasLoginInfoText", StringUtils.isNotBlank(loginInfoText));
        model.addAttribute("loginInfoText", loginInfoText);
        model.addAttribute("countrySelectItems", CountryUtil.getSelectOptions());
        model.addAttribute("ODSSelectItems", ODSUtil.getSelectOptions());

        model.addAttribute("activeRegisterCity", "active");

        model.addAttribute("isI18NActive",ConfigurationAttributeKey.IS_I18N_ACTIVE.get());
        model.addAttribute("languageSelectItems", I18nUtils.getSelectList());
        return REGISTER_CITY_VIEW_NAME;
    }



    private String getCountryCodeFromRemoteAddress(String ipAddr) throws UserLocationNotResolvableException {
        try {
            Location location = TrackingClient.getLocationForIp(ipAddr);
            if (location != null) {
                return location.getCountry();
            }
        } catch (Exception e) {
            throw new UserLocationNotResolvableException(
                    String.format("Could not retrieve country from IP address %s", ipAddr), e);
        }
        throw new UserLocationNotResolvableException(
                String.format("Could not retrieve country from IP address %s", ipAddr));
    }

    @PostMapping("/register")
    public String registerUser(HttpServletRequest request, HttpServletResponse response, Model model,
            @Valid CreateUserBeanExtended newAccountBean, BindingResult result,
            @RequestParam(required = false) String redirect) throws IOException {

        //_log.error("User1");
        System.out.println(model.toString());

        if (result.hasErrors()) {
            //_log.error("User2");
            return showRegistrationError(model);
        }
        boolean captchaValid = true;
        //_log.error("User3");
        // require captcha if user is not logged in via SSO
        if (PlatformAttributeKey.GOOGLE_RECAPTCHA_IS_ACTIVE.get()) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            captchaValid = recaptchaValidator.verify(gRecaptchaResponse);
        }
        if (!captchaValid) {
            //_log.error("User4");
            result.addError(new ObjectError("createUserBean", resourceMessageResolver.
                    getLocalizedMessage("register.form.validation.captcha.message")));
            return showRegistrationError(model);
        }
        //TODO COLAB-2617: improve redirect to avoid double handling
        //_log.error("User5");
        loginRegisterService.completeRegistration(request, response, newAccountBean, redirect, false);
        //_log.error("User6");
        return REGISTER_VIEW_NAME;
    }

    @PostMapping("/registerCity")
    public String registerUser(HttpServletRequest request, HttpServletResponse response, Model model,
            @Valid @ModelAttribute("createUserBeanCity")  CreateUserBeanCity newAccountBean, BindingResult result,
            @RequestParam(required = false) String redirect) throws IOException {

        //_log.error("City1");
        System.out.println(model.toString());

        if (result.hasErrors()) {
            //_log.error("City2");
            return showRegistrationError(model);
        }
        boolean captchaValid = true;
        //_log.error("City3");
        // require captcha if user is not logged in via SSO
        if (PlatformAttributeKey.GOOGLE_RECAPTCHA_IS_ACTIVE.get()) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            captchaValid = recaptchaValidator.verify(gRecaptchaResponse);
        }
        if (!captchaValid) {
            //_log.error("City4");
            result.addError(new ObjectError("createUserBean", resourceMessageResolver.
                    getLocalizedMessage("register.form.validation.captcha.message")));
            return showRegistrationError(model);
        }
        //TODO COLAB-2617: improve redirect to avoid double handling
        //_log.error("City5");
        loginRegisterService.completeRegistration(request, response, newAccountBean, redirect, false);
        //_log.error("City6");
        return REGISTER_CITY_VIEW_NAME;
    }

    @PostMapping("/registerCompany")
    public String registerUser(HttpServletRequest request, HttpServletResponse response, Model model,
            @Valid @ModelAttribute("createUserBeanCompany") CreateUserBeanCompany newAccountBean, BindingResult result,
            @RequestParam(required = false) String redirect) throws IOException {

        //_log.error("Company1");
        System.out.println(model.toString());

        if (result.hasErrors()) {
            //_log.error("Company2");
            return showRegistrationError(model);
        }
        boolean captchaValid = true;
        //_log.error("Company3");
        // require captcha if user is not logged in via SSO
        if (PlatformAttributeKey.GOOGLE_RECAPTCHA_IS_ACTIVE.get()) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            captchaValid = recaptchaValidator.verify(gRecaptchaResponse);
        }
        if (!captchaValid) {
            //_log.error("Company4");
            result.addError(new ObjectError("createUserBean", resourceMessageResolver.
                    getLocalizedMessage("register.form.validation.captcha.message")));
            return showRegistrationError(model);
        }
        //TODO COLAB-2617: improve redirect to avoid double handling
        //_log.error("Company5");
        loginRegisterService.completeRegistration(request, response, newAccountBean, redirect, false);
        //_log.error("Company6");
        return REGISTER_COMPANY_VIEW_NAME;
    }

    private String showRegistrationError(Model model) {
        model.addAttribute("countrySelectItems", CountryUtil.getSelectOptions());
        model.addAttribute("isI18NActive",ConfigurationAttributeKey.IS_I18N_ACTIVE.get());
        model.addAttribute("languageSelectItems", I18nUtils.getSelectList());
        return REGISTER_VIEW_NAME;
    }

    @ModelAttribute("recaptchaDataSiteKey")
    public String getRecaptchaDataSiteKey(){
        return PlatformAttributeKey.GOOGLE_RECAPTCHA_SITE_KEY.get();
    }

    @PostMapping("/api/register/generateScreenName")
    public void generateScreenName(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        JSONObject json = new JSONObject();
        final String firstName = request.getParameter("firstName");
        final String lastName = request.getParameter("lastName");

        try {
            try {
                json.put("screenName", MembersClient.generateScreenName(lastName, firstName));
                json.put("success", true);
            } catch (HttpClientErrorException e) {
                _log.warn("Failed to generate user name ", e);
                json.put("success", false);
                json.put("error", e.toString());
            }
        } catch (JSONException ignored) {

        }

        response.getWriter().write(json.toString());
    }
}
