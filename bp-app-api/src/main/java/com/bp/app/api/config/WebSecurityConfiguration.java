package com.bp.app.api.config;

import com.bp.app.api.config.properties.OAuth2LoginConfigProperties;
import com.bp.app.api.filter.JwtAuthenticationFilter;
import com.bp.app.api.handler.CustomOAuth2SuccessHandler;
import com.bp.app.api.response.ErrorResponseMessage;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfiguration {

    private static final String[] WHITE_LIST_URLS = new String[]{
        "/actuator", "/v1/members/user", "/v1/auth/**", "/oauth2/**"
    };
    private static final String[] USER_ROLE_URLS = new String[]{
        "/v1/shops/**", "/v1/reviews/**", "/v1/user/**"
    };
    private static final String[] OWNER_ROLE_URLS = new String[] {
        "/v1/owner/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2LoginConfigProperties oauth2LoginConfigProperties;
    private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )
            .csrf(
                CsrfConfigurer::disable) // cookie 를 사용하지 않으면 꺼도 되고, cookie 를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.
            .httpBasic(HttpBasicConfigurer::disable) // 기본 인증 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(request -> request
                .requestMatchers(WHITE_LIST_URLS).permitAll() // while_list
                .requestMatchers(USER_ROLE_URLS).hasAnyRole(UserRole.USER.name(), UserRole.OWNER.name()) // 권한으로 접근 제어
                .requestMatchers(OWNER_ROLE_URLS).hasAnyRole(UserRole.OWNER.name())
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint.baseUri(
                    oauth2LoginConfigProperties.authorizationEndpoint()))
                .redirectionEndpoint(endpoint -> endpoint.baseUri(
                    oauth2LoginConfigProperties.redirectedEndpoint()))
                .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new FailedAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    static class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

        private static final String ERROR_RESPONSE_MESSAGE;

        static {
            try {
                ERROR_RESPONSE_MESSAGE = new ObjectMapper().writeValueAsString(
                    ErrorResponseMessage.createErrorMessage(ErrorCode.UA001));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException authException) throws IOException {
            log.error("", authException);
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(ERROR_RESPONSE_MESSAGE);
        }
    }

    static class CustomAccessDeniedHandler implements AccessDeniedHandler {

        private static final String ERROR_RESPONSE_MESSAGE;

        static {
            try {
                ERROR_RESPONSE_MESSAGE = new ObjectMapper().writeValueAsString(
                    ErrorResponseMessage.createErrorMessage(ErrorCode.FB001));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void handle(final HttpServletRequest request, final HttpServletResponse response,
            final AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
            log.error("", accessDeniedException);
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(ERROR_RESPONSE_MESSAGE);
        }
    }
}
