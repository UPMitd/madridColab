package org.xcolab.view.config.spring.beans;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.web.filter.CompositeFilter;

import org.xcolab.commons.servlet.RequestParamUtil;
import org.xcolab.commons.servlet.flash.AlertMessage;
import org.xcolab.view.auth.AuthenticationContext;
import org.xcolab.view.auth.handlers.AuthenticationSuccessHandler;
import org.xcolab.view.auth.login.spring.MemberDetailsService;
import org.xcolab.view.config.spring.sso.ClimateXPrincipalExtractor;
import org.xcolab.view.config.spring.sso.ColabPrincipalExtractor;
import org.xcolab.view.config.spring.sso.FacebookPrincipalExtractor;
import org.xcolab.view.config.spring.sso.GooglePrincipalExtractor;
import org.xcolab.view.pages.loginregister.LoginRegisterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableOAuth2Client
@Configuration
public class SsoClientConfig {

    private static final Logger log = LoggerFactory.getLogger(SsoClientConfig.class);

    private static final AuthenticationContext AUTHENTICATION_CONTEXT = new AuthenticationContext();

    private final OAuth2ClientContext oauth2ClientContext;
    private final LoginRegisterService loginRegisterService;
    private final MemberDetailsService memberDetailsService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    public SsoClientConfig(OAuth2ClientContext oauth2ClientContext,
            LoginRegisterService loginRegisterService, MemberDetailsService memberDetailsService,
            AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.oauth2ClientContext = oauth2ClientContext;
        this.loginRegisterService = loginRegisterService;
        this.memberDetailsService = memberDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public SsoFilter ssoFilter() {
        SsoFilter ssoFilter = new SsoFilter(oauth2ClientContext, authenticationSuccessHandler);
        configureSsoFilter(ssoFilter, facebook(), "/sso/facebook", false,
                FacebookPrincipalExtractor::new);
        configureSsoFilter(ssoFilter, google(), "/sso/google", false,
                GooglePrincipalExtractor::new);
        configureSsoFilter(ssoFilter, xcolab(), "/sso/xcolab", true,
                ColabPrincipalExtractor::new);
        configureSsoFilter(ssoFilter, climateX(), "/sso/climatex", true,
                ClimateXPrincipalExtractor::new);
        return ssoFilter;
    }

    @Bean
    public SsoServices ssoServices() {
        final boolean isFacebookEnabled = StringUtils.isNotEmpty(facebook().getClient().getClientId());
        final boolean isGoogleEnabled = StringUtils.isNotEmpty(google().getClient().getClientId());
        final boolean isClimateXEnabled = StringUtils.isNotEmpty(climateX().getClient().getClientId());
        return new SsoServices(isFacebookEnabled, isGoogleEnabled, isClimateXEnabled);
    }

    private void configureSsoFilter(SsoFilter ssoFilter, SsoClientResources clientResources,
            String path, boolean requireHostname,
            BiFunction<LoginRegisterService, MemberDetailsService, PrincipalExtractor> principalExtractorSupplier) {
        if (isClientConfigured(clientResources)) {
            if (requireHostname) {
                if (StringUtils.isEmpty(clientResources.getHostname())) {
                    throw new IllegalStateException("Hostname is not configured for SsoFilter " + path);
                }
            }
            log.info("Configuring SsoFilter {} (clientId = {})", path,
                    clientResources.client.getClientId());
            ssoFilter.addFilter(clientResources, path, principalExtractorSupplier.apply(
                    loginRegisterService, memberDetailsService));
        }
    }

    private boolean isClientConfigured(SsoClientResources clientResources) {
        final AuthorizationCodeResourceDetails client = clientResources.getClient();
        return StringUtils.isNoneEmpty(client.getClientId(), client.getClientSecret());
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("sso.facebook")
    public SsoClientResources facebook() {
        return new SsoClientResources();
    }

    @Bean
    @ConfigurationProperties("sso.google")
    public SsoClientResources google() {
        return new SsoClientResources();
    }

    @Bean
    @ConfigurationProperties("sso.xcolab")
    public SsoClientResources xcolab() {
        return new SsoClientResources();
    }

    @Bean
    @ConfigurationProperties("sso.climatex")
    public SsoClientResources climateX() {
        return new SsoClientResources();
    }

    public static class SsoFilter implements Filter {

        public static final String SSO_SAVED_REFERER_SESSION_ATTRIBUTE = "SSO_SAVED_REFERRER";

        private final OAuth2ClientContext oAuth2ClientContext;
        private final AuthenticationSuccessHandler authenticationSuccessHandler;
        private final List<Filter> filters = new ArrayList<>();

        public SsoFilter(OAuth2ClientContext oAuth2ClientContext,
                AuthenticationSuccessHandler authenticationSuccessHandler) {
            this.oAuth2ClientContext = oAuth2ClientContext;
            this.authenticationSuccessHandler = authenticationSuccessHandler;
        }

        public Filter getFilter() {
            final CompositeFilter compositeFilter = new CompositeFilter();
            compositeFilter.setFilters(filters);
            return compositeFilter;
        }

        public void addFilter(SsoClientResources clientResources, String path,
                PrincipalExtractor principalExtractor) {
            filters.add(ssoFilter(clientResources, path, principalExtractor));
        }

        private Filter ssoFilter(SsoClientResources client, String path,
                PrincipalExtractor principalExtractor) {
            OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
            OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
            filter.setRestTemplate(template);
            UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                    client.getResource().getUserInfoUri(), client.getClient().getClientId());
            tokenServices.setPrincipalExtractor(principalExtractor);
            tokenServices.setRestTemplate(template);
            filter.setTokenServices(tokenServices);
            filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
            filter.setAuthenticationFailureHandler(new ForwardAuthenticationFailureHandler(
                    "/oauth/error/authenticationError"));
            return filter;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            getFilter().init(filterConfig);
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            if (httpRequest.getServletPath().startsWith("/sso")) {

                final boolean isLoggedIn = AUTHENTICATION_CONTEXT.isLoggedIn();
                if (isLoggedIn) {
                    // If the user is already logged in, the sso endpoints should not be accessed
                    AlertMessage.warning("You're already logged in!").flash(httpRequest);
                    httpResponse.sendRedirect("/");
                    return;
                } else {
                    final HttpSession session = httpRequest.getSession();
                    if (!RequestParamUtil.contains(httpRequest, "code")) {
                        session.setAttribute(SSO_SAVED_REFERER_SESSION_ATTRIBUTE, httpRequest
                                .getHeader(HttpHeaders.REFERER));
                    }
                }
            }
            getFilter().doFilter(request, response, chain);
        }

        @Override
        public void destroy() {
            getFilter().destroy();
        }
    }

    public static class SsoClientResources {

        /**
         * Hostname for OAuth endpoints, if not well-known.
         */
        private String hostname;

        @NestedConfigurationProperty
        private final AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private final ResourceServerProperties resource = new ResourceServerProperties();

        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        public ResourceServerProperties getResource() {
            return resource;
        }

        public String getHostname() {
            return hostname;
        }

        @SuppressWarnings("unused")
        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }

    public static class SsoServices {

        private final boolean isFacebookEnabled;
        private final boolean isGoogleEnabled;
        private final boolean isClimateXEnabled;

        public SsoServices(boolean isFacebookEnabled, boolean isGoogleEnabled,
                boolean isClimateXEnabled) {
            this.isFacebookEnabled = isFacebookEnabled;
            this.isGoogleEnabled = isGoogleEnabled;
            this.isClimateXEnabled = isClimateXEnabled;
        }

        public boolean isFacebookEnabled() {
            return isFacebookEnabled;
        }

        public boolean isGoogleEnabled() {
            return isGoogleEnabled;
        }

        public boolean isClimateXEnabled() {
            return isClimateXEnabled;
        }
    }
}
