package com.bp.app.api.dto;

import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BPOAuth2User implements OAuth2User {

    private String email;
    private AuthType authType;
    private UserRole role;

    private BPOAuth2User(final String email, final AuthType authType, final UserRole role) {
        this.email = email;
        this.authType = authType;
        this.role = role;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return this.email;
    }

    public static BPOAuth2User of(final String email, final AuthType authType, final UserRole role) {
        return new BPOAuth2User(email, authType, role);
    }
}
