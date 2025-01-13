package com.beautify_project.bp_security.provider;

import com.beautify_project.bp_security.config.properties.JwtConfigProperties;
import com.beautify_project.bp_security.dto.AccessTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String DEFAULT_ISSUER = "BP";
    private static final String GRANT_TYPE = "BEARER";
    private static final long MINUTE = 1000 * 60;

    private final JwtConfigProperties jwtConfigProperties;
    private final Key key;

    public JwtProvider(final JwtConfigProperties jwtConfigProperties) {
        this.jwtConfigProperties = jwtConfigProperties;
        this.key = Keys.hmacShaKeyFor(
            jwtConfigProperties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    public AccessTokenDto generate(final String memberEmail, final Map<String, Object> claims) {

        long now = System.currentTimeMillis();

        final long accessTokenExpiresIn =
            now + (MINUTE * jwtConfigProperties.accessTokenExpiredMinute());
        final long refreshTokenExpiresIn =
            now + (MINUTE * jwtConfigProperties.refreshTokenExpiredMinute());

        final String accessToken = Jwts.builder()
            .subject(memberEmail)
            .issuer(DEFAULT_ISSUER)
            .issuedAt(new Date())
            .expiration(new Date(accessTokenExpiresIn))
            .signWith(key, SignatureAlgorithm.HS256)
            .claims(claims)
            .compact();

        final String refreshToken = Jwts.builder()
            .claims(claims)
            .expiration(new Date(refreshTokenExpiresIn))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return new AccessTokenDto(GRANT_TYPE, accessToken, accessTokenExpiresIn, refreshToken,
            refreshTokenExpiresIn);
    }

    public String parseSubject(final String jwt) {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt)
            .getBody()
            .getSubject();
    }

    private Claims parseClaims(final String accessToken) {
        try {
            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException exception) {
            return exception.getClaims();
        }
    }
}
