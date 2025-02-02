package com.bp.app.api.helper;

import com.bp.app.api.provider.JwtProvider;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationHelper {

    private final JwtProvider jwtProvider;
    private final MemberAdapterRepository memberAdapterRepository;

    public String provideOwnerRoleAccessToken(final String ownerEmail) {
        if (!memberAdapterRepository.existsByEmail(ownerEmail)) {
            memberAdapterRepository.saveAndFlush(Member.newMember(ownerEmail, "password",
                "owner", "1111", AuthType.BP, UserRole.OWNER, MemberStatus.ACTIVE,
                System.currentTimeMillis()));
        }

        final Map<String, Object> ownerClaims = Map.of(
            "user_role", UserRole.OWNER,
            "auth_type", AuthType.BP
        );

        return jwtProvider.generate(ownerEmail, ownerClaims).accessToken();
    }

    public String provideUserRoleAccessToken(final String userEmail) {
        if (!memberAdapterRepository.existsByEmail(userEmail)) {
            memberAdapterRepository.saveAndFlush(Member.newMember(userEmail, "password",
                "user", "1111", AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
                System.currentTimeMillis()));
        }

        final Map<String, Object> ownerClaims = Map.of(
            "user_role", UserRole.USER,
            "auth_type", AuthType.BP);

        return jwtProvider.generate(userEmail, ownerClaims).accessToken();
    }
}
