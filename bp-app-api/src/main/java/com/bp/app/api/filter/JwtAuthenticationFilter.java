package com.bp.app.api.filter;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.provider.JwtProvider;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.utils.Validator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BEARER = "BEARER ";

    private final JwtProvider jwtProvider;
    private final MemberAdapterRepository memberAdapterRepository;

    @Override
    protected void doFilterInternal(@NotNull final HttpServletRequest request,
        @NotNull final HttpServletResponse response, @NotNull final FilterChain filterChain)
        throws ServletException, IOException {

        final String token = extractBearerToken(request);
        if (token == null) {
            log.debug("Token is null from request uri - {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String userEmail = jwtProvider.parseSubject(token);
        if (userEmail == null) {
            log.debug("UserEmail is null from request uri - {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final Member member = memberAdapterRepository.findByEmail(userEmail)
            .orElseThrow(() -> new BpCustomException(ErrorCode.ME001));
        final UserRole role = member.getRole();

        final List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + role.name()));

        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        final AbstractAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userEmail, null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        final String authorization = request.getHeader(AUTHORIZATION_HEADER_KEY);
        if (Validator.isEmptyOrBlank(authorization)) {
            log.debug("authorization header is not exist");
            return null;
        }

        if (StringUtils.startsWith(authorization, BEARER)) {
            log.debug("authorization header is not BEARER type");
            return null;
        }

        return authorization.substring(BEARER.length());
    }
}
