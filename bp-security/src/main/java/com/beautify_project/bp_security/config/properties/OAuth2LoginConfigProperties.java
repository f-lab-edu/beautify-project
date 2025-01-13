package com.beautify_project.bp_security.config.properties;

import com.beautify_project.bp_utils.Validator;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2-login")
public record OAuth2LoginConfigProperties(String authorizationEndpoint, String redirectedEndpoint,
                                          String successRedirectionUrl) {

    public OAuth2LoginConfigProperties(final String authorizationEndpoint,
        final String redirectedEndpoint, final String successRedirectionUrl) {
        this.authorizationEndpoint = authorizationEndpoint;
        this.redirectedEndpoint = redirectedEndpoint;
        this.successRedirectionUrl = successRedirectionUrl;
        validate();
    }

    private void validate() {
        if (Validator.isEmptyOrBlank(authorizationEndpoint) || Validator.isEmptyOrBlank(
            redirectedEndpoint) || Validator.isEmptyOrBlank(successRedirectionUrl)) {
            throw new IllegalStateException("oauth2-login 관련 설정값이 유효하지 않습니다");
        }
    }
}
