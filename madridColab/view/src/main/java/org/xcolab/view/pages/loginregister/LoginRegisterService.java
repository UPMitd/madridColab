package org.xcolab.view.pages.loginregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import org.xcolab.client.activities.ActivitiesClientUtil;
import org.xcolab.client.admin.attributes.platform.PlatformAttributeKey;
import org.xcolab.client.balloons.BalloonsClient;
import org.xcolab.client.balloons.exceptions.BalloonUserTrackingNotFoundException;
import org.xcolab.client.balloons.pojo.BalloonUserTracking;
import org.xcolab.client.members.MembersClient;
import org.xcolab.client.members.MessagingClient;
import org.xcolab.client.members.exceptions.MemberNotFoundException;
import org.xcolab.client.members.pojo.CommunityRegistry;
import org.xcolab.client.members.pojo.DataCity;
import org.xcolab.client.members.pojo.DataCompany;
import org.xcolab.client.members.pojo.DataPeople;
import org.xcolab.client.members.pojo.LoginToken;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.client.members.pojo.MessagingUserPreference;
import org.xcolab.client.members.pojo.OdsRegistry;
import org.xcolab.commons.html.HtmlUtil;
import org.xcolab.entity.utils.LinkUtils;
import org.xcolab.entity.utils.notifications.member.MemberBatchRegistrationNotification;
import org.xcolab.entity.utils.notifications.member.MemberRegistrationNotification;
import org.xcolab.util.activities.enums.MemberActivityType;
import org.xcolab.util.i18n.I18nUtils;
import org.xcolab.view.auth.AuthenticationService;
import org.xcolab.view.pages.redballoon.utils.BalloonCookie;
import org.xcolab.view.util.googleanalytics.GoogleAnalyticsEventType;
import org.xcolab.view.util.googleanalytics.GoogleAnalyticsUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LoginRegisterService {

    private static final Logger _log = LoggerFactory.getLogger(LoginRegisterService.class);

    private final AuthenticationService authenticationService;

    @Autowired
    public LoginRegisterService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Completes the user registration with the parameters set in the CreateUserBean
     *
     * @param request The HttpServletRequest object
     * @param response The HttpServletResponse object
     * @param newAccountBean The new user bean object
     * @param redirect Redirect URL for this request (may be null)
     */
    public void completeRegistration(HttpServletRequest request, HttpServletResponse response,
            CreateUserBeanExtended newAccountBean, String redirect, boolean postRegistration)
            throws IOException {

        _log.error("RegUser1");

        final Member member = register(newAccountBean.getScreenName(), newAccountBean.getPassword(),
                newAccountBean.getEmail(), newAccountBean.getFirstName(),
                newAccountBean.getLastName(), newAccountBean.getShortBio(),
                newAccountBean.getCountry(), newAccountBean.getImageId(),
                false, newAccountBean.getLanguage(), newAccountBean.getCommunities(),
                newAccountBean.getOds(), newAccountBean.getTwitter(), newAccountBean.getLinkedin(),
                newAccountBean.getIs_community(), newAccountBean.getId_sector(), newAccountBean.getOther(),
                newAccountBean.getCod_postal());

        _log.error("RegUser2");

        authAndRedirect(request, response, redirect, postRegistration, member);
    }

    public void completeRegistration(HttpServletRequest request, HttpServletResponse response,
            CreateUserBeanCity newAccountBean, String redirect, boolean postRegistration)
            throws IOException {

        _log.error("RegCity1");

        final Member member = registerCity(newAccountBean.getScreenName(), newAccountBean.getPassword(),
                newAccountBean.getEmail(), newAccountBean.getFirstName(),
                newAccountBean.getLastName(), newAccountBean.getShortBio(),
                newAccountBean.getCountry(), newAccountBean.getImageId(),
                false, newAccountBean.getLanguage(),
                newAccountBean.getOds(), newAccountBean.getTwitter(), newAccountBean.getWeb(),
                newAccountBean.getAut_community(), newAccountBean.getMunicipality());

        _log.error("RegCity2");

        authAndRedirect(request, response, redirect, postRegistration, member);
    }

    public void completeRegistration(HttpServletRequest request, HttpServletResponse response,
            CreateUserBeanCompany newAccountBean, String redirect, boolean postRegistration)
            throws IOException {

        _log.error("RegCompany1");

        final Member member = registerCompany(newAccountBean.getScreenName(), newAccountBean.getPassword(),
                newAccountBean.getEmail(), newAccountBean.getFirstName(),
                newAccountBean.getLastName(), newAccountBean.getShortBio(),
                newAccountBean.getCountry(), newAccountBean.getImageId(),
                false, newAccountBean.getLanguage(), newAccountBean.getCommunities(),
                newAccountBean.getOds(), newAccountBean.getTwitter(), newAccountBean.getWeb(),
                newAccountBean.getIs_community(), newAccountBean.getId_sector_company(),
                newAccountBean.getOther(), newAccountBean.getCod_postal());

        _log.error("RegCompany2");

        authAndRedirect(request, response, redirect, postRegistration, member);
    }

    private void authAndRedirect(HttpServletRequest request, HttpServletResponse response,
             String redirect, boolean postRegistration, Member member) throws IOException{
        authenticationService.authenticate(request, response, member);
        updateBalloonTracking(member, request);
        recordRegistrationEvent(member);

        if (redirect == null) {
            redirect = "/";
        }

        if (postRegistration) {
            // Add request variable for after-registration popover
            redirect = UriComponentsBuilder.fromUriString(redirect)
                    .queryParam("postRegistration", true)
                    .build().toUriString();
        }

        //Make URI relative to prevent injection of external redirect URIs
        redirect = LinkUtils.getRelativeUri(redirect);

        if (!redirect.startsWith("/login") && !redirect.startsWith("/sso/")) {
            response.sendRedirect(redirect);
        } else {
            response.sendRedirect("/");
        }
    }

    private void updateBalloonTracking(Member member, HttpServletRequest request) {
        Optional<BalloonCookie> balloonCookieOpt = BalloonCookie.from(request.getCookies());
        if (balloonCookieOpt.isPresent()) {
            BalloonCookie balloonCookie = balloonCookieOpt.get();
            try {
                BalloonUserTracking but =
                        BalloonsClient.getBalloonUserTracking(balloonCookie.getUuid());
                if (but.getUserId() == null) {
                    but.setRegistrationDate(new Timestamp(new Date().getTime()));
                    but.setUserId(member.getId());
                    BalloonsClient.updateBalloonUserTracking(but);
                }
            } catch (BalloonUserTrackingNotFoundException e) {
                _log.error("Can't find balloon user tracking for uuid: {}",
                        balloonCookie.getUuid());
            }
        }
        //update user association for all BUTs under this email address
        BalloonsClient.getBalloonUserTrackingByEmail(member.getEmailAddress()).forEach(
                b -> b.updateUserIdAndEmailIfEmpty(member.getId(), member.getEmailAddress()));
    }

    public void recordRegistrationEvent(Member member) {
        ActivitiesClientUtil.createActivityEntry(MemberActivityType.REGISTERED, member.getId(),
                member.getId());

        if (member.getFacebookId() != null) {
            GoogleAnalyticsUtils.pushEventAsync(GoogleAnalyticsEventType.REGISTRATION_SSO_FACEBOOK);
            return;
        }
        if (member.getGoogleId() != null) {
            GoogleAnalyticsUtils.pushEventAsync(GoogleAnalyticsEventType.REGISTRATION_SSO_GOOGLE);
        }
        GoogleAnalyticsUtils.pushEventAsync(GoogleAnalyticsEventType.REGISTRATION_FORM);
    }

    public void updatePassword(String forgotPasswordToken, String newPassword)
            throws MemberNotFoundException {
        Long userId = MembersClient.updateUserPassword(forgotPasswordToken, newPassword);
        if (userId == null) {
            throw new MemberNotFoundException(
                    "Member with forgotPasswordToken: " + forgotPasswordToken + " was not found");
        }
    }

    public Member autoRegister(String emailAddress, String firstName, String lastName) {
        return register(null, null, emailAddress, firstName, lastName,
                "", null, null, true, null,
                null, null, null, null, false, 5l, null, null);
    }

    public Member register(String screenName, String password, String email, String firstName,
            String lastName, String shortBio, String country, Long imageId,
            boolean generateLoginUrl, String language, List<Long> communities, List<Long> ods,
            String twitter, String linkedin, boolean is_community, Long sector, String other, String cod_postal) {

        final Member member = register( screenName,  password,  email,  firstName,
                 lastName,  shortBio,  country,  imageId, generateLoginUrl,  language);

        DataPeople data = new DataPeople(member.getId(), twitter, linkedin, is_community, sector, other, cod_postal);
        data = MembersClient.createDataPeople(data);

        if(communities != null && communities.size() > 0) {
            for(Long i: communities) {
                CommunityRegistry registry = new CommunityRegistry(member.getId(), i);
                registry = MembersClient.createCommunityRegistry(registry);
            }
        }

        if(ods != null && ods.size() > 0) {
            for(Long i: ods) {
                OdsRegistry registry = new OdsRegistry(member.getId(), i);
                registry = MembersClient.createOdsRegistry(registry);
            }
        }

        return member;
    }

    public Member registerCity(String screenName, String password, String email, String firstName,
            String lastName, String shortBio, String country, Long imageId,
            boolean generateLoginUrl, String language, List<Long> ods,
            String twitter, String web, String aut_community, String municipality) {

        final Member member = register( screenName,  password,  email,  firstName,
                lastName,  shortBio,  country,  imageId, generateLoginUrl,  language);

        DataCity data = new DataCity(member.getId(), twitter, web, aut_community, municipality);
        data = MembersClient.createDataCity(data);

        if(ods != null && ods.size() > 0) {
            for(Long i: ods) {
                OdsRegistry registry = new OdsRegistry(member.getId(), i);
                registry = MembersClient.createOdsRegistry(registry);
            }
        }

        return member;
    }

    public Member registerCompany(String screenName, String password, String email, String firstName,
            String lastName, String shortBio, String country, Long imageId,
            boolean generateLoginUrl, String language, List<Long> communities, List<Long> ods,
            String twitter, String web, boolean is_community, Long sector, String other, String cod_postal) {

        final Member member = register( screenName,  password,  email,  firstName,
                lastName,  shortBio,  country,  imageId, generateLoginUrl,  language);

        DataCompany data = new DataCompany(member.getId(), twitter, web, is_community, sector, other, cod_postal);
        data = MembersClient.createDataCompany(data);

        if(communities != null && communities.size() > 0) {
            for(Long i: communities) {
                CommunityRegistry registry = new CommunityRegistry(member.getId(), i);
                registry = MembersClient.createCommunityRegistry(registry);
            }
        }

        if(ods != null && ods.size() > 0) {
            for(Long i: ods) {
                OdsRegistry registry = new OdsRegistry(member.getId(), i);
                registry = MembersClient.createOdsRegistry(registry);
            }
        }

        return member;
    }

    public Member register(String screenName, String password, String email, String firstName,
            String lastName, String shortBio, String country, Long imageId,
            boolean generateLoginUrl, String language) {

        Assert.notNull(email, "Email address is required");

        Member member = new Member();
        if (screenName == null) {
            member.setScreenName(MembersClient.generateScreenName(lastName, firstName));
        } else {
            member.setScreenName(screenName);
        }
        if(email != null)
            member.setEmailAddress(email);
        if(firstName != null)
            member.setFirstName(firstName);
        member.setHashedPassword(password);
        if(lastName != null)
            member.setLastName(lastName);
        member.setDefaultLocale(language != null ? language : I18nUtils.DEFAULT_LANGUAGE);
        member.setStatus(0);

        final String baseUri = PlatformAttributeKey.COLAB_URL.get();
        member.setShortBio(HtmlUtil.cleanSome(shortBio, baseUri));
        member.setCountry(country);
        if (imageId != null) {
            member.setPortraitFileEntryId(imageId);
        } else {
            member.setPortraitFileEntryId(0L);
        }
        member = MembersClient.register(member);

        final MessagingUserPreference preferences =
                MessagingClient.getMessagingPreferencesForMember(member.getId());
        preferences.setEmailActivityDailyDigest(false);
        preferences.setEmailOnActivity(false);
        preferences.setEmailOnReceipt(false);
        preferences.setEmailOnSend(false);
        MessagingClient.updateMessagingPreferences(preferences);

        if (generateLoginUrl) {
            final LoginToken loginToken = MembersClient.createLoginToken(member.getId());
            new MemberBatchRegistrationNotification(member, loginToken).sendEmailNotification();
        } else {
            new MemberRegistrationNotification(member).sendEmailNotification();
        }

        return member;
    }
}
