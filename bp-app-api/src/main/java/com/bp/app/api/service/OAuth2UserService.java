package com.bp.app.api.service;

import com.bp.app.api.dto.BPOAuth2User;
import com.bp.app.api.utils.EncryptionUtils;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    public static final String AUTH_INFO_PARSE_KEY_KAKAO = "kakao_account";
    public static final String AUTH_INFO_PARSE_KEY_NAVER = "response";
    private static final String DEFAULT_PASSWORD = "DEFAULT_PASSWORD";

    private final MemberAdapterRepository memberAdapterRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            log.debug(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (Exception e) {
            log.error("", e);
        }

        final AuthType authType = extractAuthType(userRequest);
        final String email = parseUserEmail(oAuth2User, authType);

        registerAsMemberIfNewUser(email, authType);

        return BPOAuth2User.of(email, authType, UserRole.USER);
    }

    private AuthType extractAuthType(final OAuth2UserRequest userRequest) {
        return AuthType.from(userRequest.getClientRegistration().getClientName());
    }

    @SuppressWarnings("unchecked")
    private String parseUserEmail(final OAuth2User oAuth2User, final AuthType authType) {

        Map<String, Object> authInfo;
        if (authType == AuthType.KAKAO) {
            authInfo = (Map<String, Object>) oAuth2User.getAttributes().get(
                AUTH_INFO_PARSE_KEY_KAKAO);
        } else if (authType == AuthType.NAVER) {
            authInfo = (Map<String, Object>) oAuth2User.getAttributes().get(
                AUTH_INFO_PARSE_KEY_NAVER);
        } else {
            throw new UnsupportedOperationException("지원하지 않는 인증 방식입니다.");
        }

        return (String) authInfo.get("email");
    }

    private void registerAsMemberIfNewUser(final String email, final AuthType authType) {
        if (isNewUser(email)) {
            log.debug("User {} does not exist. Register {} ", email, email);
            memberAdapterRepository.save(createNewThirdPartyAuthMember(email, authType));
        }
    }

    private boolean isNewUser(final String userEmail) {
        return !memberAdapterRepository.existsByEmail(userEmail);
    }

    public Member createNewThirdPartyAuthMember(final String email, AuthType authType) {
        return Member.newMember(email, EncryptionUtils.encodeBCrypt(DEFAULT_PASSWORD), null, null,
            authType, UserRole.USER, MemberStatus.NEED_TO_SIGN_UP, System.currentTimeMillis());
    }
}
