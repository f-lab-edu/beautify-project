package com.beautify_project.bp_security.handler;

import com.beautify_project.bp_security.config.properties.OAuth2LoginConfigProperties;
import com.beautify_project.bp_security.dto.AccessTokenDto;
import com.beautify_project.bp_security.dto.BPOAuth2User;
import com.beautify_project.bp_security.provider.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final OAuth2LoginConfigProperties properties;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
        final HttpServletResponse response, final Authentication authentication)
        throws IOException, ServletException {

        log.debug("CustomOAUth2SuccessHandler Called, URI: {}", request.getRequestURI());

        final BPOAuth2User oAuth2User = (BPOAuth2User) authentication.getPrincipal();
        final String userEmail = oAuth2User.getName();
        final Map<String, Object> claims = createClaims(oAuth2User);

        final AccessTokenDto accessTokenDto = jwtProvider.generate(userEmail, claims);

        log.debug("Access token: {}", accessTokenDto);
        final String redirectUrl = properties.successRedirectionUrl();
        response.sendRedirect(
            redirectUrl.replace("{token}", accessTokenDto.accessToken()));
    }

    private static Map<String, Object> createClaims(final BPOAuth2User oAuth2User) {
        return Map.of(
            "user_role", oAuth2User.getRole(),
            "auth_type", oAuth2User.getAuthType()
        );
    }
}
